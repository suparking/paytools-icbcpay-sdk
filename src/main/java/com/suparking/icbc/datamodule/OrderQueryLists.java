package com.suparking.icbc.datamodule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderQueryLists {
    static Map<String,Object> orderQueryLists = new ConcurrentHashMap<>();

    /**
     * 删除
     * @param orderNo
     */
    public static void deleteOrderQueryNode(String orderNo)
    {
        if(orderQueryLists.containsKey(orderNo))
        {
            orderQueryLists.remove(orderNo);
        }
    }
    /**
     * 新增节点,存在则 不增加
     * @param orderNo
     */
    public static void addOrderQueryNode(String orderNo) {
        if (!orderQueryLists.containsKey(orderNo)) {
            orderQueryLists.put(orderNo, true);
        }
    }

    /**
     * 跟新某个节点数据
     * @param orderNo, flag
     * @return
     */
    public static Integer updateOrderQuery(String orderNo,boolean flag)
    {
        if(orderQueryLists.size() > 0)
        {
            for(String order : orderQueryLists.keySet())
            {
                if(order.equals(orderNo))
                {
                    orderQueryLists.put(orderNo,flag);
                    return 0;
                }
            }
        }
        return -1;
    }

    /**
     * 根据全局订单 获取 其相关信息
     * @param orderNo
     * @return
     */
    public static boolean getOrderQueryNode(String orderNo)
    {
        if(orderQueryLists.containsKey(orderNo))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
