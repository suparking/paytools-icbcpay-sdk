package com.suparking.icbc.tools;

public class ConstantData {

    /** 2020-04-15 新增支付渠道修改 */
    public static final char ICBC_CHANNEL = '2';
    /** 2020-4-15 新增 支付类型代码 */
    public static final char ICBC_ALI = 'A';
    public static final char ICBC_WX = 'W';

    public static final char ICBC_DI = 'D';

    public static final String CALL_FILE = "file";
    public static final String CALL_NET = "net";

    /** NATIVE :=> 微信支付宝 二维码支付 WXJSPAY :=> 微信小程序公众号支付 ALIJSPAY :=> 支付宝服务窗支付 */
    public static final int PAY_NATIVE = 1;
    public static final int PAY_WXJSPAY = 2;
    public static final int PAY_ALIJSPAY = 3;
    public static final int PAY_UNJSPAY = 4;
    // 支付类型
    public static final int PAY_WX = 1;
    public static final int PAY_ALI = 2;
    //第三方服务对接商
    public static final String BUTT_ICBC = "ICBC";
    //对接第三方支付平台URL
    public static final String ICBC_ORDER = "Icbc-order";
    public static final String ICBC_PAY = "Icbc-pay";
    public static final String ICBC_REFUND = "Icbc-refund";
    public static final String ICBC_REFUNDQUERY = "Icbc-refundQuery";
    public static final String ICBC_ORDERQUERY = "Icbc-tradeQuery";

    public static final String ICBC_JSAPI_ORDER = "Icbc-jspay";

    /** 新增农行无感支付退款*/
    public static final String ABC_NO_SENSE = "Abc-No-Sense";
    public static final String CLOSED = "close";
    // 子账户提现使用
    public static final String INFO_QUERY = "infoquery";
    public static final String WITH_DRAW = "withdraw";
    // 子商户进件
    public static final String ICBC_VENDOR_REGISTER = "Icbc-vendorRegister";
    public static final String ICBC_VENDOR_MODIFY = "Icbc-vendorModify";
    public static final String ICBC_VENDOR_INFO = "Icbc-vendorInfo";
    public static final String ICBC_PIC_UPLOAD = "Icbc-picUpload";
    public static final String ICBC_PIC_DOWNLOAD = "Icbc-picDownload";
    public static final String VERIFY_ACCT = "verifyacct";
    public static final String IMAGE_UPLOAD = "imageupload";
    public static final String MERCH_QUERY = "merch-query";
    public static final String DAY_BILL = "daybill";

    // 对账
    public static final String ICBC_TOKEN = "Icbc-token";
    public static final String ICBC_PAY_DOWNLOAD = "Icbc-payDownload";
    public static final String ICBC_PAY_FILEDOWNLOAD = "Icbc-payFileDownload";
    public static final String PAY_FILE_DOWNLOAD_SUCCESS = "0000";
    public static final String PAY_FILE_DOWNLOAD_PATH = "/opt/suparking/file/pay/";

    // 进件类型
    public static final String TRANSTYPE_REGIST = "2";
    public static final String TRANSTYPE_UPDATE = "4";

    //ICBC 支付方式
    public static final String STLFLAG_GENERAL = "00";
    public static final String STLFLAG_SECURITY = "01";
    public static final String STLFLAG_SHARE = "02";

    // TODO 农行对接
    public static final String BUTT_ABC = "ABC";
    public static final String ABC_CURRENCYCODE = "156";
    public static final String ABC_INSTALLMENTMARK = "0";
    public static final String ABC_COMMODITYTYPE = "0599";
    public static final String ABC_QUERYDETAIL_STATUS = "0";
    public static final String ABC_QUERYDETAIL_DETAIL = "1";
    public static final String ABC_MODELFLAG_BIG = "1";
    public static final String ABC_MODELFLAG_NOBIG = "0";
    public static final String ABC_MERCHANTFLAG_W = "W";
    public static final String ABC_MERCHANTFLAG_Z = "Z";
    //订单生成部分
    public static final String ORDER_PAYMENT_PAY = "P";
    public static final String ORDER_PAYMENT_RETURN = "R";
    public static final String ORDER_PAYMENT_WX = "W";
    public static final String ORDER_PAYMENT_ALI = "A";
    public static final String ORDER_PAYMENT_UNION = "U";
    //公司信息
    public static final String PAY_CENTER_INFO = "SuparingAbcPay";
    //支付状态
    public static final Integer STARTPAY = 1;
    public static final Integer WAITPAY = 2;
    public static final Integer SUCESSPAY = 3;
    public static final Integer REVERSEING = 4;
    public static final Integer SUCESSREVERSE = 5;
    public static final Integer ABNOREORDER = 6;
    public static final Integer CLOSEORDER = 7;
    //swift pay status
    public static final String PAY_SUCCESS = "SUCCESS";
    public static final String PAY_REFUND = "REFUND";
    public static final String PAY_NOTPAY = "NOTPAY";
    public static final String PAY_CLOSED = "CLOSED";
    public static final String PAY_REVOKED = "REVOKED";
    public static final String PAY_USERPAYING = "USERPAYING";
    public static final String PAY_PAYERROR = "PAYERROR";
    public static final String PAY_FINISHED = "FINISHED";
    public static final String PAY_UNDEFINED = "NOTEXIST";

    // ABC 等待用户输入密码 等待
    public static final String ABCUSERPAYING = "AP6419";

    //退款状态
    public static final String REFUND_FAILED = "FAILED";
    public static final String REFUND_SUCCESS = "SUCCESS";


    //
    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_JSAPI_MINI = "JSAPI_MINI";
    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_APP = "APP";

    // 刷卡支付 错误 码
    public static  final String WAITPWD = "10003";
    public static final String USERPAYING = "USERPAYING";
    public static final String ICBCUSERPAYING = "FFFFF";
    // 交易超时 等待用户输入密码
    public static final String ICBCPAYTIMEOUT ="B9997";

    public static final String REFUNDFAILDORSUCESS = "B0705";

    // 微信 公众号 小程序 trade_type
    /** 公众号*/
    public static final String WETCHATOFFICAL = "JSAPI";
    /** 小程序 */
    public static final String WETCHATMINI = "JSAPI_MINI";
    /** 原声接口 */
    public static final String COMMONTRADETYPE = "NATIVE";
    /** APP 支付 */
    public static final String APPTRADETYPE = "APP";
    /** 支付宝 服务窗支付 */
    public static final String ALIJSPAY = "ALI_JS_PAY";
    /** 银联 js 支付*/
    public static final String UNIONJSPAY = "UNION_JS_PAY";

    public static final String CLOSE = "close";
    public static final String ORDER = "order";
    public static final String PAY = "pay";
    public static final String REVERSE = "reverse";
    public static final String REFUND = "refund";
    public static final String REFUNDQUERY = "refundquery";
    public static final String ORDERQUERY = "orderquery";
}
