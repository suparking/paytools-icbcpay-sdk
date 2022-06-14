package com.suparking.icbc.datamodule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderInfoLists {

    static Map<String,OrderInfoNode> orderinfolists = new ConcurrentHashMap<>();

    /**
     * @param projectOrderNo
     * @return
     */
    public static boolean getOrderInfoNodeByProjectOrderNo(String projectOrderNo)
    {
        if(orderinfolists.size()> 0)
        {
            for (Map.Entry<String,OrderInfoNode> entry : orderinfolists.entrySet())
            {
                OrderInfoNode orderInfoNode = entry.getValue();
                if(orderInfoNode.getProjectOrderNo().equals(projectOrderNo))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean getOrderInfoNodeStatus(String globalNo)
    {
        if(orderinfolists.containsKey(globalNo)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 删除
     * @param globalNo
     */
    public static void deleteOrderInfoNode(String globalNo)
    {
        if(orderinfolists.containsKey(globalNo))
        {
            orderinfolists.remove(globalNo);
        }
    }
    /**
     * 新增节点,存在则 不增加
     * @param orderInfoNode
     */
    public static void addOrderInfoNode(OrderInfoNode orderInfoNode) {
        if (!orderinfolists.containsKey(orderInfoNode.getOrderNo())) {
            orderinfolists.put(orderInfoNode.getOrderNo(), orderInfoNode);
        }
    }

    /**
     * 跟新某个节点数据
     * @param orderInfoNode
     * @return
     */
    public static Integer updateOrderInfo(OrderInfoNode orderInfoNode)
    {
        if(orderinfolists.size() > 0)
        {
            for(String order : orderinfolists.keySet())
            {
                if(orderInfoNode.getOrderNo().equals(order))
                {
                    orderinfolists.put(orderInfoNode.getOrderNo(),orderInfoNode);
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
    public static OrderInfoNode getOrderInfoNode(String orderNo)
    {
        if(orderinfolists.size()>0)
        {
            for(String order: orderinfolists.keySet())
            {
                if(orderNo.equals(order))
                {
                    return orderinfolists.get(order);
                }
            }
        }
        return null;
    }

    /**
     * 根据 项目订单号  返回 此订单的相关信息
     * @param projectOrderNo
     * @return
     */
    public static OrderInfoNode getOrderInfoNodeByProjectNo(String projectOrderNo)
    {
        if(orderinfolists.size() > 0)
        {
            for (Map.Entry<String,OrderInfoNode> entry : orderinfolists.entrySet())
            {
                OrderInfoNode orderInfoNode = entry.getValue();
                if(orderInfoNode.getProjectOrderNo().equals(projectOrderNo))
                {
                    String globalOrderNo = entry.getKey();
                    return orderinfolists.get(globalOrderNo);
                }
            }
        }
        return null;
    }

}
