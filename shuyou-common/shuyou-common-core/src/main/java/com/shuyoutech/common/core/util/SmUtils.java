package com.shuyoutech.common.core.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.*;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;

/**
 * SM国密算法工具类
 *
 * @author YangChao
 * @date 2025-07-06 15:01
 **/
@Slf4j
public class SmUtils extends SmUtil {

    public static final String ALGORITHM_EC = "EC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成SM2 公私钥
     *
     * @return - JSON对象
     */
    public static JSONObject sm2Create() {
        // 创建sm2 对象
        SM2 sm2 = SmUtil.sm2();
        //这里会自动生成对应的随机秘钥对 , 注意！ 这里一定要强转，才能得到对应有效的秘钥信息
        byte[] privateKey = BCUtil.encodeECPrivateKey(sm2.getPrivateKey());
        //这里公钥不压缩  公钥的第一个字节用于表示是否压缩  可以不要
        byte[] publicKey = BCUtil.encodeECPublicKey(sm2.getPublicKey(), false);
        //这里得到的 压缩后的公钥
        byte[] publicKeyCompress = BCUtil.encodeECPublicKey(sm2.getPublicKey(), true);
        //打印当前的公私秘钥
        String privateKeyHex = HexUtil.encodeHexStr(privateKey);
        String publicKeyHex = HexUtil.encodeHexStr(publicKey);
        String publicKeyHexCompress = HexUtil.encodeHexStr(publicKeyCompress);
        String privateKeyPem = sm2.getPrivateKeyBase64();
        String publicKeyPem = sm2.getPublicKeyBase64();
        log.info("sm2Create ======================= 私钥: {}", privateKeyHex);
        log.info("sm2Create ======================= 公钥压缩前: {}", publicKeyHex);
        log.info("sm2Create ======================= 公钥压缩后: {}", publicKeyHexCompress);
        log.info("sm2Create ======================= 私钥base64: {}", privateKeyPem);
        log.info("sm2Create ======================= 公钥base64: {}", publicKeyPem);
        JSONObject object = new JSONObject();
        object.put("privateKeyHex", privateKeyHex);
        object.put("publicKeyHex", publicKeyHex);
        object.put("publicKeyHexCompress", publicKeyHexCompress);
        object.put("privateKeyPem", privateKeyPem);
        object.put("publicKeyPem", publicKeyPem);
        return object;
    }

