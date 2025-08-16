package com.shuyoutech.system.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 20:05
 **/
@Data
@Schema(description = "用户岗位信息业务对象类")
public class UserPostsBo implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;

    @NotNull(message = "岗位ID集合不能为空")
    @Schema(description = "岗位ID集合")
    private Set<String> postIds;

}
