package com.shuyoutech.common.core.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Validator;
import com.shuyoutech.common.core.constant.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

import java.util.List;

/**
 * <a href="https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md">...</a>
 * 根据ip地址定位工具类，离线方式
 *
 * @author YangChao
 * @date 2025-07-21 13:48
 **/
@Slf4j
public class RegionUtils {

    public static Searcher IP_SEARCHER_V4;
    public static Searcher IP_SEARCHER_V6;

    static {
        try {
            byte[] cBuff4 = ResourceUtil.readBytes("db/ip2region_v4.xdb");
            LongByteArray byteArray4 = new LongByteArray(cBuff4);
            IP_SEARCHER_V4 = Searcher.newWithBuffer(Version.IPv4, byteArray4);

            byte[] cBuff6 = ResourceUtil.readBytes("db/ip2region_v6.xdb");
            LongByteArray byteArray6 = new LongByteArray(cBuff6);
            IP_SEARCHER_V6 = Searcher.newWithBuffer(Version.IPv6, byteArray6);
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
            String region = "";
            if (Validator.isIpv4(ip)) {
                region = IP_SEARCHER_V4.search(ip);
            } else if (Validator.isIpv6(ip)) {
                region = IP_SEARCHER_V6.search(ip);
            }
            return region;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 根据IP地址离线获取城市
     *
     * @param ip 地址
     * @return 城市
     */
    public static String getCity(String ip) {
        try {
            String location = getLocation(ip);
            if (StringUtils.isBlank(location)) {
                return "";
            }
            List<String> list = CollectionUtils.newArrayList();
            List<String> arrList = StringUtils.split(location, StringConstants.VERTICAL);
            for (String str : arrList) {
                if (StringUtils.isBlank(str) || "0".equals(str)) {
                    continue;
                }
                list.add(str);
            }
            if (list.size() == 4) {
                list.removeLast();
            }
            return CollectionUtils.join(list, " ");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

}
