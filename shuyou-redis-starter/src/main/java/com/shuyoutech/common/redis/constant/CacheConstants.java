package com.shuyoutech.common.redis.constant;

/**
 * 缓存常量类
 *
 * @author YangChao
 * @date 2025-04-06 16:46
 **/
public interface CacheConstants {

    /**
     * 防重提交
     */
    String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流
     */
    String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 短信验证码
     */
    String CAPTCHA_SMS_KEY = "captcha_sms:";

    /**
     * 邮箱验证码
     */
    String CAPTCHA_EMAIL_KEY = "captcha_email:";

    /**
     * 验证码ip校验
     */
    String CAPTCHA_IP_KEY = "captcha_ip:";

    /**
     * 小程序access_token有效期
     */
    String MINI_PROGRAM_TOKEN_KEY = "mini_program_token";

    /**
     * 缓存-社交客户信息
     */
    String CACHE_SOCIAL_CLIENT = "cache_social_client";

    /**
     * 数据字典
     */
    String CACHE_DICT_KEY = "cache_dict_key";

    /**
     * 用户
     */
    String CACHE_USER_KEY = "cache_user_key";

    /**
     * 机构
     */
    String CACHE_ORG_KEY = "cache_org_key";

    /**
     * 角色
     */
    String CACHE_ROLE_KEY = "cache_role_key";

    /**
     * 岗位
     */
    String CACHE_POST_KEY = "cache_post_key";

    /**
     * 树形节点ID
     */
    String TREE_KEY = "tree_key:";

    /**
     * SHUYOU topic
     */
    String SHUYOU_PATTERN_TOPIC = "SHUYOU_PATTERN_TOPIC";

    /**
     * 支付钱包的分布式锁
     */
    String PAY_WALLET_LOCK = "pay_wallet:lock:%s";

    /**
     * ai 多轮对话ID
     */
    String AI_MEMORY_ID = "ai_memory_id:";

}
