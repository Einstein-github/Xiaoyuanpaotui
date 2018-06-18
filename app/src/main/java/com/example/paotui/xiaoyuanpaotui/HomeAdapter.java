package com.example.paotui.xiaoyuanpaotui;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Colonel on 2018/5/10.
 */

public class HomeAdapter extends BaseQuickAdapter<RemindBean, BaseViewHolder > {


    public HomeAdapter(int layoutResId, @Nullable List<RemindBean> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemindBean item) {
             helper.setText(R.id.item_remind_user_id,item.getremind_user_id())
                     .setText(R.id.item_remind_time, item.getremind_time())
                     .setText(R.id.item_remind_content,item.getremind_content());
        Glide.with(mContext).load(item.getremind_miage_url()).into((ImageView) helper.getView(R.id.item_remind_user_image));
    }
}
