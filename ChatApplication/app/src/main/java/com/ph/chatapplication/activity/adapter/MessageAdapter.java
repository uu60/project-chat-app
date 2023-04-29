package com.ph.chatapplication.activity.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;

import java.util.List;

/**
 * @author octopus
 * @date 2023/4/22 19:21
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    private Drawable oppositePortraitDrawable;
    private Drawable myPortraitDrawable;
    private List<DataHolder> data;
    private final Activity activity;

    public MessageAdapter(List<DataHolder> data, Activity activity) {
        this.data = data;
        this.activity = activity;
        oppositePortraitDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_default_portrait);
        myPortraitDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_default_portrait);
    }

    public List<DataHolder> getData() {
        return data;
    }

    public void setData(List<DataHolder> data) {
        this.data = data;
    }

    public void setOppositePortraitDrawable(Bitmap oppositePortrait) {
        if (oppositePortrait != null) {
            oppositePortrait = Bitmap.createScaledBitmap(oppositePortrait, 60, 60, true);
            this.oppositePortraitDrawable =
                    RoundedBitmapDrawableFactory.create(activity.getResources(), oppositePortrait);
            ((RoundedBitmapDrawable) this.oppositePortraitDrawable).setCircular(true);
        }
    }

    public void setMyPortraitDrawable(Bitmap myPortrait) {
        if (myPortrait != null) {
            myPortrait = Bitmap.createScaledBitmap(myPortrait, 60, 60, true);
            this.myPortraitDrawable =
                    RoundedBitmapDrawableFactory.create(activity.getResources(), myPortrait);
            ((RoundedBitmapDrawable) this.myPortraitDrawable).setCircular(true);
        }
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
        holder.llItem.setLayoutDirection(dataHolder.me ? View.LAYOUT_DIRECTION_RTL :
                View.LAYOUT_DIRECTION_LTR);
        holder.tvTime.setText(dataHolder.time);
        holder.tvContent.setText(dataHolder.text);
        holder.ivPortrait.setImageDrawable(dataHolder.me ? myPortraitDrawable : oppositePortraitDrawable);

        if (!dataHolder.me) {
            holder.tvContent.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_message_oppo));
            holder.tvContent.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else {
            holder.tvContent.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_message_me));
            holder.tvContent.setTextColor(ContextCompat.getColor(activity, R.color.black));
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
