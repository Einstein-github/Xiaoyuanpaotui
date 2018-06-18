package com.example.paotui.xiaoyuanpaotui;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Colonel on 2018/5/10.
 */

public  class OrderAdapter extends BaseQuickAdapter<OrdersBean, BaseViewHolder > {


    public OrderAdapter(int layoutResId, @Nullable List<OrdersBean> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrdersBean item) {
             helper.setText(R.id.item_orders_user_id,item.getOrders_user_id())
                     .setText(R.id.item_orders_time, item.getOrders_time())
                     .setText(R.id.item_orders_content,item.getOrders_content())
                     .setText(R.id.item_orders_status,item.getOrders_status())
                     .setText(R.id.item_orders_price,item.getOrders_price());
        Glide.with(mContext).load(item.getOrders_miage_url()).into((ImageView) helper.getView(R.id.item_orders_user_image));
    }


}
