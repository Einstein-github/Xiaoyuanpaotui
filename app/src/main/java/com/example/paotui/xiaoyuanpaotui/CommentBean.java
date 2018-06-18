package com.example.paotui.xiaoyuanpaotui;

import java.util.Date;

/**
 * Created by Colonel on 2018/5/10.
 */

public class CommentBean {
    private String comment_user_id;
    private String comment_time;
    private String comment_miage_url;
    private String comment_content;


    public CommentBean(String comment_user_id, String comment_time, String comment_miage_url, String comment_content) {
        this.comment_user_id = comment_user_id;
        this.comment_time = comment_time;
        this.comment_miage_url = comment_miage_url;
        this.comment_content = comment_content;
    }


    public String getcomment_user_id() {
        return comment_user_id;
    }


    public String getcomment_time() {
        return comment_time;
    }


    public String getcomment_miage_url() {
        return comment_miage_url;
    }


    public String getcomment_content() {
        return comment_content;
    }

}
