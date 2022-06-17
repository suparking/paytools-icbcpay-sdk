package com.suparking.icbc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suparking.icbc.datamodule.PlatformLists;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.datamodule.merch.ICBCAccountRegisterNode;
import com.suparking.icbc.datamodule.merch.ICBCAccountSearchNode;
import com.suparking.icbc.datamodule.merch.ICBCPayFileDownloadNode;
import com.suparking.icbc.datamodule.merch.ICBCPicDownloadNode;
import com.suparking.icbc.datamodule.merch.ICBCPicUploadNode;
import com.suparking.icbc.datamodule.merch.ICBCTokenNode;
import com.suparking.icbc.datamodule.projectInfoImpl.IcbcPayProjectInfo;
import com.suparking.icbc.pojo.merch.APIMainTainModel;
import com.suparking.icbc.pojo.merch.APIMerchQueryModel;
import com.suparking.icbc.pojo.merch.APIPayFileDownloadModel;
import com.suparking.icbc.pojo.merch.APIPicDownLoadModel;
import com.suparking.icbc.pojo.merch.APIPicUploadModel;
import com.suparking.icbc.pojo.merch.APITokenModel;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.SnowflakeConfig;
import com.suparking.icbc.tools.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * the merch service function.
 *
 * @author nuo-promise
 */
public class ICBCMerchServiceFunction {
    public static final Logger LOGGER = LoggerFactory.getLogger(ICBCMerchServiceFunction.class);
    private static final ICBCMerchServiceImpl icbcMerchService = new ICBCMerchServiceImpl();

