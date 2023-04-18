package com.ph.chatapplication.activity.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.utils.Instances;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author octopus
 * @date 2023/4/17 12:57
 */
public class AddContactFragmentAdapter extends RecyclerView.Adapter<AddContactFragmentAdapter.ViewHolder> {

    private List<DataHolder> data = new ArrayList<>();

    public AddContactFragmentAdapter(List<DataHolder> data) {
        this.data = data;
        //setData(this.data);
    }

//    public void setData(List<DataHolder> data) {
////        this.data = data;
//        // test data
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//        data.add(new DataHolder(1, "user1", null, new Date()));
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，加载item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        DataHolder dataHolder = data.get(position);
        viewHolder.dataHolder = dataHolder;
        viewHolder.tvNickname.setText(dataHolder.nickName);
        try {
            viewHolder.tvRequestTime.setText(Instances.sdf.format(dataHolder.requestTime));
        } catch (Throwable ignored) {

        }
        if (dataHolder.portrait == null) {
            viewHolder.imPortrait.setImageResource(R.drawable.ic_default_portrait);
        } else {
            viewHolder.imPortrait.setImageBitmap(dataHolder.portrait);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imPortrait;
        private final TextView tvNickname;
        private final TextView tvRequestTime;
        private DataHolder dataHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imPortrait = itemView.findViewById(R.id.im_portrait);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvRequestTime = itemView.findViewById(R.id.tv_request_time);
        }
    }

    public static class DataHolder {
        Integer userId;
        String nickName;
        Bitmap portrait;
        Date requestTime;

        public DataHolder(Integer userId, String nickName, Bitmap portrait, Date requestTime) {
            this.userId = userId;
            this.nickName = nickName;
            this.portrait = portrait;
            this.requestTime = requestTime;
        }
    }
}
