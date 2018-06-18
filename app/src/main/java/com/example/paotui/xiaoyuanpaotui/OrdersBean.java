package com.example.paotui.xiaoyuanpaotui;

/**
 * Created by Colonel on 2018/5/10.
 */

public class OrdersBean {
    private String orders_user_id;
    private String orders_time;
    private String orders_miage_url;
    private String orders_content;
    private String orders_status;
    private String orders_price;

    public OrdersBean(String orders_user_id, String orders_time, String orders_miage_url, String orders_content, String orders_status, String orders_price) {
        this.orders_user_id = orders_user_id;
        this.orders_time = orders_time;
        this.orders_miage_url = orders_miage_url;
        this.orders_content = orders_content;
        this.orders_status = orders_status;
        this.orders_price = orders_price;
    }

    public String getOrders_user_id() {
        return orders_user_id;
    }

    public String getOrders_time() {
        return orders_time;
    }

    public String getOrders_miage_url() {
        return orders_miage_url;
    }

    public String getOrders_content() {
        return orders_content;
    }

    public String getOrders_status() {
        return orders_status;
    }

    public String getOrders_price() {
        return orders_price;
    }
}
