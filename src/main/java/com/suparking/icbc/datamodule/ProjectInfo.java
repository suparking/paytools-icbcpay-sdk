package com.suparking.icbc.datamodule;

import com.suparking.icbc.pojo.abcnosense.AbcNoSenseInfo;
import lombok.Data;

import java.util.Optional;
import java.util.StringJoiner;

@Data
public class ProjectInfo {
    // 项目编号
    private String projectNo;
    //TODO 支付平台ID
    private String platformId;
    // TODO 商户号
    private String merchantId;
    // 密钥
    private String muchkey;
    // 网关公钥
    private String apiGwPublicKey;
    // encryptKey
    private String encryptKey;

    private String platformName;
    private String queryType;

    // 渠道编号
    private String channelId;
    /** 2020-07-09 新增农行无感支付 商户号 */
    private AbcNoSenseInfo abcNoSenseInfo;
    /** 2021-01-13 新增icbcappid 配置*/
    private String icbcAppId;


    @Override
    public String toString()
    {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add("支付平台名称: "+ getPlatformName());
        stringJoiner.add("支付平台ID： "+ getPlatformId());
        stringJoiner.add("项目编号:"+getProjectNo());
        stringJoiner.add("商户号: "+ getMerchantId());
        stringJoiner.add("密钥: "+ getMuchkey());
        stringJoiner.add("网关公钥: " + getApiGwPublicKey());
        stringJoiner.add("应用加密Key: "+ getEncryptKey());
        stringJoiner.add("渠道编号:"+getChannelId());
        stringJoiner.add("一级商户ID:"+getIcbcAppId());
        if (Optional.ofNullable(getAbcNoSenseInfo()).isPresent()) {
            stringJoiner.add("农行无感支付商户号:"+getAbcNoSenseInfo().getPartnerId());
        }
        return stringJoiner.toString();
    }
}