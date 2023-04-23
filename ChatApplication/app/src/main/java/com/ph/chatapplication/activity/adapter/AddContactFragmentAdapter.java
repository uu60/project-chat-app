package com.ph.chatapplication.activity.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;
import com.ph.chatapplication.utils.source.Instances;

import java.util.Date;
import java.util.List;

/**
 * @author octopus
 * @date 2023/4/17 12:57
 */
public class AddContactFragmentAdapter extends RecyclerView.Adapter<AddContactFragmentAdapter.ViewHolder> {

    private List<DataHolder> data;
    private ButtonInterface buttonInterface;

    public AddContactFragmentAdapter(List<DataHolder> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，加载item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_contact,
                parent, false);
        return new ViewHolder(view);
    }

    public void buttonSetOnclick(ButtonInterface buttonInterface) {
        this.buttonInterface = buttonInterface;
    }

    public interface ButtonInterface {
        void onclick(View view, int userId, int isAgree, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,
                                 @SuppressLint("RecyclerView") int position) {
        DataHolder dataHolder = data.get(position);
        Integer userId = dataHolder.userId;
        viewHolder.dataHolder = dataHolder;
        viewHolder.tvNickname.setText(dataHolder.nickName);
        try {
            viewHolder.tvRequestTime.setText(Instances.simpleSdf.format(dataHolder.requestTime));
        } catch (Throwable ignored) {

        }
        if (dataHolder.portrait == null) {
            viewHolder.imPortrait.setImageResource(R.drawable.ic_default_portrait);
        } else {
            viewHolder.imPortrait.setImageBitmap(dataHolder.portrait);
        }
        //viewHolder.btnAdd.setText(data.get(position).toString());
        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonInterface != null) {
                    buttonInterface.onclick(v, dataHolder.userId, 1, position);
                }
            }
        });
        viewHolder.btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonInterface != null) {
                    buttonInterface.onclick(v, dataHolder.userId, 0, position);
                }
            }
        });
    }

    public void removeData(int position) {
        data.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imPortrait;
        private final TextView tvNickname;
        private final TextView tvRequestTime;
        private final Button btnAdd;
        private final Button btnRefuse;
        private DataHolder dataHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imPortrait = itemView.findViewById(R.id.im_portrait);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvRequestTime = itemView.findViewById(R.id.tv_request_time);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnRefuse = itemView.findViewById(R.id.btn_refuse);
        }
    }

    public static class DataHolder {
        public Integer userId;
        public  String nickName;
        public Bitmap portrait;
        public Date requestTime;

        public DataHolder(Integer userId, String nickName, Bitmap portrait, Date requestTime) {
            this.userId = userId;
            this.nickName = nickName;
            this.portrait = portrait;
            this.requestTime = requestTime;
        }
        public DataHolder() {
            this.userId = userId;
            this.nickName = nickName;
            this.portrait = portrait;
            this.requestTime = requestTime;
        }
    }
}
