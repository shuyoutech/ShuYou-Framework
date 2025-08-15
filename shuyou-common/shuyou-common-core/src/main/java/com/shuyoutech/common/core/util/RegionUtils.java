package com.shuyoutech.common.core.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HtmlUtil;
import com.shuyoutech.common.core.constant.NumberConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.util.List;

/**
 * 根据ip地址定位工具类，离线方式
 *
 * @author YangChao
 * @date 2025-07-21 13:48
 **/
@Slf4j
public class RegionUtils {

    public static Searcher IP_SEARCHER;

    static {
        try {
            byte[] cBuff = ResourceUtil.readBytes("db/ip2region.xdb");
            IP_SEARCHER = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            log.error("ip2region init error:{}", e.getMessage());
        }
    }

    /**
     * 根据IP地址离线获取城市
     *
     * @param ip 地址
     * @return 城市
     */
    public static String getCity(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                return "";
            }
            if (StringUtils.containsIgnoreCase(ip, "0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            } else {
                ip = HtmlUtil.cleanHtmlTag(ip);
            }
            if (NetUtil.isInnerIP(ip)) {
                return "内网IP";
            }
            // region = 国家|区域|省份|城市|ISP --> 中国|0|江苏省|苏州市|电信
            String region = IP_SEARCHER.search(ip);
            log.info("getCity ================ ip:{},region:{}", ip, region);
            if (StringUtils.isBlank(region)) {
                return "";
            }
            List<String> arrList = StringUtils.split(region, StringConstants.VERTICAL);
            if (NumberConstants.FOUR == arrList.size()) {
                if (!"0".equals(arrList.get(3))) {
                    return arrList.get(3);
                } else if (!"0".equals(arrList.get(2))) {
                    return arrList.get(2);
                } else {
                    return arrList.getFirst();
                }
            } else {
                return region.replace("0|", "").replace("|0", "");
            }
        } catch (Exception e) {
            log.error("getCity =============== ip:{},exception:{}", ip, e.getMessage());
        }
        return "";
    }

}
