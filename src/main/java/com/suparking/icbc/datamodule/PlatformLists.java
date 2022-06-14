package com.suparking.icbc.datamodule;

import com.suparking.icbc.tools.ConstantData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PlatformLists {

    private static final String CommonUrl = "https://gw.open.icbc.com.cn";
    private static final String AbcNoSenseConnonUrl = "https://enjoy.abchina.com";

    private static final String PayDownload = "https://mpay.cf.icbc.com.cn";

    private static final String PayFileDownload = "https://mbiz.cf.icbc.com.cn";
    // 软引用队列
    static Map<String,List<PlatformNode>> platforminfo = new ConcurrentHashMap<>();
    // TODO  静态代码块 加载平台接口信息

    static{
        /** 原生支付,生成二维码支付 */
        PlatformNode platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-order");
        platformNode.setUrl(CommonUrl + "/api/jft/api/pay/gen/pay/order/qrcode/V2");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 农行无感退费 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ABC);
        platformNode.setScene("Abc-No-Sense");
        platformNode.setUrl(AbcNoSenseConnonUrl + "/yh-web/quickpaypark/refundOutParkWithHold");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 刷卡支付 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-pay");
        platformNode.setUrl(CommonUrl+"/api/jft/api/pay/qrcode/V2");
        addPlatformNode(platformNode.getPlatformId(),platformNode);

        /** H5 聚合支付 微信/支付宝/数字人民币 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-jspay");
        platformNode.setUrl(CommonUrl + "/ui/jft/ui/pay/h5/V3");
        addPlatformNode(platformNode.getPlatformId(), platformNode);
        
        /** 订单查询 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-tradeQuery");
        platformNode.setUrl(CommonUrl + "/api/jft/api/pay/query/order/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 退款 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-refund");
        platformNode.setUrl(CommonUrl + "/api/jft/api/pay/refund/accept/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 退款查询 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-refundQuery");
        platformNode.setUrl(CommonUrl + "/api/jft/api/pay/refund/query/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 获取对账 token 接口 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-token");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vacct/token/get/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 按订单结算对账单下载 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-payDownload");
        platformNode.setUrl(PayDownload + "/pay/fileDownload");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 对账单下载接口 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-payFileDownload");
        platformNode.setUrl(PayFileDownload + "/jftvendor/jftFileDownload/download");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 图片上传接口 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-picUpload");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vendor/pic/upload/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 图片下载接口 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-picDownload");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vendor/pic/download/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 子商户注册 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-vendorRegister");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vendor/info/register/V2");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 子商户修改 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-vendorModify");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vendor/info/modify/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);

        /** 子商户查询 */
        platformNode = new PlatformNode();
        platformNode.setPlatformId(ConstantData.BUTT_ICBC);
        platformNode.setScene("Icbc-vendorInfo");
        platformNode.setUrl(CommonUrl + "/api/jft/api/vendor/info/query/V1");
        addPlatformNode(platformNode.getPlatformId(), platformNode);
    }

    /**
     * 新增PlatformNode 节点
     * @param plateformNode
     */
    public  static  void addPlatformNode(String platformId,PlatformNode plateformNode)
    {
        if(!platforminfo.containsKey(platformId))
        {
            List<PlatformNode> plateformNodes = new ArrayList<>(1);
            plateformNodes.add(plateformNode);
            platforminfo.put(platformId,plateformNodes);
        }
        else
        {
            platforminfo.get(platformId).add(plateformNode);
        }
    }


    /**
     * 根据 platformid scene 返回 url
     * @param platformId
     * @param scene
     * @return
     */
    public static String getPlatformUrl(String platformId,String scene)
    {
        if(platforminfo.size() > 0)
        {
            for (PlatformNode plateformNode : platforminfo.get(platformId))
            {
                if(scene.equals(plateformNode.getScene()))
                {
                    return plateformNode.getUrl();
                }
            }
        }
        else {
            return null;
        }
        return "";
    }
}
