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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jtalk.ChatActivity;
import com.example.jtalk.R;
import com.example.jtalk.model.User;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<User> friendList;

    public FriendListAdapter(List<User> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView =   inflater.inflate(R.layout.user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.username.setText(user.username);

        if (user.online) {
            // set image when is online
            holder.online.setImageResource(R.drawable.online);
        } else {
            // set image when is offline
            holder.online.setImageResource(R.drawable.offline);
        }
//
        if (!user.avatar.equals("")) {
            // set avatar
            Glide.with(holder.avatar.getContext()).load(user.avatar).into(holder.avatar);
        }


    }

    public int getPositionById(String username) {
        int index = 0;
        for (User x : friendList) {
            if (x.username.equals(username)) {
                break;
            } else index++;
        }
        return index;
    }

    public boolean checkUser(String username){
        boolean result = false;
        for(User friend : friendList){
            if(friend.username.equals(username)){
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public String getItemUsername(int position) {
        return friendList.get(position).username;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView avatar;
        ImageView online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ((Activity) v.getContext()).getIntent();
                    intent.setClass(v.getContext(), ChatActivity.class);
                    intent.putExtra("receiver", username.getText().toString());
                    intent.putExtra("sender", intent.getStringExtra("username"));
                    v.getContext().startActivity(intent);
                }
            });

            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.avatar);
            online = itemView.findViewById(R.id.online);
        }
    }

}
