package com.example.jtalk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jtalk.MainFragmentDirections;
import com.example.jtalk.R;
import com.example.jtalk.model.Chat;
import com.example.jtalk.model.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<Chat> friendList;

    public ChatListAdapter(List<Chat> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.user_item_in_chat_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = friendList.get(position);
        holder.username.setText(chat.username);
        holder.lastMessage.setText(chat.lastMessages);

        if (chat.online) {
            // set image when is online
            holder.online.setImageResource(R.drawable.online);
        } else {
            // set image when is offline
            holder.online.setImageResource(R.drawable.offline);
        }
        if (!chat.avatar.equals("")) {
            // set avatar
            Glide.with(holder.avatar.getContext()).load(chat.avatar).into(holder.avatar);
        }
//
    }

    public void updateNewMessage(int position) {
        Chat temp = friendList.get(position);
        for (int i = position; i >= 1; i--) {
            friendList.set(i, friendList.get(i - 1));
        }

        friendList.set(0, temp);

    }


    public int getPositionById(String username) {
        int index = 0;
        for (User x : friendList) {
            if (x.username.equals(username)) {
                return index;
            } else index++;
        }
        return -1;
    }


    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        public TextView lastMessage;
        ImageView avatar;
        ImageView online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final NavController navController = Navigation.findNavController(v);
                    Intent intent = ((Activity) v.getContext()).getIntent();
                    MainFragmentDirections.MainToChat mainToChat = MainFragmentDirections.mainToChat(intent.getStringExtra("username"), username.getText().toString());
                    mainToChat.setSender(intent.getStringExtra("username"));
                    mainToChat.setReceiver(username.getText().toString());
                    navController.navigate(mainToChat);
                }
            });
            lastMessage = itemView.findViewById(R.id.last_message);
            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.avatar);
            online = itemView.findViewById(R.id.online);

        }


    }

}
