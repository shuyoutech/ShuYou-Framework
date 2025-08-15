package com.shuyoutech.common.core.util;

import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-07 16:39
 **/
@Slf4j
public class JwtUtils {

    /**
     * 获取私钥
     *
     * @return 私钥
     */
    public static SecretKey getSecretKey() {
        byte[] encodeKey = Decoders.BASE64.decode(CommonConstants.JWT_SECRET);
        return Keys.hmacShaKeyFor(encodeKey);
    }

    /**
     * 根据用户ID生成token
     *
     * @param userId 用户ID
     * @return token
     */
    public static String generateToken(String userId) {
        Map<String, Object> map = MapUtils.newHashMap();
        map.put(CommonConstants.USER_ID, userId);
        SecretKey secretKey = getSecretKey();
        Date expireTime = new Date(System.currentTimeMillis() + CommonConstants.EXPIRE_TIME);
        return Jwts.builder().claims(map).subject(userId).issuedAt(new Date()).expiration(expireTime).signWith(secretKey).compact();
    }

    /**
     * 解析token信息
     *
     * @param request 请求对象
     * @return token
     */
    public static String parseToken(HttpServletRequest request) {
        String token = request.getHeader(CommonConstants.HEADER_TOKEN);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = request.getHeader(CommonConstants.HEADER_AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (token.startsWith(CommonConstants.HEADER_AUTHORIZATION_PREFIX)) {
            return token.replaceFirst(CommonConstants.HEADER_AUTHORIZATION_PREFIX, StringConstants.EMPTY);
        }
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        try {
            SecretKey secretKey = getSecretKey();
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            log.error("parseToken ======================= token:{},exception:{}", token, e.getMessage());
        }
        return null;
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static String getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            if (null == claims) {
                log.error("getUserId ======================= token:{} claims is null", token);
                return null;
            }
            return claims.get(CommonConstants.USER_ID, String.class);
        } catch (Exception e) {
            log.error("getUserId ======================= token:{},exception:{}", token, e.getMessage());
        }
        return null;
    }

}