    /**
     * 获取对账token
     *
     * @param projectInfo
     * @param apiTokenModel
     * @return
     */
    public JSONObject icbcToken(IcbcPayProjectInfo projectInfo, APITokenModel apiTokenModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_token_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_TOKEN);
            if (StringUtils.isNotEmpty(icbc_token_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_token_url);
                ICBCTokenNode icbcTokenNode = new ICBCTokenNode();
                icbcTokenNode.setUrl(urlMap.get("url"));
                icbcTokenNode.setAppId(projectInfo.getIcbcAppId());
                icbcTokenNode.setValidTime(apiTokenModel.getValidTime());
                icbcTokenNode.setVersion(apiTokenModel.getVersion());
                LOGGER.info("SDK-ICBC 获取对账接口参数:{}", apiTokenModel);
                String retJsonStr = icbcMerchService.icbcToken(projectInfo, apiTokenModel);
                LOGGER.info("SDK-ICBC 获取对账接口查询返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * B2C按订单结算对账单下载
     *
     * @param projectInfo
     * @param apiPayFileDownloadModel
     * @return
     */
    public JSONObject payDownload(IcbcPayProjectInfo projectInfo, APIPayFileDownloadModel apiPayFileDownloadModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_pay_file_download_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_PAY_DOWNLOAD);
            if (StringUtils.isNotEmpty(icbc_pay_file_download_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_pay_file_download_url);
                ICBCPayFileDownloadNode icbcPayFileDownloadNode = new ICBCPayFileDownloadNode();
                icbcPayFileDownloadNode.setUrl(urlMap.get("url"));
                icbcPayFileDownloadNode.setOutVendorId(apiPayFileDownloadModel.getSubInstId());
                icbcPayFileDownloadNode.setRandomValue(projectInfo.getIcbcAppId());
                icbcPayFileDownloadNode.setToken(apiPayFileDownloadModel.getToken());
                icbcPayFileDownloadNode.setAcDate(apiPayFileDownloadModel.getAcDate());
                LOGGER.info("SDK-ICBC 获取订单结算对账单接口参数:{}", apiPayFileDownloadModel);
                String retJsonStr = icbcMerchService.payDownload(icbcPayFileDownloadNode);
                LOGGER.info("SDK-ICBC 获取订单结算对账单接口返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * B2C对账单下载
     *
     * @param projectInfo
     * @param apiPayFileDownloadModel
     * @return
     */
    public JSONObject payFileDownload(IcbcPayProjectInfo projectInfo, APIPayFileDownloadModel apiPayFileDownloadModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_pay_file_download_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_PAY_FILEDOWNLOAD);
            if (StringUtils.isNotEmpty(icbc_pay_file_download_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_pay_file_download_url);
                ICBCPayFileDownloadNode icbcPayFileDownloadNode = new ICBCPayFileDownloadNode();
                icbcPayFileDownloadNode.setUrl(urlMap.get("url"));
                icbcPayFileDownloadNode.setOutVendorId(apiPayFileDownloadModel.getSubInstId());
                icbcPayFileDownloadNode.setRandomValue(projectInfo.getIcbcAppId());
                icbcPayFileDownloadNode.setToken(apiPayFileDownloadModel.getToken());
                icbcPayFileDownloadNode.setAcDate(apiPayFileDownloadModel.getAcDate());
                LOGGER.info("SDK-ICBC 获取对账单接口参数:{}", apiPayFileDownloadModel);
                String retJsonStr = icbcMerchService.payFileDownload(icbcPayFileDownloadNode);
                LOGGER.info("SDK-ICBC 获取对账单接口返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * 子商户查询
     *
     * @param projectInfo
     * @param apiMerchQueryModel
     * @return
     */
    public JSONObject icbcMerchQuery(ProjectInfo projectInfo, APIMerchQueryModel apiMerchQueryModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_merch_query_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_VENDOR_INFO);
            if (StringUtils.isNotEmpty(icbc_merch_query_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_merch_query_url);
                ICBCAccountSearchNode icbcAccountSearchNode = new ICBCAccountSearchNode();
                icbcAccountSearchNode.setUrl(urlMap.get("url"));
                icbcAccountSearchNode.setAppId(projectInfo.getIcbcAppId());
                icbcAccountSearchNode.setOutVendorid(apiMerchQueryModel.getSubInstId());
                LOGGER.info("SDK-ICBC 子商户查询参数:{}", icbcAccountSearchNode);
                String retJsonStr = icbcMerchService.merchQuery(projectInfo, icbcAccountSearchNode);
                LOGGER.info("SDK-ICBC 子商户查询返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * 影像上传接口
     *
     * @param projectInfo
     * @param APIPicUploadModel
     * @return
     */
    public JSONObject icbcImageUpload(ProjectInfo projectInfo, APIPicUploadModel APIPicUploadModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_image_upload_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_PIC_UPLOAD);
            if (StringUtils.isNotEmpty(icbc_image_upload_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_image_upload_url);
                ICBCPicUploadNode icbcPicUploadNode = new ICBCPicUploadNode();
                icbcPicUploadNode.setUrl(urlMap.get("url"));
                icbcPicUploadNode.setApp_id(projectInfo.getIcbcAppId());
                icbcPicUploadNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcPicUploadNode.setFormat("json");
                icbcPicUploadNode.setCharset("UTF-8");
                icbcPicUploadNode.setSign_type("RSA2");

                icbcPicUploadNode.setAppId(projectInfo.getIcbcAppId());
                icbcPicUploadNode.setOutVendorId(APIPicUploadModel.getSubInstId());
                icbcPicUploadNode.setImageFile(APIPicUploadModel.getImageFile());
                LOGGER.info("SDK-ICBC 图片上传参数:{}", icbcPicUploadNode);
                String retJsonStr = icbcMerchService.imageUpload(projectInfo, icbcPicUploadNode);
                LOGGER.info("SDK-ICBC 图片上传返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * 影像下载接口
     *
     * @param projectInfo
     * @param apiPicDownLoadModel
     * @return
     */
    public JSONObject icbcImageDownload(ProjectInfo projectInfo, APIPicDownLoadModel apiPicDownLoadModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_image_download_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_PIC_DOWNLOAD);
            if (StringUtils.isNotEmpty(icbc_image_download_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_image_download_url);
                ICBCPicDownloadNode icbcPicDownloadNode = new ICBCPicDownloadNode();
                icbcPicDownloadNode.setUrl(urlMap.get("url"));
                icbcPicDownloadNode.setApp_id(projectInfo.getIcbcAppId());
                icbcPicDownloadNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcPicDownloadNode.setFormat("json");
                icbcPicDownloadNode.setCharset("UTF-8");
                icbcPicDownloadNode.setSign_type("RSA2");

                icbcPicDownloadNode.setAppId(projectInfo.getIcbcAppId());
                icbcPicDownloadNode.setOutVendorId(apiPicDownLoadModel.getSubInstId());
                icbcPicDownloadNode.setImageKey(apiPicDownLoadModel.getImageKey());
                LOGGER.info("SDK-ICBC 图片下载参数:{}", icbcPicDownloadNode);
                String retJsonStr = icbcMerchService.imageDownload(projectInfo, icbcPicDownloadNode);
                LOGGER.info("SDK-ICBC 图片下载返回:{}", retJsonStr);

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }

    /**
     * 进件
     *
     * @param projectInfo
     * @param apiMainTainModel
     * @return
     */
    public JSONObject icbcMerchMainTain(ProjectInfo projectInfo, APIMainTainModel apiMainTainModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_vendor_register_url = "";
            if (ConstantData.TRANSTYPE_REGIST.equals(apiMainTainModel.getTransType())) {
                icbc_vendor_register_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_VENDOR_REGISTER);
            }

            if (ConstantData.TRANSTYPE_UPDATE.equals(apiMainTainModel.getTransType())) {
                icbc_vendor_register_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_VENDOR_MODIFY);
            }

            if (StringUtils.isNotEmpty(icbc_vendor_register_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_vendor_register_url);
                ICBCAccountRegisterNode icbcAccountRegisterNode = new ICBCAccountRegisterNode();
                icbcAccountRegisterNode.setUrl(urlMap.get("url"));
                icbcAccountRegisterNode.setApp_id(projectInfo.getIcbcAppId());
                icbcAccountRegisterNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcAccountRegisterNode.setFormat("json");
                icbcAccountRegisterNode.setCharset("UTF-8");
                icbcAccountRegisterNode.setSign_type("RSA2");

                icbcAccountRegisterNode.setAppId(projectInfo.getIcbcAppId());
                icbcAccountRegisterNode.setOutUpperVendorId(projectInfo.getMerchantId());
                icbcAccountRegisterNode.setOutVendorId(apiMainTainModel.getSubInstId());
                icbcAccountRegisterNode.setOutUserId(apiMainTainModel.getProjectNo() + TimeUtils.getPayDate());
                icbcAccountRegisterNode.setVendorName(apiMainTainModel.getSubInstName());
                icbcAccountRegisterNode.setVendorShortName(apiMainTainModel.getSubInstShortName());
                icbcAccountRegisterNode.setVendorPhone(apiMainTainModel.getServicePhone());
                icbcAccountRegisterNode.setVendorEmail(apiMainTainModel.getEmail());
                icbcAccountRegisterNode.setProvince(apiMainTainModel.getProvince());
                icbcAccountRegisterNode.setCity(apiMainTainModel.getCity());
                icbcAccountRegisterNode.setCounty(apiMainTainModel.getDistrict());
                icbcAccountRegisterNode.setAddress(apiMainTainModel.getAddress());
                icbcAccountRegisterNode.setPostcode(apiMainTainModel.getPostcode());
                icbcAccountRegisterNode.setOperatorName(apiMainTainModel.getLinkman());
                icbcAccountRegisterNode.setOperatorMobile(apiMainTainModel.getPhone());
                icbcAccountRegisterNode.setOperatorEmail(apiMainTainModel.getEmail());
                icbcAccountRegisterNode.setOperatorIdNo(apiMainTainModel.getOperatorIdNo());
                icbcAccountRegisterNode.setVendorType(apiMainTainModel.getCorpType());
                icbcAccountRegisterNode.setCorprateIdType(apiMainTainModel.getOwnerIdType());
                icbcAccountRegisterNode.setCorprateName(apiMainTainModel.getOwner());
                icbcAccountRegisterNode.setCorprateMobile(apiMainTainModel.getOwnerPhone());
                icbcAccountRegisterNode.setCorprateIdNo(apiMainTainModel.getOwnerIDNo());
                // 调用 图片上传 获取 key 值
                icbcAccountRegisterNode.setCorprateIdPic1("需要上传");
                icbcAccountRegisterNode.setCertType(apiMainTainModel.getCertType());
                // 调用 图片上传 营业执照
                icbcAccountRegisterNode.setCertPic("需要上传");
                icbcAccountRegisterNode.setCertNo(apiMainTainModel.getCertNo());
                icbcAccountRegisterNode.setAccountName(apiMainTainModel.getSettAccName());
                icbcAccountRegisterNode.setAccountBankProvince(apiMainTainModel.getSettAccProvince());
                icbcAccountRegisterNode.setAccountBankCity(apiMainTainModel.getSettAccCity());
                icbcAccountRegisterNode.setAccountBankNm(apiMainTainModel.getAccountBankNm());
                icbcAccountRegisterNode.setAccountBankName(apiMainTainModel.getSettAccAttr1());
                icbcAccountRegisterNode.setAccountBankCode(apiMainTainModel.getSettAccAttr2());
                icbcAccountRegisterNode.setAccountNo(apiMainTainModel.getSettAccIdNo());
                icbcAccountRegisterNode.setAccountMobile(apiMainTainModel.getSettAccMobile());
                icbcAccountRegisterNode.setSubAppid1(apiMainTainModel.getSubAppid1());
                icbcAccountRegisterNode.setWxmcc1(apiMainTainModel.getWxmcc1());
                icbcAccountRegisterNode.setWxName1(apiMainTainModel.getWxName1());
                icbcAccountRegisterNode.setWxTel1(apiMainTainModel.getWxTel1());
                icbcAccountRegisterNode.setWxmcc2(apiMainTainModel.getWxmcc2());
                icbcAccountRegisterNode.setWxName2(apiMainTainModel.getWxName2());
                icbcAccountRegisterNode.setWxTel2(apiMainTainModel.getWxTel2());
                icbcAccountRegisterNode.setZfbMcc1(apiMainTainModel.getZfbMcc1());
                icbcAccountRegisterNode.setZfbSourceId1(apiMainTainModel.getZfbSourceId1());
                icbcAccountRegisterNode.setZfbRateChanl1(apiMainTainModel.getZfbRateChanl1());
                icbcAccountRegisterNode.setZfbMerCusttype1(apiMainTainModel.getZfbMerCusttype1());
                icbcAccountRegisterNode.setZfbMerCustnum1(apiMainTainModel.getZfbMerCustnum1());
                icbcAccountRegisterNode.setZfbMerCustSort1(apiMainTainModel.getZfbMerCustSort1());
                icbcAccountRegisterNode.setZfbMerCustCode1(apiMainTainModel.getZfbMerCustCode1());
                icbcAccountRegisterNode.setZfbMerCard1(apiMainTainModel.getZfbMerCard1());
                icbcAccountRegisterNode.setZfboperatortype1(apiMainTainModel.getZfboperatortype1());
                icbcAccountRegisterNode.setZfbservicephone1(apiMainTainModel.getZfbservicephone1());
                icbcAccountRegisterNode.setZfbcontact1(apiMainTainModel.getZfbcontact1());
                icbcAccountRegisterNode.setZfbcontacttype1(apiMainTainModel.getZfbcontacttype1());
                icbcAccountRegisterNode.setZfbmerchantremart1(apiMainTainModel.getZfbmerchantremart1());
                icbcAccountRegisterNode.setZfbaddress1(apiMainTainModel.getZfbaddress1());

                String retJsonStr = "";
                if (ConstantData.TRANSTYPE_REGIST.equals(apiMainTainModel.getTransType())) {
                    LOGGER.info("SDK-ICBC 进件参数:{}", icbcAccountRegisterNode);
                    retJsonStr = icbcMerchService.maintain(projectInfo, icbcAccountRegisterNode);
                    LOGGER.info("SDK-ICBC 进件参数返回:{}", retJsonStr);
                }

                if (ConstantData.TRANSTYPE_UPDATE.equals(apiMainTainModel.getTransType())) {
                    LOGGER.info("SDK-ICBC 进件修改参数:{}", icbcAccountRegisterNode);
                    retJsonStr = icbcMerchService.maintainModify(projectInfo, icbcAccountRegisterNode);
                    LOGGER.info("SDK-ICBC 进件修改参数返回:{}", retJsonStr);
                }

                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    return JSON.parseObject(retJsonStr);
                }

            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }


}
