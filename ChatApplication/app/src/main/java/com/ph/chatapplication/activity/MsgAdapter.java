package com.ph.chatapplication.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    //把显示的数据源传进来
    public MsgAdapter(List<Object> msgList) {
        super();
    }

    //创建ViewHolder实例
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    //对RecyclerView子项数据进行赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Msg msg = mMsgList.get(position);
//        holder.time.setText((CharSequence)msg.getTime());
//        if(msg.getType() == Msg.TYPE_RECEIVED){  //接收端
//            holder.his_head.setImageResource(msg.getImageId());
//            holder.leftLayout.setVisibility(View.VISIBLE);  //左可见
//            holder.rightLayout.setVisibility(View.GONE);    //右不可见
//            holder.hisName.setText(msg.getName());
//            holder.leftMsg.setText(msg.getContent());       //左边显示消息
//        }else if(msg.getType() == Msg.TYPE_SENT){
//            holder.my_head.setImageResource(msg.getImageId());
//            holder.rightLayout.setVisibility(View.VISIBLE);
//            holder.leftLayout.setVisibility(View.GONE);
//            holder.name.setText(msg.getName());
//            holder.rightMsg.setText(msg.getContent());
//        }
    }

    //告诉RecycleView一共多少子项，返回数据源长度
    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout ;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView time;
        TextView name;
        TextView hisName;
        ImageView my_head;
        ImageView his_head;

        public ViewHolder(View view) {
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
            time = view.findViewById(R.id.time);              //显示时间
            name = view.findViewById(R.id.my_name);
            hisName = view.findViewById(R.id.others_name);
            my_head = view.findViewById(R.id.head_my);
            his_head = view.findViewById(R.id.head_others);
        }
    }
}

