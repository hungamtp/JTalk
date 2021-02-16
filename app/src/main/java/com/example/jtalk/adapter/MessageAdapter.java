package com.example.jtalk.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jtalk.R;
import com.example.jtalk.model.Message;
import java.util.ArrayList;


public class MessageAdapter  extends BaseAdapter {
    ArrayList<Message> messageList;
    static final int SENDER = 1;

    public MessageAdapter(ArrayList<Message> messageList) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView;
        if (convertView == null) {
                if( getItemViewType(position) == SENDER){
                    messageView = View.inflate(parent.getContext(), R.layout.message_view_of_sender, null);
                }
                else{
                    messageView = View.inflate(parent.getContext(), R.layout.message_view_of_receiver, null);
                }

        } else messageView = convertView;


        Message message = (Message) getItem(position);
       // ((ImageView) messageView.findViewById(R.id.avatar)).setimage(R.drawable.demo_avatar);
        ((TextView) messageView.findViewById(R.id.message)).setText(String.format("%s", message.message));
        return messageView;
    }

    @Override
    public int getItemViewType(int position) {
        int type =0;
        boolean isSender = false;
        if(messageList.get(position).isSender){
            isSender = true;
        }
        if(isSender) type = SENDER;

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
