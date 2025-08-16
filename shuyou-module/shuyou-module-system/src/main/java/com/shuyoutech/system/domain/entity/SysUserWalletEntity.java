package com.shuyoutech.system.domain.entity;

import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.ProfileVo;
import com.shuyoutech.system.domain.vo.SysUserVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-06-11 09:55
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMappers({@AutoMapper(target = SysUserVo.class), @AutoMapper(target = LoginUser.class), @AutoMapper(target = ProfileVo.class)})
@Schema(description = "用户钱包表")
@Document(collection = "sys_user_wallet")
public class SysUserWalletEntity extends BaseEntity<SysUserWalletEntity> {

    @Schema(description = "总的可用余额，包括赠金和充值余额")
    private String totalBalance;

    @Schema(description = "未过期的赠金余额")
    private String grantedBalance;

    @Schema(description = "充值余额")
    private String toppedUpBalance;

    @Schema(description = "货币 人民币或美元 CNY, USD")
    private String currency;

}
