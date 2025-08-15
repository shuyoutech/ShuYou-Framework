package com.shuyoutech.common.core.model;

import com.shuyoutech.common.core.enums.QueryLogicEnum;
import com.shuyoutech.common.core.enums.QuerySymbolEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-04-06 20:49
 **/
@Data
public class ParamTerm implements Serializable {

    @Schema(description = "字段列名")
    private String column;

    @Schema(description = "字段值")
    private Object value;

    @Schema(description = "字段属性类型")
    private Class<?> fieldType;

    @Schema(description = "查询逻辑关系")
    private QueryLogicEnum logic = QueryLogicEnum.AND;

    @Schema(description = "查询条件符号")
    private QuerySymbolEnum symbol = QuerySymbolEnum.EQ;

    @Schema(description = "嵌套的条件")
    private List<ParamTerm> terms = new LinkedList<>();

    public void or(ParamTerm term) {
        term.setLogic(QueryLogicEnum.OR);
        this.terms.add(term);
    }

    public void or(String column, Object value) {
        or(column, QuerySymbolEnum.EQ, value);
    }

    public void or(String column, QuerySymbolEnum symbol, Object value) {
        ParamTerm term = new ParamTerm();
        term.setSymbol(symbol);
        term.setValue(value);
        term.setColumn(column);
        term.setLogic(QueryLogicEnum.OR);
        this.terms.add(term);
    }

    public void and(ParamTerm term) {
        term.setLogic(QueryLogicEnum.AND);
        this.terms.add(term);
    }

    public void and(String column, Object value) {
        and(column, QuerySymbolEnum.EQ, value);
    }

    public void and(String column, QuerySymbolEnum symbol, Object value) {
        ParamTerm term = new ParamTerm();
        term.setLogic(QueryLogicEnum.AND);
        term.setSymbol(symbol);
        term.setColumn(column);
        term.setValue(value);
        this.terms.add(term);
    }

    public void addTerm(ParamTerm term) {
        this.terms.add(term);
    }

    public static ParamTerm build(String column, Object value) {
        return build(column, QuerySymbolEnum.EQ, value);
    }

    public static ParamTerm build(String column, QuerySymbolEnum symbol, Object value) {
        ParamTerm term = new ParamTerm();
        term.column = column;
        term.value = value;
        term.symbol = symbol;
        return term;
    }

}