    /**
     * SM2 公钥 将Hex格式密钥转换为PEM格式密钥(sm2p256v1)
     *
     * @param publicKeyHex - 密钥HEX字符串
     * @return - 秘钥PEM
     */
    public static String sm2PublicHexToPem(String publicKeyHex) {
        try {
            byte[] encodeByte = SecureUtil.decode(publicKeyHex);
            X9ECParameters x9Parameters = ECUtil.getNamedCurveByName(SM2_CURVE_NAME);
            ECCurve curve = x9Parameters.getCurve();
            ECPoint ecPoint = EC5Util.convertPoint(curve.decodePoint(encodeByte));
            ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(SM2_CURVE_NAME);
            ECNamedCurveSpec spec = new ECNamedCurveSpec(SM2_CURVE_NAME, parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());

            ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, spec);
            ECPublicKey ecPublicKey = new BCECPublicKey(ALGORITHM_EC, keySpec, BouncyCastleProvider.CONFIGURATION);

            String encode = Base64.encode(ecPublicKey.getEncoded());
            log.info("sm2PublicHexToPem ======================= pem: {}", encode);
            return encode;
        } catch (Exception e) {
            log.error("sm2PublicHexToPem ======================= exception: {}", e.getMessage());
        }
        return "";
    }

    /**
     * SM2 公钥 将PEM格式密钥转换为Hex
     *
     * @param publicKeyPem - 密钥HEX字符串
     * @return - 秘钥Hex
     */
    public static String sm2PublicPemToHex(String publicKeyPem) {
        try {
            PublicKey publicKey = KeyUtil.generatePublicKey(ALGORITHM_EC, Base64.decode(publicKeyPem));
            String publicKeyHex = HexUtil.encodeHexStr(((BCECPublicKey) publicKey).getQ().getEncoded(false));
            log.info("sm2PublicPemToHex ======================= Hex: {}", publicKeyHex);
            return publicKeyHex;
        } catch (Exception e) {
            log.error("sm2PublicPemToHex ======================= exception: {}", e.getMessage());
        }
        return "";
    }

    /**
     * SM2 私钥 将Hex格式密钥转换为PEM格式密钥(sm2p256v1)
     *
     * @param privateKeyHex - 密钥HEX字符串
     * @return - 秘钥PEM
     */
    public static String sm2PrivateHexToPem(String privateKeyHex) {
        try {
            ECPrivateKeyParameters ecPrivateKeyParameters = ECKeyUtil.decodePrivateKeyParams(SecureUtil.decode(privateKeyHex));
            X9ECParameters x9Parameters = GMNamedCurves.getByName(SM2_CURVE_NAME);
            ECNamedCurveSpec ecNamedCurveSpec = new ECNamedCurveSpec(SM2_CURVE_NAME, x9Parameters.getCurve(), x9Parameters.getG(), x9Parameters.getN(), x9Parameters.getH());

            ECDomainParameters domainParameters = ecPrivateKeyParameters.getParameters();
            org.bouncycastle.math.ec.ECPoint q = new FixedPointCombMultiplier().multiply(domainParameters.getG(), ecPrivateKeyParameters.getD());
            ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(q, domainParameters);

            BCECPublicKey publicKey = new BCECPublicKey(ALGORITHM_EC, ecPublicKeyParameters, ecNamedCurveSpec, BouncyCastleProvider.CONFIGURATION);
            BCECPrivateKey privateKey = new BCECPrivateKey(ALGORITHM_EC, ecPrivateKeyParameters, publicKey, ecNamedCurveSpec, BouncyCastleProvider.CONFIGURATION);

            String encode = Base64.encode(privateKey.getEncoded());
            log.info("sm2PrivateHexToPem ======================= pem: {}", encode);
            return encode;
        } catch (Exception e) {
            log.error("sm2PrivateHexToPem ======================= exception: {}", e.getMessage());
        }
        return "";
    }

    /**
     * SM2 私钥 将PEM格式密钥转换为Hex
     *
     * @param privateKeyPem - 密钥HEX字符串
     * @return - 秘钥Hex
     */
    public static String sm2PrivatePemToHex(String privateKeyPem) {
        try {
            PrivateKey privateKey = KeyUtil.generatePrivateKey(ALGORITHM_EC, Base64.decode(privateKeyPem));
            String privateKeyHex = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(privateKey));
            log.info("sm2PrivatePemToHex ======================= pem: {}", privateKeyHex);
            return privateKeyHex;
        } catch (Exception e) {
            log.error("sm2PrivatePemToHex ======================= exception: {}", e.getMessage());
        }
        return "";
    }

    /**
     * SM2 - 公钥加密Base64
     *
     * @param publicKey - 公钥
     * @param data      - 原始数据
     * @return base64字符串
     */
    public static String sm2EncryptBase64(String publicKey, String data) {
        SM2 sm2 = SmUtil.sm2(null, publicKey);
        return sm2.encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * SM2 - 公钥加密Hex
     *
     * @param publicKey - 公钥
     * @param data      - 原始数据
     * @return Hex字符串
     */
    public static String sm2EncryptHex(String publicKey, String data) {
        SM2 sm2 = SmUtil.sm2(null, publicKey);
        return sm2.encryptHex(data, KeyType.PublicKey);
    }

    /**
     * SM2 - 私钥解密
     *
     * @param privateKey - 私钥
     * @param data       - 加密数据
     * @return 字符串
     */
    public static String sm2Decrypt(String privateKey, String data) {
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        return sm2.decryptStr(data, KeyType.PrivateKey);
    }

    /**
     * SM2 - 私钥签名
     *
     * @param privateKey - 私钥
     * @param data       - 签名数据
     * @return Hex字符串
     */
    public static String sm2SignHex(String privateKey, String data) {
        byte[] dataBytes = data.getBytes();
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        byte[] sign = sm2.sign(dataBytes);
        return HexUtil.encodeHexStr(sign);
    }

    /**
     * SM2 - 私钥签名
     *
     * @param privateKey - 私钥
     * @param data       - 签名数据
     * @return Base64字符串
     */
    public static String sm2SignBase64(String privateKey, String data) {
        byte[] dataBytes = data.getBytes();
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        byte[] sign = sm2.sign(dataBytes);
        return Base64.encode(sign);
    }

    /**
     * SM2 - 验证签名
     *
     * @param publicKey - 公钥
     * @param data      - 数据
     * @param sign      - 签名
     * @return Base64字符串
     */
    public static Boolean sm2Verify(String publicKey, String data, String sign) {
        byte[] dataBytes = data.getBytes();
        byte[] singBytes = SecureUtil.decode(sign);
        SM2 sm2 = SmUtil.sm2(null, publicKey);
        return sm2.verify(dataBytes, singBytes);
    }

}
