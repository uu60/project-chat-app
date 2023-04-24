package com.ph.chatapplication.activity.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ph.chatapplication.R;

import java.util.List;

/**
 * @author octopus
 * @date 2023/4/22 19:21
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    private Bitmap oppositePortrait;
    private Bitmap myPortrait;
    private List<DataHolder> data;
    private final Activity activity;

    public MessageAdapter(List<DataHolder> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    public List<DataHolder> getData() {
        return data;
    }

    public void setData(List<DataHolder> data) {
        this.data = data;
    }

    public void setOppositePortrait(Bitmap oppositePortrait) {
        this.oppositePortrait = oppositePortrait;
    }

    public void setMyPortrait(Bitmap myPortrait) {
        this.myPortrait = myPortrait;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,
                parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataHolder dataHolder = data.get(position);
        holder.tvTime.setText(dataHolder.time);
        holder.tvContent.setText(dataHolder.text);
        Glide.with(activity)
                .load(dataHolder.me ? (myPortrait == null ? R.drawable.ic_default_portrait :
                        myPortrait) :
                        (oppositePortrait == null ? R.drawable.ic_default_portrait :
                                oppositePortrait))
                .apply(RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                        .skipMemoryCache(true)).into(holder.ivPortrait);
        holder.llItem.setLayoutDirection(dataHolder.me ? View.LAYOUT_DIRECTION_RTL :
                View.LAYOUT_DIRECTION_LTR);
        if (!dataHolder.me) {
            holder.tvContent.setBackground(activity.getDrawable(R.drawable.shape_message_oppo));
            holder.tvContent.setTextColor(activity.getResources().getColor(R.color.white));
        } else {
            holder.tvContent.setBackground(activity.getDrawable(R.drawable.shape_message_me));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected static class Holder extends RecyclerView.ViewHolder {
        private final LinearLayout llItem;
        private final ImageView ivPortrait;
        private final TextView tvTime;
        private final TextView tvContent;

        public Holder(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.ll_item);
            ivPortrait = itemView.findViewById(R.id.iv_portrait);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    public static class DataHolder {
        public String time;
        public String text;
        boolean me;

        public void setTime(String time) {
            this.time = time;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setMe(boolean me) {
            this.me = me;
        }
    }
}
