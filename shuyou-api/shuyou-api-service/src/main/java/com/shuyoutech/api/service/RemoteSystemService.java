package com.shuyoutech.api.service;

import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.api.model.RemoteSysFile;

import java.util.Map;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 15:49
 **/
public interface RemoteSystemService {

    LoginUser getUserByUsername(String username);

    Boolean passwordMatch(String rawPassword, String encodedPassword);

    Map<String, String> getUserName(Set<String> userIds);

    Map<String, String> translateByDictCode(String dictCode);

    Set<String> getMenuPermission(String userId);

    Set<String> getRolePermission(String userId);

    RemoteSysFile getFileById(String fileId);

    String getFilePath(String fileId);

    String generatedUrl(String ossId, Long expiration);

    RemoteSysFile upload(String originalFilename, byte[] data);
}
