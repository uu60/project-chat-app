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

import java.util.List;

/**
 * @author octopus
 * @date 2023/4/17 12:57
 */
public class ContactFragmentAdapter extends RecyclerView.Adapter<ContactFragmentAdapter.Holder> {

    private final List<DataHolder> data;


    public ContactFragmentAdapter(List<DataHolder> data) {
        this.data = data;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，加载item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,
                parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataHolder dataHolder = data.get(position);
        holder.dataHolder = dataHolder;
        holder.tvNickname.setText(dataHolder.nickName);
        if (dataHolder.portrait == null) {
            holder.imPortrait.setImageResource(R.drawable.ic_default_portrait);
        } else {
            holder.imPortrait.setImageBitmap(dataHolder.portrait);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView imPortrait;
        private TextView tvNickname;
        private DataHolder dataHolder;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imPortrait = itemView.findViewById(R.id.im_portrait);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
        }
    }

    public static class DataHolder {
        Integer userId;
        Bitmap portrait;
        String nickName;

        public DataHolder(Integer userId, Bitmap portrait, String nickName) {
            this.userId = userId;
            this.portrait = portrait;
            this.nickName = nickName;
        }

        public Integer getUserId() {
            return userId;
        }

        public Bitmap getPortrait() {
            return portrait;
        }

        public String getNickName() {
            return nickName;
        }
    }
}
