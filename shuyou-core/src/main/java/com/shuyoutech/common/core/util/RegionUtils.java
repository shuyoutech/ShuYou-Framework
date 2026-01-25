package com.shuyoutech.common.core.util;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.beans.factory.DisposableBean;

import java.io.InputStream;

/**
 * <a href="https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md">...</a>
 * 根据ip地址定位工具类，离线方式
 *
 * @author YangChao
 * @since 2025-07-21 13:48
 **/
@Slf4j
public class RegionUtils implements DisposableBean {

    public static Ip2Region IP2_REGION;

    static {
        try {
            InputStream stream4 = ResourceUtil.getStream("db/ip2region_v4.xdb");
            InputStream stream6 = ResourceUtil.getStream("db/ip2region_v6.xdb");

            // 1, 创建 v4 的配置：指定缓存策略和 v4 的 xdb 文件路径
            final Config v4Config = Config.custom().setCachePolicy(Config.BufferCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    .setXdbInputStream(stream4)             // 设置 v4 xdb 文件的 inputstream 对象
                    .setXdbPath("ip2region v4 xdb path")    // 设置 v4 xdb 文件的路径
                    .asV4();    // 指定为 v4 配置

            // 2, 创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径
            final Config v6Config = Config.custom().setCachePolicy(Config.BufferCache)     // 指定缓存策略: NoCache / VIndexCache / BufferCache
                    .setSearchers(15)                       // 设置初始化的查询器数量
                    .setXdbInputStream(stream6)             // 设置 v6 xdb 文件的 inputstream 对象
                    .setXdbPath("ip2region v6 xdb path")    // 设置 v6 xdb 文件的路径
                    .asV6();    // 指定为 v6 配置

            // 3，通过上述配置创建 Ip2Region 查询服务
            IP2_REGION = Ip2Region.create(v4Config, v6Config);

            // 4，导出 ip2region 服务作为全局变量，进行双版本的IP地址的并发查询，例如：
            // final String v4Region = ip2Region.search("113.92.157.29");                          // 进行 IPv4 查询
            // final String v6Region = ip2Region.search("240e:3b7:3272:d8d0:db09:c067:8d59:539e"); // 进行 IPv6 查询
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据IP地址离线获取地理位置
     *
     * @param ip 地址
     * @return 城市
     */
    public static String getLocation(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                return "";
            }
            // 中国|广东省|深圳市|家庭宽带
            // region = 国家|区域|省份|城市|ISP --> 中国|0|江苏省|苏州市|电信
            return IP2_REGION.search(ip);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    @Override
    public void destroy() throws Exception {
        if (null != IP2_REGION) {
            IP2_REGION.close();
        }
    }
}
