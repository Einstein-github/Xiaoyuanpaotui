package com.example.paotui.xiaoyuanpaotui;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Colonel on 2018/5/26.
 */

public class AddressAdapter extends BaseQuickAdapter<AddressBean, BaseViewHolder> {


public AddressAdapter(int layoutResId, @Nullable List<AddressBean> data) {
        super(layoutResId,data);
        }

@Override
protected void convert(BaseViewHolder helper, AddressBean item) {
        helper.setText(R.id.address_content,item.getAddress_content())
        .setText(R.id.address_user_name, item.getAddress_name())
        .setText(R.id.address_user_num,item.getAddress_telephone());
    Glide.with(mContext).load(item.getAddress_url()).into((ImageView) helper.getView(R.id.address_imageView));
    }
}