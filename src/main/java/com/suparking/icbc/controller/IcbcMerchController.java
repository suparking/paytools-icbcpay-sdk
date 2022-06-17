package com.suparking.icbc.controller;

import com.alibaba.fastjson.JSONObject;
import com.icbc.api.internal.util.StringUtils;
import com.suparking.icbc.datamodule.projectInfoImpl.IcbcPayProjectInfo;
import com.suparking.icbc.pojo.GenericResponse;
import com.suparking.icbc.pojo.merch.APIMainTainModel;
import com.suparking.icbc.pojo.merch.APIMerchQueryModel;
import com.suparking.icbc.pojo.merch.APIPayFileDownloadModel;
import com.suparking.icbc.pojo.merch.APIPicDownLoadModel;
import com.suparking.icbc.pojo.merch.APIPicUploadModel;
import com.suparking.icbc.pojo.merch.APITokenModel;
import com.suparking.icbc.service.ICBCMerchServiceFunction;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.ResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * the icbc merch function.
 *
 * @author nuo-promise
 */
public class IcbcMerchController {
    public static final Logger LOGGER = LoggerFactory.getLogger(IcbcMerchController.class);
    private static final ICBCMerchServiceFunction icbcMerchServiceFunction = new ICBCMerchServiceFunction();

    /**
     * 获取token
     *
     * @param apiTokenModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse token(APITokenModel apiTokenModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_TOKEN, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject tokenObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiTokenModel.getProjectNo())) {
                apiTokenModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "获取token成功!");
            tokenObj = icbcMerchServiceFunction.icbcToken(projectInfo, apiTokenModel);
            if (!tokenObj.containsKey("result_code") ||
                    !tokenObj.containsKey("result_desc") ||
                    !((String) tokenObj.get("result_code")).contains("0000") ||
                    !((String) tokenObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", tokenObj.get("result_code"));
                retJsonObj.put("result_desc", tokenObj.get("result_desc"));
            } else {
                retJsonObj.put("response", tokenObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_TOKEN, "获取token:Icbc-token出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_TOKEN, retJsonObj);
    }

    /**
     * B2C按订单结算对账单下载
     *
     * @param apiPayFileDownloadModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse payDownload(APIPayFileDownloadModel apiPayFileDownloadModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_PAY_DOWNLOAD, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject tokenObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiPayFileDownloadModel.getProjectNo())) {
                apiPayFileDownloadModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "订单结算对账单下载成功!");
            tokenObj = icbcMerchServiceFunction.payDownload(projectInfo, apiPayFileDownloadModel);
            if (!tokenObj.containsKey("result_code") ||
                    !tokenObj.containsKey("result_desc") ||
                    !((String) tokenObj.get("result_code")).contains("0000") ||
                    !((String) tokenObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", tokenObj.get("result_code"));
                retJsonObj.put("result_desc", tokenObj.get("result_desc"));
            } else {
                retJsonObj.put("response", tokenObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_PAY_DOWNLOAD, "获取订单结算对账单:payDownload出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_PAY_DOWNLOAD, retJsonObj);
    }

    /**
     * B2C对账单下载
     *
     * @param apiPayFileDownloadModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse payFileDownload(APIPayFileDownloadModel apiPayFileDownloadModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_PAY_FILEDOWNLOAD, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject tokenObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiPayFileDownloadModel.getProjectNo())) {
                apiPayFileDownloadModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "对账单下载成功!");
            tokenObj = icbcMerchServiceFunction.payFileDownload(projectInfo, apiPayFileDownloadModel);
            if (!tokenObj.containsKey("result_code") ||
                    !tokenObj.containsKey("result_desc") ||
                    !((String) tokenObj.get("result_code")).contains("0000") ||
                    !((String) tokenObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", tokenObj.get("result_code"));
                retJsonObj.put("result_desc", tokenObj.get("result_desc"));
            } else {
                retJsonObj.put("response", tokenObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_PAY_FILEDOWNLOAD, "获取对账单:pauFileDownload出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_PAY_FILEDOWNLOAD, retJsonObj);
    }

    /**
     * 影像上传
     *
     * @param apiPicUploadModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse imageUpload(APIPicUploadModel apiPicUploadModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_PIC_UPLOAD, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject imageUploadObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiPicUploadModel.getProjectNo())) {
                apiPicUploadModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "影像上传成功!");
            imageUploadObj = icbcMerchServiceFunction.icbcImageUpload(projectInfo, apiPicUploadModel);
            if (!imageUploadObj.containsKey("result_code") ||
                    !imageUploadObj.containsKey("result_desc") ||
                    !((String) imageUploadObj.get("result_code")).contains("0000") ||
                    !((String) imageUploadObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", imageUploadObj.get("result_code"));
                retJsonObj.put("result_desc", imageUploadObj.get("result_desc"));
            } else {
                retJsonObj.put("response", imageUploadObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_PIC_UPLOAD, "影像上传:imageUpload出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_PIC_UPLOAD, retJsonObj);
    }

    /**
     * 影像下载
     *
     * @param apiPicDownLoadModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse imageDownload(APIPicDownLoadModel apiPicDownLoadModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_PIC_DOWNLOAD, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject imageDownloadObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiPicDownLoadModel.getProjectNo())) {
                apiPicDownLoadModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "影像下载成功!");
            imageDownloadObj = icbcMerchServiceFunction.icbcImageDownload(projectInfo, apiPicDownLoadModel);
            if (!imageDownloadObj.containsKey("result_code") ||
                    !imageDownloadObj.containsKey("result_desc") ||
                    !((String) imageDownloadObj.get("result_code")).contains("0000") ||
                    !((String) imageDownloadObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", imageDownloadObj.get("result_code"));
                retJsonObj.put("result_desc", imageDownloadObj.get("result_desc"));
            } else {
                retJsonObj.put("response", imageDownloadObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_PIC_DOWNLOAD, "影像下载:imageDownload出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_PIC_DOWNLOAD, retJsonObj);
    }

    /**
     * 子商户查询
     *
     * @param apiMerchQueryModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse merchQuery(APIMerchQueryModel apiMerchQueryModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_VENDOR_INFO, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject merchQueryObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiMerchQueryModel.getProjectNo())) {
                apiMerchQueryModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "子商户查询成功!");
            merchQueryObj = icbcMerchServiceFunction.icbcMerchQuery(projectInfo, apiMerchQueryModel);
            if (!merchQueryObj.containsKey("result_code") ||
                    !merchQueryObj.containsKey("result_desc") ||
                    !((String) merchQueryObj.get("result_code")).contains("0000") ||
                    !((String) merchQueryObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", merchQueryObj.get("result_code"));
                retJsonObj.put("result_desc", merchQueryObj.get("result_desc"));
            } else {
                retJsonObj.put("response", merchQueryObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.MERCH_QUERY, "子商户查询:merchQuery出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.MERCH_QUERY, retJsonObj);
    }

    /**
     * 进件
     *
     * @param apiMainTainModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse maintain(APIMainTainModel apiMainTainModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.ICBC_VENDOR_REGISTER, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject mainTainObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiMainTainModel.getProjectNo())) {
                apiMainTainModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "信息处理成功!");
            mainTainObj = icbcMerchServiceFunction.icbcMerchMainTain(projectInfo, apiMainTainModel);
            if (!mainTainObj.containsKey("result_code") ||
                    !mainTainObj.containsKey("result_desc") ||
                    !((String) mainTainObj.get("result_code")).contains("0000") ||
                    !((String) mainTainObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", mainTainObj.get("result_code"));
                retJsonObj.put("result_desc", mainTainObj.get("result_desc"));
            } else {
                retJsonObj.put("response", mainTainObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ICBC_VENDOR_REGISTER, "子商户进件:maintain出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ICBC_VENDOR_REGISTER, retJsonObj);
    }
}
