package com.shuyoutech.common.core.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author YangChao
 * @date 2025-07-06 17:22
 **/
@Slf4j
public class MessageSourceUtils {

    /**
     * 获取消息来源 由本机IP和端口组成唯一值
     *
     * @return 唯一值
     */
    public static String getMsgSource() {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Environment env = SpringUtil.getBean(Environment.class);
            String port = env.getProperty("server.port");
            return host + ":" + port;
        } catch (UnknownHostException e) {
            log.error("getMsgSource =============== exception:{}", e.getMessage());
        }
        return "";
    }

}
