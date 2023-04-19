package com.ph.chatapplication.activity.adapter;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ph.chatapplication.R;
import com.ph.chatapplication.activity.fragment.ContactFragment;

import java.util.List;

/**
 * @author octopus
 * @date 2023/4/17 12:57
 */
public class ContactFragmentAdapter extends RecyclerView.Adapter<ContactFragmentAdapter.Holder> {

    private final List<DataHolder> data;
    private final ContactFragment fragment;


    public ContactFragmentAdapter(List<DataHolder> data, ContactFragment fragment) {
        this.data = data;
        this.fragment = fragment;
    }

    public List<DataHolder> getData() {
        return data;
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
        Glide.with(fragment).load(dataHolder.portrait == null ? R.drawable.ic_default_portrait :
                dataHolder.portrait).apply(RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(true)).into(holder.imPortrait);
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
        String portraitUrl;
        Bitmap portrait;
        String nickName;

        public DataHolder(Integer userId, String portraitUrl, Bitmap portrait, String nickName) {
            this.userId = userId;
            this.portraitUrl = portraitUrl;
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

        public String getPortraitUrl() {
            return portraitUrl;
        }

        public void setPortrait(Bitmap bitmap) {
            this.portrait = bitmap;
        }
    }
}
