package com.suparking.icbc.service;

import com.alibaba.fastjson.JSONObject;
import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.request.JftApiVendorInfoModifyRequestV1;
import com.icbc.api.request.JftApiVendorInfoQueryRequestV1;
import com.icbc.api.request.JftApiVendorInfoRegisterRequestV1;
import com.icbc.api.request.JftApiVendorInfoRegisterRequestV2;
import com.icbc.api.request.JftApiVendorPicDownloadRequestV1;
import com.icbc.api.request.JftApiVendorPicUploadRequestV1;
import com.icbc.api.request.TokenRequestV1;
import com.icbc.api.response.JftApiVendorInfoModifyResponseV1;
import com.icbc.api.response.JftApiVendorInfoQueryResponseV1;
import com.icbc.api.response.JftApiVendorInfoRegisterResponseV2;
import com.icbc.api.response.JftApiVendorPicDownloadResponseV1;
import com.icbc.api.response.JftApiVendorPicUploadResponseV1;
import com.icbc.api.response.TokenResponseV1;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.datamodule.merch.ICBCAccountChangeNode;
import com.suparking.icbc.datamodule.merch.ICBCAccountRegisterNode;
import com.suparking.icbc.datamodule.merch.ICBCAccountSearchNode;
import com.suparking.icbc.datamodule.merch.ICBCPayFileDownloadNode;
import com.suparking.icbc.datamodule.merch.ICBCPicDownloadNode;
import com.suparking.icbc.datamodule.merch.ICBCPicUploadNode;
import com.suparking.icbc.datamodule.merch.ICBCTokenNode;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * the merch service impl.
 *
 * @author nuo-promise
 */
public class ICBCMerchServiceImpl {

