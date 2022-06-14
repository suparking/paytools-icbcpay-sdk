package com.suparking.icbc.controller;

import com.alibaba.fastjson.JSONObject;
import com.icbc.api.internal.util.StringUtils;
import com.suparking.icbc.datamodule.projectInfoImpl.IcbcPayProjectInfo;
import com.suparking.icbc.pojo.GenericResponse;
import com.suparking.icbc.pojo.merch.APIDayBillModel;
import com.suparking.icbc.pojo.merch.APIImageUploadModel;
import com.suparking.icbc.pojo.merch.APIMainTainModel;
import com.suparking.icbc.pojo.merch.APIMerchQueryModel;
import com.suparking.icbc.pojo.merch.APIVerifyAcctModel;
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
    private static ICBCMerchServiceFunction icbcMerchServiceFunction = new ICBCMerchServiceFunction();

    /**
     * 日结账单查询
     * @param apiDayBillModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse dayBill(APIDayBillModel apiDayBillModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.DAY_BILL, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject dayBillObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiDayBillModel.getProjectNo())) {
                apiDayBillModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "日结账单查询成功!");
            dayBillObj = icbcMerchServiceFunction.icbcDayBill(projectInfo, apiDayBillModel);
            if (!dayBillObj.containsKey("result_code") ||
                    !dayBillObj.containsKey("result_desc") ||
                    !((String) dayBillObj.get("result_code")).contains("0000") ||
                    !((String) dayBillObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", dayBillObj.get("result_code"));
                retJsonObj.put("result_desc", dayBillObj.get("result_desc"));
            } else {
                retJsonObj.put("response", dayBillObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.DAY_BILL, "日结账单查询:dayBill出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.DAY_BILL, retJsonObj);
    }

    /**
     * 子商户验证
     * @param apiVerifyAcctModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse verifyAcct(APIVerifyAcctModel apiVerifyAcctModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.VERIFY_ACCT, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject verifyAcctObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiVerifyAcctModel.getProjectNo())) {
                apiVerifyAcctModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "子商户账号验证成功!");
            verifyAcctObj = icbcMerchServiceFunction.icbcVerifyAcct(projectInfo, apiVerifyAcctModel);
            if (!verifyAcctObj.containsKey("result_code") ||
                    !verifyAcctObj.containsKey("result_desc") ||
                    !((String) verifyAcctObj.get("result_code")).contains("0000") ||
                    !((String) verifyAcctObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", verifyAcctObj.get("result_code"));
                retJsonObj.put("result_desc", verifyAcctObj.get("result_desc"));
            }else {
                retJsonObj.put("response", verifyAcctObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.VERIFY_ACCT, "子商户账号验证:verifyAcct出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.VERIFY_ACCT, retJsonObj);
    }
    /**
     * 影像上传
     * @param apiImageUploadModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse imageUpload(APIImageUploadModel apiImageUploadModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.IMAGE_UPLOAD, ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject imageUploadObj = new JSONObject();
        try {
            if (StringUtils.isEmpty(apiImageUploadModel.getProjectNo())) {
                apiImageUploadModel.setProjectNo("");
            }
            retJsonObj.put("result_code", "0");
            retJsonObj.put("result_desc", "影像上传成功!");
            imageUploadObj = icbcMerchServiceFunction.icbcImageUpload(projectInfo, apiImageUploadModel);
            if (!imageUploadObj.containsKey("result_code") ||
                    !imageUploadObj.containsKey("result_desc") ||
                    !((String) imageUploadObj.get("result_code")).contains("0000") ||
                    !((String) imageUploadObj.get("result_desc")).contains("成功")) {
                retJsonObj.put("result_code", imageUploadObj.get("result_code"));
                retJsonObj.put("result_desc", imageUploadObj.get("result_desc"));
            }else {
                retJsonObj.put("response", imageUploadObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.IMAGE_UPLOAD, "影像上传:imageUpload出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.IMAGE_UPLOAD, retJsonObj);
    }

    /**
     * 子商户查询
     * @param apiMerchQueryModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse merchQuery(APIMerchQueryModel apiMerchQueryModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.MERCH_QUERY, ConstantData.PAY_CENTER_INFO);
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
            }else {
                retJsonObj.put("response", merchQueryObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.MERCH_QUERY, "子商户查询:merchQuery出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.MERCH_QUERY, retJsonObj);
    }
    /**
     * 进件
     * @param apiMainTainModel
     * @param initPayStr
     * @return
     */
    public static GenericResponse maintain(APIMainTainModel apiMainTainModel, String initPayStr) {
        IcbcPayProjectInfo projectInfo = IcbcPayController.getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus()) {
            return ResponseFormat.retParam(1004, ConstantData.MAIN_TAIL, ConstantData.PAY_CENTER_INFO);
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
            }else {
                retJsonObj.put("response", mainTainObj.getJSONObject("response"));
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.MAIN_TAIL, "子商户进件:maintail出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.MAIN_TAIL, retJsonObj);
    }
}
