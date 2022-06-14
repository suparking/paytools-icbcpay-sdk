package com.suparking.icbc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suparking.icbc.datamodule.PlatformLists;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.datamodule.merch.ICBCAccountRegisterNode;
import com.suparking.icbc.datamodule.merch.ICBCImageUploadNode;
import com.suparking.icbc.pojo.merch.APIDayBillModel;
import com.suparking.icbc.pojo.merch.APIImageUploadModel;
import com.suparking.icbc.pojo.merch.APIMainTainModel;
import com.suparking.icbc.pojo.merch.APIMerchQueryModel;
import com.suparking.icbc.pojo.merch.APIVerifyAcctModel;
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
     * 日结账单
     * @param projectInfo
     * @param apiDayBillModel
     * @return
     */
    public JSONObject icbcDayBill(ProjectInfo projectInfo, APIDayBillModel apiDayBillModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_day_bill_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.DAY_BILL);
            if (!StringUtils.isEmpty(icbc_day_bill_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_day_bill_url);
                ICBCDayBill icbcDayBill = new ICBCDayBill();
                icbcDayBill.setUrl(urlMap.get("url"));
                icbcDayBill.setService(urlMap.get("service"));
                icbcDayBill.setSubInstId(apiDayBillModel.getSubInstId());
                icbcDayBill.setQueryDate(apiDayBillModel.getQueryDate());
                LOGGER.info("SDK-ICBC 子商户日结账单参数:{}", icbcDayBill.toString());
                String retJsonStr = icbcMerchService.dayBill(projectInfo, icbcDayBill);
                LOGGER.info("SDK-ICBC 子商户日结账单返回:{}", retJsonStr);
                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    if(!retJsonObj.containsKey("ICBC_API_RETCODE")||
                            !retJsonObj.containsKey("ICBC_API_RETMSG")||
                            0 != (Integer)retJsonObj.get("ICBC_API_RETCODE"))
                    {
                        result.put("result_code",retJsonObj.get("ICBC_API_RETCODE").toString());
                        result.put("result_desc",retJsonObj.get("ICBC_API_RETMSG"));
                    }
                    else
                    {
                        if (!retJsonObj.containsKey("hostRspCode") ||
                                !retJsonObj.containsKey("hostRspMsg") ||
                                !((String)retJsonObj.get("hostRspCode")).contains("0000") ||
                                !((String)retJsonObj.get("hostRspMsg")).contains("成功")) {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                        } else {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                            result.put("response",retJsonObj.get("response"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            result.put("result_code", "20004");
            result.put("result_desc", ex.getMessage());
        }
        return result;
    }
    /**
     * 子商户账号验证
     * @param projectInfo
     * @param apiVerifyAcctModel
     * @return
     */
    public JSONObject icbcVerifyAcct(ProjectInfo projectInfo, APIVerifyAcctModel apiVerifyAcctModel) {
       JSONObject result = new JSONObject();
       try {
          String icbc_verify_acct_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.VERIFY_ACCT);
          if (StringUtils.isNotEmpty(icbc_verify_acct_url)) {
              Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_verify_acct_url);
              ICBCVerifyAcctNode icbcVerifyAcctNode = new ICBCVerifyAcctNode();
              icbcVerifyAcctNode.setUrl(urlMap.get("url"));
              icbcVerifyAcctNode.setService(urlMap.get("service"));
              icbcVerifyAcctNode.setSubInstId(apiVerifyAcctModel.getSubInstId());
              icbcVerifyAcctNode.setAmount(apiVerifyAcctModel.getAmount());
              LOGGER.info("SDK-ICBC 子商户银行账号验证参数:{}", icbcVerifyAcctNode.toString());
              String retJsonStr = icbcMerchService.verifyacct(projectInfo, icbcVerifyAcctNode);
              LOGGER.info("SDK-ICBC 子商户银行账号验证返回:{}", retJsonStr);
              if (StringUtils.isEmpty(retJsonStr)) {
                  result.put("result_code", "20003");
              } else {
                  JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                  if(!retJsonObj.containsKey("ICBC_API_RETCODE")||
                          !retJsonObj.containsKey("ICBC_API_RETMSG")||
                          0 != (Integer)retJsonObj.get("ICBC_API_RETCODE"))
                  {
                      result.put("result_code",retJsonObj.get("ICBC_API_RETCODE").toString());
                      result.put("result_desc",retJsonObj.get("ICBC_API_RETMSG"));
                  }
                  else
                  {
                      if (!retJsonObj.containsKey("hostRspCode") ||
                              !retJsonObj.containsKey("hostRspMsg") ||
                              !((String)retJsonObj.get("hostRspCode")).contains("0000") ||
                              !((String)retJsonObj.get("hostRspMsg")).contains("成功")) {
                          result.put("result_code", retJsonObj.get("hostRspCode"));
                          result.put("result_desc", retJsonObj.get("hostRspMsg"));
                      } else {
                          result.put("result_code", retJsonObj.get("hostRspCode"));
                          result.put("result_desc", retJsonObj.get("hostRspMsg"));
                          result.put("response",retJsonObj.get("response"));
                      }
                  }
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
     * @param projectInfo
     * @param apiMerchQueryModel
     * @return
     */
    public JSONObject icbcMerchQuery(ProjectInfo projectInfo, APIMerchQueryModel apiMerchQueryModel) {
        JSONObject result = new JSONObject();
       try {
          String icbc_merch_query_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.MERCH_QUERY);
          if (StringUtils.isNotEmpty(icbc_merch_query_url)) {
              Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_merch_query_url);
              ICBCMerchQuery icbcMerchQuery = new ICBCMerchQuery();
              icbcMerchQuery.setUrl(urlMap.get("url"));
              icbcMerchQuery.setService(urlMap.get("service"));
              icbcMerchQuery.setSubInstId(apiMerchQueryModel.getSubInstId());
              LOGGER.info("SDK-ICBC 子商户查询参数:{}",icbcMerchQuery.toString());
              String retJsonStr = icbcMerchService.merchQuery(projectInfo, icbcMerchQuery);
              LOGGER.info("SDK-ICBC 子商户查询返回:{}", retJsonStr);
              if (StringUtils.isEmpty(retJsonStr)) {
                  result.put("result_code", "20003");
              } else {
                  JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                  if(!retJsonObj.containsKey("ICBC_API_RETCODE")||
                          !retJsonObj.containsKey("ICBC_API_RETMSG")||
                          0 != (Integer)retJsonObj.get("ICBC_API_RETCODE"))
                  {
                      result.put("result_code",retJsonObj.get("ICBC_API_RETCODE").toString());
                      result.put("result_desc",retJsonObj.get("ICBC_API_RETMSG"));
                  }
                  else
                  {
                      if (!retJsonObj.containsKey("hostRspCode") ||
                              !retJsonObj.containsKey("hostRspMsg") ||
                              !((String)retJsonObj.get("hostRspCode")).contains("0000") ||
                              !((String)retJsonObj.get("hostRspMsg")).contains("成功")) {
                          result.put("result_code", retJsonObj.get("hostRspCode"));
                          result.put("result_desc", retJsonObj.get("hostRspMsg"));
                      } else {
                          result.put("result_code", retJsonObj.get("hostRspCode"));
                          result.put("result_desc", retJsonObj.get("hostRspMsg"));
                          result.put("response",retJsonObj.get("response"));
                      }
                  }
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
     * @param projectInfo
     * @param apiImageUploadModel
     * @return
     */
    public JSONObject icbcImageUpload(ProjectInfo projectInfo, APIImageUploadModel apiImageUploadModel) {
        JSONObject result = new JSONObject();
        try {
            String icbc_image_upload_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.IMAGE_UPLOAD);
            if (StringUtils.isNotEmpty(icbc_image_upload_url)) {
                Map<String, String> urlMap = ICBCServiceFunction.getUrls(icbc_image_upload_url);
                ICBCImageUploadNode icbcImageUploadNode = new ICBCImageUploadNode();
                icbcImageUploadNode.setUrl(urlMap.get("url"));
                icbcImageUploadNode.setService(urlMap.get("service"));
                icbcImageUploadNode.setSubInstId(apiImageUploadModel.getSubInstId());
                icbcImageUploadNode.setImageFileName(apiImageUploadModel.getImageFileName());
                icbcImageUploadNode.setImageFilePath(apiImageUploadModel.getImageFilePath());
                icbcImageUploadNode.setImageType(apiImageUploadModel.getImageType());
                icbcImageUploadNode.setFinishFlag(apiImageUploadModel.getFinishFlag());
                LOGGER.info("SDK-ICBC 图片上传参数:{}",icbcImageUploadNode.toString());
                String retJsonStr = icbcMerchService.imageUpload(projectInfo, icbcImageUploadNode);
                LOGGER.info("SDK-ICBC 图片上传返回:{}", retJsonStr);
                if (StringUtils.isEmpty(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    if(!retJsonObj.containsKey("ICBC_API_RETCODE")||
                            !retJsonObj.containsKey("ICBC_API_RETMSG")||
                            0 != (Integer)retJsonObj.get("ICBC_API_RETCODE"))
                    {
                        result.put("result_code",retJsonObj.get("ICBC_API_RETCODE").toString());
                        result.put("result_desc",retJsonObj.get("ICBC_API_RETMSG"));
                    }
                    else
                    {
                        if (!retJsonObj.containsKey("hostRspCode") ||
                                !retJsonObj.containsKey("hostRspMsg") ||
                                !((String)retJsonObj.get("hostRspCode")).contains("0000") ||
                                !((String)retJsonObj.get("hostRspMsg")).contains("成功")) {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                        } else {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                            result.put("response",retJsonObj.get("response"));
                        }
                    }
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
     * @param projectInfo
     * @param apiMainTainModel
     * @return
     */
    public JSONObject icbcMerchMainTain(ProjectInfo projectInfo, APIMainTainModel apiMainTainModel) {
       JSONObject result = new JSONObject();
       try {
           String icbc_vendor_register_url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC, ConstantData.ICBC_VENDOR_REGISTER);
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

               LOGGER.info("SDK-ICBC 进件参数:{}",icbcAccountRegisterNode);
               String retJsonStr = icbcMerchService.maintain(projectInfo, icbcAccountRegisterNode);
               LOGGER.info("SDK-ICBC 进件参数返回:{}", retJsonStr);
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
