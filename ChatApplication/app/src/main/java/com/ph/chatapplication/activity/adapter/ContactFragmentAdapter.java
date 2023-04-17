package com.ph.chatapplication.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph.chatapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author octopus
 * @date 2023/4/17 12:57
 */
public class ContactFragmentAdapter extends RecyclerView.Adapter<ContactFragmentAdapter.Holder> {

    private List<DataHolder> data = new ArrayList<>();


    public ContactFragmentAdapter(List<List<Map>> data) {
        for (Map temp : data.get(0)){
            String s = (String) temp.get("portraitUrl");
            this.data.add(new DataHolder((String) temp.get("portraitUrl"), (String) temp.get("nickname")));
        }

        setData(this.data);
    }

    public ContactFragmentAdapter(Object data) {

        setData(this.data);
    }

    public void setData(List<DataHolder> data) {
//        this.data = data;
        // test data

//
//        data.add(new DataHolder("www", "user1"));
//        data.add(new DataHolder("www", "user1"));
//        data.add(new DataHolder("www", "user1"));
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，加载item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvNickname.setText(data.get(position).nickName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView imPortrait;
        private TextView tvNickname;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imPortrait = itemView.findViewById(R.id.im_portrait);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
        }
    }

    static class DataHolder {
        String portraitUrl;
        String nickName;

        public DataHolder(String portraitUrl, String nickName) {
            this.portraitUrl = portraitUrl;
            this.nickName = nickName;
        }
    }
}
