package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Message;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        inflater = LayoutInflater.from(context);
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);
        if (null == convertView) {
            if ("11111".equals(message.getFromUserId())) {
                convertView = inflater.inflate(R.layout.row_sent_message, null);
            } else {
                convertView = inflater.inflate(R.layout.row_received_message, null);
            }
        }
        TextView mTimeStampTv = convertView.findViewById(R.id.timestamp);
        TextView mContentTv = convertView.findViewById(R.id.tv_chatcontent);

        mTimeStampTv.setText(message.getCreateTime());
        mContentTv.setText(message.getContent());

        return convertView;
    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv;
        TextView tv_userId;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }
}
