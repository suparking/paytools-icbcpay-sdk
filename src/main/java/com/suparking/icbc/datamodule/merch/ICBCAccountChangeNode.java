package com.suparking.icbc.datamodule.merch;

import com.suparking.icbc.datamodule.ICBC.ICBCBaseNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCAccountChangeNode extends ICBCBaseNode {
    private String appId;
    private String outUpperVendorId;

    private String outVendorId;

    private String outUserId;

    private String vendorName;

    private String vendorShortName;

    private String vendorPhone;

    private String vendorEmail;

    private String province;

    private String city;

    private String county;

    private String address;

    private String postcode;

    private String operatorName;

    private String operatorMobile;

    private String operatorEmail;

    private String operatorIdNo;

    private String vendorType;

    private String corprateIdType;

    private String corprateName;

    private String corprateMobile;

    private String corprateIdNo;

    private String corprateIdValidity;

    private String corprateIdPic1;

    private String corprateIdPic2;

    private String certType;

    private String certPic;

    private String CertPic2;

    private String certNo;

    private String certValidity1;

    private String accountBizType;

    private String accountName;

    private String accountBankProvince;

    private String accountBankCity;

    private String accountBankNm;

    private String accountBankName;

    private String accountBankCode;

    private String accountNo;

    private String accountMobile;

    private String cbmsVendorAppIPic1;

    private String wxName1;

    private String wxTel1;

    private String wxmcc1;

    private String wxName2;

    private String wxTel2;

    private String wxmcc2;

    private String subAppid1;

    private String zfbMcc1;

    private String zfbSourceId1;

    private String zfbRateChanl1;

    private String zfbMerCusttype1;

    private String zfbMerCustnum1;

    private String zfbMerCustSort1;

    private String zfbMerCustCode1;

    private String zfbMerCard1;

    private String zfboperatortype1;

    private String zfbservicephone1;

    private String zfbcontact1;

    private String zfbcontacttype1;

    private String zfbmerchantremart1;

    private String zfbaddress1;
}
