package com.suparking.icbc.service;

import com.alibaba.fastjson.JSONObject;
import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.request.JftApiVendorInfoRegisterRequestV2;
import com.icbc.api.response.JftApiVendorInfoRegisterResponseV2;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.datamodule.merch.ICBCAccountRegisterNode;
import com.suparking.icbc.datamodule.merch.ICBCImageUploadNode;
import com.suparking.icbc.tools.HttpUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * the merch service impl.
 *
 * @author nuo-promise
 */
public class ICBCMerchServiceImpl {

    /**
     * 子商户日结账单
     * @param projectInfo
     * @param obj
     * @return
     */
    public String dayBill(ProjectInfo projectInfo, Object obj) throws Exception {
        String receiveJsonRet = "";
        try {
            ApiClient ac = new ApiClient(projectInfo.getMuchkey());
            ICBCDayBill icbcDayBill = (ICBCDayBill) obj;
            ApiRequest sendJson = ICBCPaymentServiceImpl.getApiRequest(projectInfo, icbcDayBill.getUrl(), icbcDayBill.getService());
            sendJson.setRequestField("subInstId", icbcDayBill.getSubInstId());
            sendJson.setRequestField("instId", projectInfo.getIcbcAppId());
            sendJson.setRequestField("queryDate", icbcDayBill.getQueryDate());
            receiveJsonRet = HttpUtils.icbcSdkSendPost(ac, sendJson);
            if (receiveJsonRet.isEmpty()) {
                return "";
            }
        } catch (Exception ex) {
            throw new Exception("dayBill,异常", ex);
        }
        return receiveJsonRet;
    }

