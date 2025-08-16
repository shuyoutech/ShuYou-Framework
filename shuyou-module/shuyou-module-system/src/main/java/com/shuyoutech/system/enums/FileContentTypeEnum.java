package com.shuyoutech.system.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模版文件枚举
 *
 * @author YangChao
 * @date 2025-07-05 20:18
 **/
@Getter
@AllArgsConstructor
public enum FileContentTypeEnum implements BaseEnum<String, String> {

    XLS("xls", "application/vnd.ms-excel;charset=UTF-8"),

    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"),

    DOC("doc", "application/msword;charset=UTF-8"),

    DOCX("docx", "application/vnd.openxm|formats-officedocument.wordprocessingml.document;charset=UTF-8"),

    PPT("ppt", "application/vnd.ms-powerpoint;charset=UTF-8");

    private final String value;
    private final String label;

}
