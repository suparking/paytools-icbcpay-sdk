package com.suparking.icbc.tools;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    /**
     * 获取日期的Date与Time
     * @return
     */
    public static String[] getAbcOrderDateAndTime()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        String currentDate = simpleDateFormat.format(System.currentTimeMillis());
        return currentDate.split("-");
    }
    public static  String getQRCodeDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static  String getQRCodeDate(String data)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = simpleDateFormat1.parse(data);
            return simpleDateFormat.format(date1);
        }catch (Exception e){
            return null;
        }
    }
    public static String getQueryDateBeforeNDay(Integer day)
    {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        return simpleDateFormat.format(date);
    }
    public static Date getDateFromDateStr(String dateStr)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
        }
        return date;
    }
    public static String getQueryCodeDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }
    public static String getDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(System.currentTimeMillis());
    }
    public static String getOrderDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(System.currentTimeMillis());
    }
    public static String getPayDate() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmssSSS");
        return simpleDateFormat.format(System.currentTimeMillis());
    }
    /*将秒转化为天 小时 分秒字符串*/
    public static String formatSeconds(long seconds)
    {
        String  timeStr = seconds + "秒";
        if (seconds > 60)
        {
            long second = seconds % 60;
            long min = seconds /60;
            timeStr = min + "分" + second + "秒";
            if (min > 60)
            {
                min = (seconds/60)%60;
                long hour = (seconds / 60)/60;
                timeStr = hour + "小时" + min +"分" +second +"秒";
                if (hour > 24)
                {
                    hour = ((seconds / 60)/60)%24;
                    long day = (((seconds / 60)/60)/24);
                    timeStr = day + "天" + hour+"小时"+min+"分"+second+"秒";
                }
            }
        }
        return timeStr;
    }

    public static String CalculateTime(String dateStr)
    {
        long nowTime = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date setTime = null;
        try
        {
            setTime = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
        //获取指定时间的毫秒数
        long reset = setTime.getTime();
        long dateDiff = nowTime-reset;
        if(dateDiff < 0)
        {
            return null;
        }

        return formatSeconds(dateDiff/1000);
    }

    public static void main(String[] args) {
        String day = "-30";
        System.out.println(TimeUtils.getDate()+","+TimeUtils.getQueryDateBeforeNDay(Integer.parseInt(day)));
    }
}
