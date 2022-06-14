package com.suparking.icbc.pojo.merch;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * .
 *
 * @author nuo-promise
 */
@Data
public class APIMainTainModel {
    private String projectNo;
    // 子商户编号 20
    private String subInstId;
    // 子商户名称 100
    private String subInstName;
    // 子商户简称 20
    private String subInstShortName;
    // 机构开通微信支付时必输
    private String subInstWxType;
    // 机构开通支付宝支付时必输
    private String subInstZfbType;
    // 支付订单中展示给用户的电话
    private String servicePhone;
    // 回佣费率
    private Integer feeRate;
    // 回佣费率json 数组
    private List<JSONObject> feerateArray;
    // 回佣费率json数组
    private List<JSONObject> icbcFeeRateArray;
    // 担保支付支持标志 00 不支持, 01 支持
    private String guaranteeFlag;
    // 担保支付回佣费率
    private Integer guaranteeFeeRate;
    // 绿洲商户开启标志
    private String lvzFlag;
    // 维护类型 2 完整信息登记 4 详细信息更新 2,4 都需要银行内部审核
    private String transType;
    // 详细信息
    private String certType;
    private String certNo;
    private String certCorpName;
    private String certLegalRep;
    private String certExp;
    private String corpType;
    private String province;
    private String city;
    private String district;
    private String address;
    private String postcode;
    private String website;
    private String owner;
    private String ownerIdType;
    private String ownerIDNo;
    private String IDCardExp;
    private String ownerPhone;
    private String linkman;
    private String phone;
    private String email;
    private String operatorIdNo;
    private String accountBankNm;
    private String singleamtlmt;
    private String dayamtlmt;
    private String paySingleamtlmt;
    private String payDayamtlmt;
    private String informUrl;
    private String subscribeAppid;
    private List<JSONObject> subscribeAppidPair;
    // 银行账户信息
    private String settAccAttr1;
    private String settAccAttr2;
    private String settAccProvince;
    private String settAccCity;
    private String settAccNo;
    private String settAccName;
    private String checkAccnoFlag;
    private String settAccIdType;
    private String settAccIdNo;
    private String settAccMobile;
    private String retAccNo;
    private String retAccName;
}
