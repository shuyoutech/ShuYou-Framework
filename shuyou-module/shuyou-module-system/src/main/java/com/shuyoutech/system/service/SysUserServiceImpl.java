package com.shuyoutech.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import com.shuyoutech.system.domain.bo.SysUserBo;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysUserVo;
import com.shuyoutech.system.enums.DictTypeEnum;
import com.shuyoutech.system.enums.FileContentTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author YangChao
 * @date 2025-02-14 16:18:57
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends SuperServiceImpl<SysUserEntity, SysUserVo> implements SysUserService {

    @Value("${shuyoutech.storage.upload-dir}")
    private String filePath;

    @Override
    public List<SysUserVo> convertTo(List<SysUserEntity> list) {
        List<SysUserVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> statusMap = cachePlusService.translateByDictCode(DictTypeEnum.STATUS_TYPE.getValue());
        Map<String, String> sexMap = cachePlusService.translateByDictCode(DictTypeEnum.SEX_TYPE.getValue());
        Set<String> orgIds = CollectionUtils.newHashSet();
        CollectionUtils.addAll(orgIds, StreamUtils.toSet(list, SysUserEntity::getOrgId));
        Map<String, String> orgMap = cachePlusService.translateOrgName(orgIds);
        Set<String> roleIds = list.stream().filter(f -> CollectionUtils.isNotEmpty(f.getRoleIds())).flatMap(f -> f.getRoleIds().stream()).collect(Collectors.toSet());
        Set<String> postIds = list.stream().filter(f -> CollectionUtils.isNotEmpty(f.getPostIds())).flatMap(f -> f.getPostIds().stream()).collect(Collectors.toSet());
        Map<String, String> roleMap = cachePlusService.translateRoleName(roleIds);
        Map<String, String> postMap = cachePlusService.translatePostName(postIds);
        list.forEach(e -> {
            SysUserVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setStatusName(MapUtils.getStr(statusMap, e.getStatus(), ""));
                vo.setPassword(DesensitizedUtil.password(e.getPassword()));
                vo.setOrgName(MapUtils.getStr(orgMap, e.getOrgId(), ""));
                vo.setSexName(MapUtils.getStr(sexMap, e.getSex(), ""));
                vo.setRoleNames(CollectionUtils.translate(e.getRoleIds(), roleMap));
                vo.setPostNames(CollectionUtils.translate(e.getPostIds(), postMap));
                result.add(vo);
            }
        });
        return result;
    }

    @Override
    public SysUserVo convertTo(SysUserEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(SysUserBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getStatus())) {
            query.addCriteria(Criteria.where("status").is(bo.getStatus()));
        }
        if (StringUtils.isNotBlank(bo.getUsername())) {
            query.addCriteria(Criteria.where("username").regex(Pattern.compile(String.format("^.*%s.*$", bo.getUsername()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getNickname())) {
            query.addCriteria(Criteria.where("nickname").regex(Pattern.compile(String.format("^.*%s.*$", bo.getNickname()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getRealName())) {
            query.addCriteria(Criteria.where("realName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getRealName()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getPhone())) {
            query.addCriteria(Criteria.where("phone").regex(Pattern.compile(String.format("^.*%s.*$", bo.getPhone()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getEmail())) {
            query.addCriteria(Criteria.where("email").regex(Pattern.compile(String.format("^.*%s.*$", bo.getEmail()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getRoleId())) {
            query.addCriteria(Criteria.where("roleIds").is(bo.getRoleId()));
        }
        if (StringUtils.isNotBlank(bo.getNeRoleId())) {
            query.addCriteria(Criteria.where("roleIds").ne(bo.getNeRoleId()));
        }
        if (StringUtils.isNotBlank(bo.getOrgId())) {
            query.addCriteria(Criteria.where("orgId").is(bo.getOrgId()));
        }
        if (CollectionUtils.isNotEmpty(bo.getOrgIds())) {
            query.addCriteria(Criteria.where("orgId").in(bo.getOrgIds()));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        SysUserEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<SysUserVo> page(PageQuery<SysUserBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public SysUserVo detail(String id) {
        SysUserEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveSysUser(SysUserBo bo) {
        String password = bo.getPassword();
        if (StringUtils.isNotBlank(password)) {
            bo.setPassword(passwordEncoder.encode(password));
        }
        if (StringUtils.isNotBlank(bo.getStatus())) {
            bo.setStatus(StatusEnum.ENABLE.getValue());
        }
        SysUserEntity entity = this.save(bo);
        return null == entity ? null : entity.getId();
    }

    @Override
    public boolean updateSysUser(SysUserBo bo) {
        Update update = new Update();
        update.set("username", bo.getUsername());
        update.set("realName", bo.getRealName());
        update.set("nickname", bo.getNickname());
        update.set("orgId", bo.getOrgId());
        update.set("phone", bo.getPhone());
        update.set("email", bo.getEmail());
        update.set("sex", bo.getSex());
        update.set("address", bo.getAddress());
        if (StringUtils.isNotBlank(bo.getAvatar())) {
            update.set("avatar", bo.getAvatar());
        }
        return MongoUtils.patch(bo.getId(), update, SysUserEntity.class);
    }

    @Override
    public boolean deleteSysUser(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusSysUser(String id, String status) {
        SysUserEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, SysUserEntity.class);
    }

    @Override
    public Boolean passwordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public JSONObject importUser(MultipartFile file, HttpServletRequest request) {
        int successCount = 0;
        int failCount = 0;
        List<String> messageList = CollectionUtil.newArrayList();
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<List<Object>> readAll = reader.read();
            if (CollectionUtil.isEmpty(readAll) || readAll.size() <= 1) {
                throw new BusinessException("文件内容为空!");
            }
            // 查询所有的人员信息缓存起来进行校验
            List<SysUserEntity> userList = this.selectList();
            Set<String> usernameSet = CollectionUtil.newHashSet();
            Set<String> mobileSet = CollectionUtil.newHashSet();
            if (CollectionUtil.isNotEmpty(userList)) {
                for (SysUserEntity user : userList) {
                    if (StringUtil.isNotBlank(user.getUsername())) {
                        usernameSet.add(user.getUsername());
                    }
                    if (StringUtil.isNotBlank(user.getPhone())) {
                        mobileSet.add(user.getPhone());
                    }
                }
            }
            int row = 2;
            boolean successFlag;
            SysUserEntity user;
            List<SysUserEntity> insertList = CollectionUtil.newArrayList();
            String username;
            String nickname;
            String realName;
            String phone;
            String email;
            String sex;
            String address;
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < readAll.size(); i++) {
                message.setLength(0);
                successFlag = true;
                user = new SysUserEntity();
                // 用户名,用户昵称,真实姓名,手机号码,电子邮箱,性别,地址
                if (readAll.get(i).size() < 7) {
                    continue;
                }
                username = StringUtil.toString(readAll.get(i).get(0));
                nickname = StringUtil.toString(readAll.get(i).get(1));
                realName = StringUtil.toString(readAll.get(i).get(2));
                phone = StringUtil.toString(readAll.get(i).get(3));
                email = StringUtil.toString(readAll.get(i).get(4));
                sex = StringUtil.toString(readAll.get(i).get(5));
                address = StringUtil.toString(readAll.get(i).get(6));
                if (StringUtil.isEmpty(username)) {
                    message.append("用户名不能为空");
                    successFlag = false;
                } else {
                    if (usernameSet.contains(username)) {
                        message.append("用户名:").append(username).append(" 已存在");
                        successFlag = false;
                    }
                }
                if (StringUtil.isEmpty(nickname)) {
                    message.append("用户昵称不能为空");
                    successFlag = false;
                }
                if (StringUtil.isEmpty(realName)) {
                    message.append("用户姓名不能为空");
                    successFlag = false;
                }
                if (StringUtil.isEmpty(phone)) {
                    message.append("手机号不能为空");
                    successFlag = false;
                } else {
                    if (mobileSet.contains(phone)) {
                        message.append("手机号:").append(phone).append(" 已存在");
                        successFlag = false;
                    }
                }
                user.setCreateTime(new Date());
                user.setStatus(StatusEnum.ENABLE.getValue());
                user.setUsername(username);
                user.setNickname(nickname);
                user.setRealName(realName);
                user.setPhone(phone);
                user.setPassword(passwordEncoder.encode(phone));
                user.setEmail(email);
                user.setSex(sex);
                user.setAddress(address);
                if (successFlag) {
                    successCount++;
                    insertList.add(user);
                    usernameSet.add(username);
                    mobileSet.add(phone);
                } else {
                    message.insert(0, StringUtils.format("第 {} 行：失败！", row));
                    failCount++;
                    messageList.add(message.toString());
                }
                row++;
            }
            if (CollectionUtil.isNotEmpty(insertList)) {
                this.saveBatch(insertList);
            }
            messageList.add(StringUtils.format("成功 {} 条, 失败 {} 条", successCount, failCount));
        } catch (Exception e) {
            log.error("importUser ==================== exception:{}", e.getMessage());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("successCount", successCount);
        jsonObject.put("failCount", failCount);
        jsonObject.put("message", CollectionUtil.join(messageList, "\n"));
        return jsonObject;
    }

    @Override
    public void export(SysUserBo userQuery, HttpServletRequest request, HttpServletResponse response) {
        Query query = buildQuery(userQuery);
        List<SysUserEntity> userList = selectList(query);
        if (CollectionUtils.isEmpty(userList)) {
            throw new BusinessException("查询数据为空");
        }
        try {
            List<Map<String, Object>> rows = CollectionUtil.newArrayList();
            Map<String, Object> map;
            for (SysUserEntity user : userList) {
                map = new LinkedHashMap<>();
                map.put("用户ID", user.getId());
                map.put("用户名", user.getUsername());
                map.put("用户昵称", user.getNickname());
                map.put("用户姓名", user.getRealName());
                map.put("用户邮箱", user.getEmail());
                map.put("手机号码", user.getPhone());
                map.put("用户性别", user.getSex());
                map.put("用户地址", user.getAddress());
                rows.add(map);
            }
            // 通过工具类创建writer
            String parentFilePath = filePath + File.separator + DateUtil.today();
            FileUtil.mkdir(parentFilePath);
            String fileName = parentFilePath + File.separator + IdUtil.simpleUUID() + ".xlsx";
            ExcelWriter writer = ExcelUtil.getWriter(fileName);
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(rows, true);
            // 关闭writer，释放内存
            writer.close();

            // 读取文件并进行导出
            InputStream inputStream = FileUtil.getInputStream(fileName);
            String encodeFileName = FileUtils.encodeFileName(request, "用户列表.xlsx");
            FileUtils.setAttachmentResponseHeader(response, encodeFileName);
            response.setContentType(FileContentTypeEnum.XLSX.getLabel());
            response.setHeader("Content-Length", String.valueOf(inputStream.available()));
            IoUtil.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("export ================ exception:{}", e.getMessage());
        }
    }

    @Override
    public void resetPassword(String userId, String password) {
        Update update = new Update();
        update.set("password", passwordEncoder.encode(password));
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    @Override
    public void grantRole(String userId, Set<String> roleIds) {
        Update update = new Update();
        update.set("roleIds", roleIds);
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    @Override
    public void grantPost(String userId, Set<String> postIds) {
        Update update = new Update();
        update.set("postIds", postIds);
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    private final PasswordEncoder passwordEncoder;
    private final CachePlusService cachePlusService;

}