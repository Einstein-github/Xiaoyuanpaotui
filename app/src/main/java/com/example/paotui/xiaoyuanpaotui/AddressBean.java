package com.example.paotui.xiaoyuanpaotui;

/**
 * Created by Colonel on 2018/5/26.
 */

public class AddressBean {
    private String address_content;
    private String address_user_name;
    private String address_user_telephone;
    private String address_url;

    public AddressBean(String address_content, String address_name, String address_telephone ,String address_url) {
        this.address_content = address_content;
        this.address_user_name = address_name;
        this.address_user_telephone = address_telephone;
        this.address_url = address_url;
    }

    public String getAddress_content() {
        return address_content;
    }

    public String getAddress_name() {
        return address_user_name;
    }

    public String getAddress_telephone() {
        return address_user_telephone;
    }
    public String getAddress_url() {
        return address_url;
    }
}
