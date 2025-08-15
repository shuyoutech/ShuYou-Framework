package com.shuyoutech.common.core.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-25 09:33
 **/
public class TemplateUtils extends TemplateUtil {

    /**
     * 字符串模板渲染内容，使用 ${name} 占位
     *
     * @param content 字符串模板内容
     * @param params  参数
     * @return 字符串
     */
    public static String format(String content, Map<String, Object> params) {
        if (StringUtils.isBlank(content) || MapUtils.isEmpty(params)) {
            return content;
        }
        Dict dict = Dict.create();
        dict.putAll(params);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());
        Template template = engine.getTemplate(content);
        return template.render(dict);
    }

}
