package com.example.paotui.xiaoyuanpaotui;

/**
 * Created by Colonel on 2018/5/23.
 */

public class RemindBean {
    private String remind_user_id;
    private String remind_time;
    private String remind_miage_url;
    private String remind_content;
    public RemindBean(String remind_user_id, String remind_time, String remind_miage_url, String remind_content) {
        this.remind_user_id = remind_user_id;
        this.remind_time = remind_time;
        this.remind_miage_url = remind_miage_url;
        this.remind_content = remind_content;
    }
    public String getremind_user_id() {
        return remind_user_id;
    }
    public String getremind_time() {
        return remind_time;
    }
    public String getremind_miage_url() {
        return remind_miage_url;
    }
    public String getremind_content() {
        return remind_content;
    }
}