    /**
     * 照片上传
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String imageUpload(ProjectInfo projectInfo, Object obj) throws Exception {
        String receiveJsonRet = "";
        try {
            ApiClient ac = new ApiClient(projectInfo.getMuchkey());
            ICBCImageUploadNode icbcImageUploadNode = (ICBCImageUploadNode) obj;
            ApiRequest sendJson = ICBCPaymentServiceImpl.getApiRequest(projectInfo, icbcImageUploadNode.getUrl(), icbcImageUploadNode.getService());
            sendJson.setRequestField("subInstId", icbcImageUploadNode.getSubInstId());
            sendJson.setRequestField("imageFileName", icbcImageUploadNode.getImageFileName());
            sendJson.setRequestField("imageType", icbcImageUploadNode.getImageType());
            sendJson.setRequestField("finishFlag", icbcImageUploadNode.getFinishFlag());
            receiveJsonRet = HttpUtils.icbcSdkImageUpload(ac, sendJson, icbcImageUploadNode.getImageFilePath());
            if (receiveJsonRet.isEmpty()) {
                return "";
            }
        } catch (Exception ex) {
            throw new Exception("imageUpload,异常", ex);
        }
        return receiveJsonRet;
    }
    /**
     * 子商户信息查询
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String merchQuery(ProjectInfo projectInfo, Object obj) throws Exception {
        String receiveJsonRet = "";
        try {
            ApiClient ac = new ApiClient(projectInfo.getMuchkey());
            ICBCMerchQuery icbcMerchQuery = (ICBCMerchQuery) obj;
            ApiRequest sendJson = ICBCPaymentServiceImpl.getApiRequest(projectInfo, icbcMerchQuery.getUrl(), icbcMerchQuery.getService());
            sendJson.setRequestField("subInstId", icbcMerchQuery.getSubInstId());
            receiveJsonRet = HttpUtils.icbcSdkSendPost(ac, sendJson);
            if (receiveJsonRet.isEmpty()) {
                return "";
            }
        } catch (Exception ex) {
            throw new Exception("merchQuery,异常", ex);
        }
        return receiveJsonRet;
    }

    /**
     * 子商户银行结算账号验证
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String verifyacct(ProjectInfo projectInfo, Object obj) throws Exception {
        String receiveJsonRet = "";
        try {
            ApiClient ac = new ApiClient(projectInfo.getMuchkey());
            ICBCVerifyAcctNode icbcVerifyAcctNode = (ICBCVerifyAcctNode) obj;
            ApiRequest sendJson = ICBCPaymentServiceImpl.getApiRequest(projectInfo, icbcVerifyAcctNode.getUrl(), icbcVerifyAcctNode.getService());
            sendJson.setRequestField("subInstId", icbcVerifyAcctNode.getSubInstId());
            sendJson.setRequestField("amount", icbcVerifyAcctNode.getAmount());
            receiveJsonRet = HttpUtils.icbcSdkSendPost(ac, sendJson);
            if (receiveJsonRet.isEmpty()) {
                return "";
            }
        } catch (Exception ex) {
            throw new Exception("verifyacct,异常", ex);
        }
        return receiveJsonRet;
    }

    /**
     * 进件
     * @param projectInfo
     * @param obj
     * @return
     */
    public String maintain(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCAccountRegisterNode icbcAccountRegisterNode = (ICBCAccountRegisterNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcAccountRegisterNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcAccountRegisterNode.getCharset(), icbcAccountRegisterNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcAccountRegisterNode.getEncrypt_type(), projectInfo.getEntryptKey(), "", "");
            JftApiVendorInfoRegisterRequestV2 request = new JftApiVendorInfoRegisterRequestV2();
            request.setServiceUrl(icbcAccountRegisterNode.getUrl());

            JftApiVendorInfoRegisterRequestV2.JftApiVendorInfoRegisterRequestV2Biz bizCount = new JftApiVendorInfoRegisterRequestV2.JftApiVendorInfoRegisterRequestV2Biz();
            bizCount.setAppId(icbcAccountRegisterNode.getAppId());
            bizCount.setOutUpperVendorId(icbcAccountRegisterNode.getOutUpperVendorId());
            bizCount.setOutVendorId(icbcAccountRegisterNode.getOutVendorId());
            bizCount.setOutUserId(icbcAccountRegisterNode.getOutUserId());
            bizCount.setVendorName(icbcAccountRegisterNode.getVendorName());
            bizCount.setVendorShortName(icbcAccountRegisterNode.getVendorShortName());
            bizCount.setVendorPhone(icbcAccountRegisterNode.getVendorPhone());
            bizCount.setVendorEmail(icbcAccountRegisterNode.getVendorEmail());
            bizCount.setProvince(icbcAccountRegisterNode.getProvince());
            bizCount.setCity(icbcAccountRegisterNode.getCity());
            bizCount.setCounty(icbcAccountRegisterNode.getCounty());
            bizCount.setAddress(icbcAccountRegisterNode.getAddress());
            bizCount.setPostcode(icbcAccountRegisterNode.getPostcode());
            bizCount.setOperatorName(icbcAccountRegisterNode.getOperatorName());
            bizCount.setOperatorMobile(icbcAccountRegisterNode.getOperatorMobile());
            bizCount.setOperatorEmail(icbcAccountRegisterNode.getOperatorEmail());
            bizCount.setOperatorIdNo(icbcAccountRegisterNode.getOperatorIdNo());
            bizCount.setVendorType(icbcAccountRegisterNode.getVendorType());
            bizCount.setCorprateIdType(icbcAccountRegisterNode.getCorprateIdType());
            bizCount.setCorprateName(icbcAccountRegisterNode.getCorprateName());
            bizCount.setCorprateMobile(icbcAccountRegisterNode.getCorprateMobile());
            bizCount.setCorprateIdNo(icbcAccountRegisterNode.getCorprateIdNo());
            bizCount.setCorprateIdPic1(icbcAccountRegisterNode.getCorprateIdPic1());
            bizCount.setCertType(icbcAccountRegisterNode.getCertType());
            bizCount.setCertPic(icbcAccountRegisterNode.getCertPic());
            bizCount.setCertNo(icbcAccountRegisterNode.getCertNo());
            bizCount.setAccountName(icbcAccountRegisterNode.getAccountName());
            bizCount.setAccountBankProvince(icbcAccountRegisterNode.getAccountBankProvince());
            bizCount.setAccountBankCity(icbcAccountRegisterNode.getAccountBankCity());
            bizCount.setAccountBankNm(icbcAccountRegisterNode.getAccountBankNm());
            bizCount.setAccountBankName(icbcAccountRegisterNode.getAccountBankName());
            bizCount.setAccountBankCode(icbcAccountRegisterNode.getAccountBankCode());
            bizCount.setAccountNo(icbcAccountRegisterNode.getAccountNo());
            bizCount.setAccountMobile(icbcAccountRegisterNode.getAccountMobile());

            request.setBizContent(bizCount);

            JftApiVendorInfoRegisterResponseV2 response = (JftApiVendorInfoRegisterResponseV2) client.execute(request, icbcAccountRegisterNode.getMsg_id());
            if (response.isSuccess()) {
               result.put("result_code", "0000");
               result.put("result_desc", "成功");
               result.put("msg_id", response.getMsgId());
               result.put("outVendorId", response.getOutVendorId());
               // 1 新建 2 正常 3 冻结 4  销户
               result.put("vendorStatus", response.getVendorStatus());
               result.put("auditStatus", response.getAuditStatus());

            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }

        } catch (Exception ex) {
            throw new Exception("maintain,异常",ex);
        }
        return result.toJSONString();
    }
}