    /**
     * 获取对账token
     *
     * @param projectInfo
     * @param obj
     * @return
     */
    public String icbcToken(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCTokenNode icbcTokenNode = (ICBCTokenNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcTokenNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcTokenNode.getCharset(), icbcTokenNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcTokenNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");

            TokenRequestV1 request = new TokenRequestV1();
            request.setServiceUrl(icbcTokenNode.getUrl());

            TokenRequestV1.TokenRequestV1Biz bizCount = new TokenRequestV1.TokenRequestV1Biz();
            bizCount.setAppId(icbcTokenNode.getAppId());
            bizCount.setVersion(icbcTokenNode.getVersion());
            bizCount.setValidTime(icbcTokenNode.getValidTime());
            request.setBizContent(bizCount);

            TokenResponseV1 response = (TokenResponseV1) client.execute(request, icbcTokenNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("appId", response.getAppId());
                tmp.put("msg_id", response.getMsgId());
                tmp.put("randomValue", response.getRandomValue());
                tmp.put("token", response.getToken());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("icbcToken,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 订单结算对账单
     *
     * @param obj
     * @return
     */
    public String payDownload(Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCPayFileDownloadNode icbcPayFileDownloadNode = (ICBCPayFileDownloadNode) obj;
            String requestUrl = icbcPayFileDownloadNode.getUrl();
            String appId = icbcPayFileDownloadNode.getAppId();
            String token = icbcPayFileDownloadNode.getToken();
            String randomValue = icbcPayFileDownloadNode.getRandomValue();
            String acDate = icbcPayFileDownloadNode.getAcDate();

            URL url = new URL(requestUrl + "?appId=" + appId + "&" + "token=" + token + "&" + "randomValue=" + randomValue + "&" + "acDate=" + acDate);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            Map<String, List<String>> map = httpUrlConnection.getHeaderFields();
            List<String> retList = map.get("rets");
            String retcode = "";
            String retmsg = "";
            if (retList != null && retList.size() > 0) {
                retcode = URLDecoder.decode(retList.get(0), "utf-8");
                retmsg = URLDecoder.decode(retList.get(1), "utf-8");
            }
            if (ConstantData.PAY_FILE_DOWNLOAD_SUCCESS.equals(retcode)) {
                InputStream is = httpUrlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                String path = ConstantData.PAY_FILE_DOWNLOAD_PATH;
                String fileName = "WITHDRAWORDER_ + " + icbcPayFileDownloadNode.getOutVendorId() + "_" + TimeUtils.getQRCodeDate(acDate) + ".zip";
                File file = new File(path + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                int i = -1;
                while ((i = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, i);
                }
                is.close();
                fos.flush();
                fos.close();

                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("fileName", fileName);
                result.put("response", tmp);
            } else {
                result.put("result_code", retcode);
                result.put("result_desc", retmsg);
            }
        } catch (Exception ex) {
            throw new Exception("payDownload,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 对账单
     *
     * @param obj
     * @return
     */
    public String payFileDownload(Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCPayFileDownloadNode icbcPayFileDownloadNode = (ICBCPayFileDownloadNode) obj;
            String requestUrl = icbcPayFileDownloadNode.getUrl();
            String appId = icbcPayFileDownloadNode.getAppId();
            String token = icbcPayFileDownloadNode.getToken();
            String randomValue = icbcPayFileDownloadNode.getRandomValue();
            String acDate = icbcPayFileDownloadNode.getAcDate();
            URL url = new URL(requestUrl + "?appId=" + appId + "&" + "token=" + token + "&" + "randomValue=" + randomValue + "&" + "acDate=" + acDate);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            Map<String, List<String>> map = httpUrlConnection.getHeaderFields();
            List<String> retList = map.get("rets");
            String retcode = "";
            String retmsg = "";
            if (retList != null && retList.size() > 0) {
                retcode = URLDecoder.decode(retList.get(0), "utf-8");
                retmsg = URLDecoder.decode(retList.get(1), "utf-8");
            }
            if (ConstantData.PAY_FILE_DOWNLOAD_SUCCESS.equals(retcode)) {
                InputStream is = httpUrlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                String path = ConstantData.PAY_FILE_DOWNLOAD_PATH;
                String fileName = "AccountCheckingDetail" + icbcPayFileDownloadNode.getOutVendorId() + TimeUtils.getQRCodeDate() + ".zip";
                File file = new File(path + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                int i = -1;
                while ((i = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, i);
                }
                is.close();
                fos.flush();
                fos.close();

                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("fileName", fileName);
                result.put("response", tmp);
            } else {
                result.put("result_code", retcode);
                result.put("result_desc", retmsg);
            }
        } catch (Exception ex) {
            throw new Exception("payFileDownload,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 照片上传
     *
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String imageUpload(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCPicUploadNode icbcPicUploadNode = (ICBCPicUploadNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcPicUploadNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcPicUploadNode.getCharset(), icbcPicUploadNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcPicUploadNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");

            JftApiVendorInfoRegisterRequestV1 request = new JftApiVendorInfoRegisterRequestV1();
            request.setServiceUrl(icbcPicUploadNode.getUrl());

            JftApiVendorPicUploadRequestV1.JftApiVendorPicUploadRequestV1Biz bizCount = new JftApiVendorPicUploadRequestV1.JftApiVendorPicUploadRequestV1Biz();
            bizCount.setAppId(icbcPicUploadNode.getAppId());
            bizCount.setOutVendorId(icbcPicUploadNode.getOutVendorId());
            bizCount.setImageFile(icbcPicUploadNode.getImageFile());

            request.setBizContent(bizCount);

            JftApiVendorPicUploadResponseV1 response = (JftApiVendorPicUploadResponseV1) client.execute(request, icbcPicUploadNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("msg_id", response.getMsgId());
                tmp.put("outVendorId", response.getOutVendorId());
                tmp.put("imageKey", response.getImageKey());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("imageUpload,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 照片下载
     *
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String imageDownload(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCPicDownloadNode icbcPicDownloadNode = (ICBCPicDownloadNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcPicDownloadNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcPicDownloadNode.getCharset(), icbcPicDownloadNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcPicDownloadNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");

            JftApiVendorPicDownloadRequestV1 request = new JftApiVendorPicDownloadRequestV1();
            request.setServiceUrl(icbcPicDownloadNode.getUrl());

            JftApiVendorPicDownloadRequestV1.JftApiVendorPicDownloadRequestV1Biz bizCount = new JftApiVendorPicDownloadRequestV1.JftApiVendorPicDownloadRequestV1Biz();
            bizCount.setAppId(icbcPicDownloadNode.getAppId());
            bizCount.setOutVendorId(icbcPicDownloadNode.getOutVendorId());
            bizCount.setImageKey(icbcPicDownloadNode.getImageKey());
            request.setBizContent(bizCount);

            JftApiVendorPicDownloadResponseV1 response = (JftApiVendorPicDownloadResponseV1) client.execute(request, icbcPicDownloadNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("msg_id", response.getMsgId());
                tmp.put("outVendorId", response.getOutVendorId());
                tmp.put("imageFile", response.getImageFile());
                tmp.put("contentType", response.getContentType());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("imageDownload,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 子商户信息查询
     *
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String merchQuery(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCAccountSearchNode icbcAccountSearchNode = (ICBCAccountSearchNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcAccountSearchNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcAccountSearchNode.getCharset(), icbcAccountSearchNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcAccountSearchNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");

            JftApiVendorInfoQueryRequestV1 request = new JftApiVendorInfoQueryRequestV1();
            request.setServiceUrl(icbcAccountSearchNode.getUrl());

            JftApiVendorInfoQueryRequestV1.JftApiVendorInfoQueryRequestV1Biz bizCount = new JftApiVendorInfoQueryRequestV1.JftApiVendorInfoQueryRequestV1Biz();
            bizCount.setAppId(icbcAccountSearchNode.getAppId());
            bizCount.setOutVendorId(icbcAccountSearchNode.getOutVendorid());

            request.setBizContent(bizCount);

            JftApiVendorInfoQueryResponseV1 response = (JftApiVendorInfoQueryResponseV1) client.execute(request, icbcAccountSearchNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("msg_id", response.getMsgId());
                tmp.put("outVendorId", response.getOutVendorId());
                tmp.put("vendorName", response.getVendorName());
                tmp.put("vendorShortName", response.getVendorShortName());
                tmp.put("vendorPhone", response.getVendorPhone());
                tmp.put("vendorEmail", response.getVendorEmail());
                tmp.put("province", response.getProvince());
                tmp.put("city", response.getCity());
                tmp.put("county", response.getCounty());
                tmp.put("address", response.getAddress());
                tmp.put("operatorName", response.getOperatorName());
                tmp.put("operatorMobile", response.getOperatorMobile());
                tmp.put("operatorEmail", response.getOperatorEmail());
                tmp.put("operatorIdNo", response.getOperatorIdNo());
                tmp.put("vendorStatus", response.getVendorStatus());
                tmp.put("auditStatus", response.getAuditStatus());
                tmp.put("vendorType", response.getVendorType());
                tmp.put("corprateName", response.getCorprateName());
                tmp.put("corprateMobile", response.getCorprateMobile());
                tmp.put("corprateIdType", response.getCorprateIdType());
                tmp.put("corprateIdNo", response.getCorprateIdNo());
                tmp.put("corporateIdValidity", response.getCorporateIdValidity());
                tmp.put("corprateIdPic1", response.getCorprateIdPic1());
                tmp.put("certType", response.getCertType());
                tmp.put("certNo", response.getCertNo());
                tmp.put("certValidityl", response.getCertValidityl());
                tmp.put("certPic", response.getCertPic());
                tmp.put("otherCertPic1", response.getOtherCertPic1());
                tmp.put("otherCertPic2", response.getOtherCertPic2());
                tmp.put("otherCertPic3", response.getOtherCertPic3());
                tmp.put("accountName", response.getAccountName());
                tmp.put("accountBankProvince", response.getAccountBankProvince());
                tmp.put("accountBankCity", response.getAccountBankCity());
                tmp.put("accountBankNm", response.getAccountBankNm());
                tmp.put("accountBankName", response.getAccountBankName());
                tmp.put("accountBankCode", response.getAccountBankCode());
                tmp.put("accountNo", response.getAccountNo());
                tmp.put("accountMobile", response.getAccountMobile());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("merchQuery,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 进件
     *
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
                    icbcAccountRegisterNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
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
            bizCount.setSubAppid1(icbcAccountRegisterNode.getSubAppid1());
            bizCount.setWxmcc1(icbcAccountRegisterNode.getWxmcc1());
            bizCount.setWxName1(icbcAccountRegisterNode.getWxName1());
            bizCount.setWxTel1(icbcAccountRegisterNode.getWxTel1());
            bizCount.setWxmcc2(icbcAccountRegisterNode.getWxmcc2());
            bizCount.setWxName2(icbcAccountRegisterNode.getWxName2());
            bizCount.setWxTel2(icbcAccountRegisterNode.getWxTel2());
            bizCount.setZfbMcc1(icbcAccountRegisterNode.getZfbMcc1());
            bizCount.setZfbSourceId1(icbcAccountRegisterNode.getZfbSourceId1());
            bizCount.setZfbRateChanl1(icbcAccountRegisterNode.getZfbRateChanl1());
            bizCount.setZfbMerCusttype1(icbcAccountRegisterNode.getZfbMerCusttype1());
            bizCount.setZfbMerCustnum1(icbcAccountRegisterNode.getZfbMerCustnum1());
            bizCount.setZfbMerCustSort1(icbcAccountRegisterNode.getZfbMerCustSort1());
            bizCount.setZfbMerCustCode1(icbcAccountRegisterNode.getZfbMerCustCode1());
            bizCount.setZfbMerCard1(icbcAccountRegisterNode.getZfbMerCard1());
            bizCount.setZfboperatortype1(icbcAccountRegisterNode.getZfboperatortype1());
            bizCount.setZfbservicephone1(icbcAccountRegisterNode.getZfbservicephone1());
            bizCount.setZfbcontact1(icbcAccountRegisterNode.getZfbcontact1());
            bizCount.setZfbcontacttype1(icbcAccountRegisterNode.getZfbcontacttype1());
            bizCount.setZfbmerchantremart1(icbcAccountRegisterNode.getZfbmerchantremart1());
            bizCount.setZfbaddress1(icbcAccountRegisterNode.getZfbaddress1());

            request.setBizContent(bizCount);

            JftApiVendorInfoRegisterResponseV2 response = (JftApiVendorInfoRegisterResponseV2) client.execute(request, icbcAccountRegisterNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("msg_id", response.getMsgId());
                tmp.put("outVendorId", response.getOutVendorId());
                // 1 新建 2 正常 3 冻结 4  销户
                tmp.put("vendorStatus", response.getVendorStatus());
                tmp.put("auditStatus", response.getAuditStatus());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("maintain,异常", ex);
        }
        return result.toJSONString();
    }

    /**
     * 进件修改
     *
     * @param projectInfo
     * @param obj
     * @return
     */
    public String maintainModify(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCAccountChangeNode icbcAccountChangeNode = (ICBCAccountChangeNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcAccountChangeNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcAccountChangeNode.getCharset(), icbcAccountChangeNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcAccountChangeNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");

            JftApiVendorInfoModifyRequestV1 request = new JftApiVendorInfoModifyRequestV1();
            request.setServiceUrl(icbcAccountChangeNode.getUrl());

            JftApiVendorInfoModifyRequestV1.JftApiVendorInfoModifyRequestV1Biz bizCount = new JftApiVendorInfoModifyRequestV1.JftApiVendorInfoModifyRequestV1Biz();
            bizCount.setAppId(icbcAccountChangeNode.getAppId());
            bizCount.setOutVendorId(icbcAccountChangeNode.getOutVendorId());
            bizCount.setVendorName(icbcAccountChangeNode.getVendorName());
            bizCount.setVendorShortName(icbcAccountChangeNode.getVendorShortName());
            bizCount.setVendorPhone(icbcAccountChangeNode.getVendorPhone());
            bizCount.setVendorEmail(icbcAccountChangeNode.getVendorEmail());
            bizCount.setProvince(icbcAccountChangeNode.getProvince());
            bizCount.setCity(icbcAccountChangeNode.getCity());
            bizCount.setCounty(icbcAccountChangeNode.getCounty());
            bizCount.setAddress(icbcAccountChangeNode.getAddress());
            bizCount.setPostcode(icbcAccountChangeNode.getPostcode());
            bizCount.setOperatorName(icbcAccountChangeNode.getOperatorName());
            bizCount.setOperatorMobile(icbcAccountChangeNode.getOperatorMobile());
            bizCount.setOperatorEmail(icbcAccountChangeNode.getOperatorEmail());
            bizCount.setOperatorIdNo(icbcAccountChangeNode.getOperatorIdNo());
            bizCount.setVendorType(icbcAccountChangeNode.getVendorType());
            bizCount.setCorprateIdType(icbcAccountChangeNode.getCorprateIdType());
            bizCount.setCorprateName(icbcAccountChangeNode.getCorprateName());
            bizCount.setCorprateMobile(icbcAccountChangeNode.getCorprateMobile());
            bizCount.setCorprateIdNo(icbcAccountChangeNode.getCorprateIdNo());
            bizCount.setCorprateIdPic1(icbcAccountChangeNode.getCorprateIdPic1());
            bizCount.setCertType(icbcAccountChangeNode.getCertType());
            bizCount.setCertPic(icbcAccountChangeNode.getCertPic());
            bizCount.setCertNo(icbcAccountChangeNode.getCertNo());
            bizCount.setAccountName(icbcAccountChangeNode.getAccountName());
            bizCount.setAccountBankProvince(icbcAccountChangeNode.getAccountBankProvince());
            bizCount.setAccountBankCity(icbcAccountChangeNode.getAccountBankCity());
            bizCount.setAccountBankNm(icbcAccountChangeNode.getAccountBankNm());
            bizCount.setAccountBankName(icbcAccountChangeNode.getAccountBankName());
            bizCount.setAccountBankCode(icbcAccountChangeNode.getAccountBankCode());
            bizCount.setAccountNo(icbcAccountChangeNode.getAccountNo());
            bizCount.setAccountMobile(icbcAccountChangeNode.getAccountMobile());
            bizCount.setSubAppid1(icbcAccountChangeNode.getSubAppid1());
            bizCount.setWxmcc1(icbcAccountChangeNode.getWxmcc1());
            bizCount.setWxName1(icbcAccountChangeNode.getWxName1());
            bizCount.setWxTel1(icbcAccountChangeNode.getWxTel1());
            bizCount.setWxmcc2(icbcAccountChangeNode.getWxmcc2());
            bizCount.setWxName2(icbcAccountChangeNode.getWxName2());
            bizCount.setWxTel2(icbcAccountChangeNode.getWxTel2());
            bizCount.setZfbMcc1(icbcAccountChangeNode.getZfbMcc1());
            bizCount.setZfbSourceId1(icbcAccountChangeNode.getZfbSourceId1());
            bizCount.setZfbRateChanl1(icbcAccountChangeNode.getZfbRateChanl1());
            bizCount.setZfbMerCusttype1(icbcAccountChangeNode.getZfbMerCusttype1());
            bizCount.setZfbMerCustnum1(icbcAccountChangeNode.getZfbMerCustnum1());
            bizCount.setZfbMerCustSort1(icbcAccountChangeNode.getZfbMerCustSort1());
            bizCount.setZfbMerCustCode1(icbcAccountChangeNode.getZfbMerCustCode1());
            bizCount.setZfbMerCard1(icbcAccountChangeNode.getZfbMerCard1());
            bizCount.setZfboperatortype1(icbcAccountChangeNode.getZfboperatortype1());
            bizCount.setZfbservicephone1(icbcAccountChangeNode.getZfbservicephone1());
            bizCount.setZfbcontact1(icbcAccountChangeNode.getZfbcontact1());
            bizCount.setZfbcontacttype1(icbcAccountChangeNode.getZfbcontacttype1());
            bizCount.setZfbmerchantremart1(icbcAccountChangeNode.getZfbmerchantremart1());
            bizCount.setZfbaddress1(icbcAccountChangeNode.getZfbaddress1());

            request.setBizContent(bizCount);

            JftApiVendorInfoModifyResponseV1 response = (JftApiVendorInfoModifyResponseV1) client.execute(request, icbcAccountChangeNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("msg_id", response.getMsgId());
                tmp.put("outVendorId", response.getOutVendorId());
                result.put("response", tmp);
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        } catch (Exception ex) {
            throw new Exception("maintain,异常", ex);
        }
        return result.toJSONString();
    }
}
