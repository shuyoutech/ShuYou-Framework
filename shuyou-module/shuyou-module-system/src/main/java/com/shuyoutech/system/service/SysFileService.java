package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysFileBo;
import com.shuyoutech.system.domain.entity.SysFileEntity;
import com.shuyoutech.system.domain.vo.SysFileVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YangChao
 * @date 2023-07-10 22:32
 **/
public interface SysFileService extends SuperService<SysFileEntity, SysFileVo> {

    Query buildQuery(SysFileBo bo);

    PageResult<SysFileVo> page(PageQuery<SysFileBo> pageQuery);

    /**
     * 文件上传
     *
     * @param file 上传文件对象
     * @return 文件存储对象
     */
    SysFileVo upload(MultipartFile file);

    /**
     * 文件上传
     *
     * @param originalFilename 文件名称
     * @param data             文件数据
     * @return 文件对象
     */
    SysFileVo upload(String originalFilename, byte[] data);

    /**
     * 根据ID删除文件
     *
     * @param fileId 文件id
     */
    void deleteFileById(String fileId);

    /**
     * 下载文件
     *
     * @param fileId   文件id
     * @param request  请求对象
     * @param response 返回对象
     */
    void down(String fileId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 生成预览在线地址
     *
     * @param ossId 文件id
     * @return 地址
     */
    String generatedUrl(String ossId);

    /**
     * 生成预览在线地址
     *
     * @param ossId      文件id
     * @param expiration 失效时间
     * @return 地址
     */
    String generatedUrl(String ossId, Long expiration);

}
