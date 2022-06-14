package com.suparking.icbc.datamodule.projectInfoImpl;

import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.pojo.abcnosense.AbcNoSenseInfo;
import lombok.Data;

@Data
public class IcbcPayProjectInfo extends ProjectInfo {
    //TODO 支付平台ID
    private String platformId="icbc";
    // TODO 商户号 工行由于支持程序清分,所以也支持不同的商户,所以商户号支持开发者 根据项目传递
    private String merchantId="";
    // 密钥
    private String muchkey="";

    // 2022-06-13 新增网关公钥
    private String apiGwPublicKey;

    // 2022-06-14 新增加密密钥
    private String encryptKey;

    // 支付平台名称
    private String platformName="数泊科技:工商银行支付";
    private String projectNo;
    //渠道编号
    private String channelId="";
    private Boolean initStatus=false;
    /** 2020-07-09 新增农行无感支付 商户号 */
    private AbcNoSenseInfo abcNoSenseInfo;
    /** 2021-01-13 新增 APPID 配置 */
    private String icbcAppId;
}
