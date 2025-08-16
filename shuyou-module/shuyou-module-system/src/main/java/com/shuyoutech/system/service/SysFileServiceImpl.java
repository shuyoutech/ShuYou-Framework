package com.shuyoutech.system.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.shuyoutech.common.core.constant.DateConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.common.web.util.JakartaServletUtils;
import com.shuyoutech.system.config.FileStorageProperties;
import com.shuyoutech.system.domain.bo.SysFileBo;
import com.shuyoutech.system.domain.entity.SysFileEntity;
import com.shuyoutech.system.domain.vo.SysFileVo;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2023-07-10 22:41
 **/
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends SuperServiceImpl<SysFileEntity, SysFileVo> implements SysFileService {

    private OSS ossClient;
    private String bucketName = null;

    @PostConstruct
    public void init() {
        String accessKey = fileStorageProperties.getAccessKey();
        String secretAccessKey = fileStorageProperties.getSecretKey();
        String endpoint = fileStorageProperties.getEndpoint();
        bucketName = fileStorageProperties.getBucketName();
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretAccessKey);
    }

    @Override
    public Query buildQuery(SysFileBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getFileName())) {
            query.addCriteria(Criteria.where("fileName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getFileName()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getOriginalFileName())) {
            query.addCriteria(Criteria.where("originalFileName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getOriginalFileName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public List<SysFileVo> convertTo(List<SysFileEntity> list) {
        List<SysFileVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        list.forEach(e -> {
            SysFileVo vo = MapstructUtils.convert(e, this.voClass);
            vo.setFileSizeFormat(DataSizeUtil.format(e.getFileSize()));
            result.add(vo);
        });
        return result;
    }

    public SysFileVo convertTo(SysFileEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public PageResult<SysFileVo> page(PageQuery<SysFileBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysFileVo upload(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            return upload(originalFileName, file.getBytes());
        } catch (Exception e) {
            log.error("upload =================== exception:{}", e.getMessage());
        }
        return null;
    }

    @Override
    public SysFileVo upload(String originalFileName, byte[] data) {
        String fileType = FileUtil.extName(originalFileName);
        if (StringUtils.isBlank(fileType)) {
            throw new BusinessException("文件格式有误!");
        }
        try {
            long fileSize = data.length;
            Date date = new Date();
            String fileName = StringUtils.format("{}.{}", IdUtil.getSnowflakeNextIdStr(), fileType);
            String ossFileKey = StringUtils.format("{}/{}/{}", bucketName, DateUtils.format(date, DateConstants.PURE_DATE_FORMAT), fileName);
            String uploadDir = StringUtils.format("{}/{}", fileStorageProperties.getUploadDir(), DateUtils.format(date, DateConstants.PURE_DATE_FORMAT));
            FileUtils.mkdir(uploadDir);
            String filePath = uploadDir + File.separator + fileName;

            SysFileEntity sysFile = new SysFileEntity();
            sysFile.setId(IdUtil.simpleUUID());
            sysFile.setCreateTime(new Date());
            sysFile.setCreateUserId(AuthUtils.getLoginUserId());
            sysFile.setCreateUserName(AuthUtils.getLoginUserName());
            sysFile.setFileName(fileName);
            sysFile.setOriginalFileName(originalFileName);
            sysFile.setOssFileKey(ossFileKey);
            sysFile.setFileSize(fileSize);
            sysFile.setFileType(fileType);
            sysFile.setFilePath(filePath);
            sysFile.setBucketName(bucketName);
            sysFile.setPreviewUrl(StringUtils.format("{}/{}/{}", fileStorageProperties.getPreviewPrefix(), DateUtils.format(date, DateConstants.PURE_DATE_FORMAT), fileName));
            FileUtils.writeBytes(data, filePath);
            String fileHash = SmUtils.sm3(new File(filePath));
            sysFile.setFileHash(fileHash);
            PutObjectRequest request = new PutObjectRequest(bucketName, ossFileKey, new File(filePath));
            ossClient.putObject(request);
            MongoUtils.save(sysFile);
            return MapstructUtils.convert(sysFile, SysFileVo.class);
        } catch (Exception e) {
            log.error("upload ==================== exception:{}", e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteFileById(String fileId) {
        SysFileEntity sysFile = MongoUtils.getById(fileId, SysFileEntity.class);
        if (null == sysFile) {
            return;
        }
        try {
            ossClient.deleteObject(bucketName, sysFile.getOssFileKey());
            FileUtil.del(sysFile.getFilePath());
        } catch (Exception e) {
            log.error("delFile ==================== exception:{}", e.getMessage());
        } finally {
            MongoUtils.deleteById(fileId, SysFileEntity.class);
        }
    }

    @Override
    public void down(String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            SysFileEntity sysFile = getById(id);
            if (null == sysFile) {
                JakartaServletUtils.write(response, 500, "文件数据不存在");
                return;
            }
            String filePath = sysFile.getFilePath();
            String ossFileKey = sysFile.getOssFileKey();
            String originalFileName = sysFile.getOriginalFileName();
            String encodeFileName = FileUtils.encodeFileName(request, originalFileName);
            FileUtils.setAttachmentResponseHeader(response, encodeFileName);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Length", String.valueOf(sysFile.getFileSize()));
            if (FileUtils.exist(filePath)) {
                BufferedInputStream inputStream = FileUtils.getInputStream(filePath);
                response.setHeader("Content-Type", FileUtils.getMimeType(filePath));
                IoUtil.copy(inputStream, response.getOutputStream());
                IoUtil.close(inputStream);
            } else {
                OSSObject ossObject = ossClient.getObject(bucketName, ossFileKey);
                InputStream inputStream = ossObject.getObjectContent();
                ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
                response.setHeader("Content-Type", objectMetadata.getContentType());
                IoUtil.copy(inputStream, response.getOutputStream());
                IoUtil.close(inputStream);
            }
        } catch (Exception e) {
            log.error("down ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public String generatedUrl(String ossId) {
        return generatedUrl(ossId, 86400000L);
    }

    @Override
    public String generatedUrl(String ossId, Long expiration) {
        SysFileEntity sysFile = MongoUtils.getById(ossId, SysFileEntity.class);
        if (null == sysFile) {
            throw new BusinessException("该文件ID没有对应记录");
        }
        String previewUrl = sysFile.getPreviewUrl();
        String fileName = sysFile.getFileName();
        if (StringUtils.isNotBlank(previewUrl) && FileUtils.exist(sysFile.getFilePath())) {
            return previewUrl;
        }
        //Date expDate = new Date(System.currentTimeMillis() + 86400000L);
        //ossClient.generatePresignedUrl(bucketName, sysFile.getStoreFileName(), expDate).toString()
        OSSObject ossObject = ossClient.getObject(bucketName, sysFile.getOssFileKey());
        InputStream inputStream = ossObject.getObjectContent();
        Date date = new Date();
        String uploadDir = StringUtils.format("{}/{}", fileStorageProperties.getUploadDir(), DateUtils.format(date, DateConstants.PURE_DATE_FORMAT));
        FileUtils.mkdir(uploadDir);
        String filePath = StringUtils.isBlank(sysFile.getFilePath()) ? uploadDir + File.separator + fileName : sysFile.getFilePath();
        FileUtils.del(filePath);
        FileUtils.writeFromStream(inputStream, filePath);
        if (StringUtils.isNotBlank(previewUrl)) {
            return previewUrl;
        }
        String url = StringUtils.format("{}/{}/{}", fileStorageProperties.getPreviewPrefix(), DateUtils.format(date, DateConstants.PURE_DATE_FORMAT), fileName);
        Update update = new Update();
        update.set("filePath", filePath);
        update.set("previewUrl", url);
        MongoUtils.patch(ossId, update, SysFileEntity.class);
        return url;
    }

    private final FileStorageProperties fileStorageProperties;

}
