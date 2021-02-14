package com.example.jtalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jtalk.ChatActivity;
import com.example.jtalk.MainActivity;
import com.example.jtalk.R;
import com.example.jtalk.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<User> friendList ;

    public FriendListAdapter(List<User> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView =
                inflater.inflate(R.layout.user_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user =  friendList.get(position);


        holder.username.setText(user.username);
        if(user.online){
            holder.online.setImageResource(R.drawable.ic_launcher_background);
        }else {
            holder.online.setImageResource(R.drawable.ic_baseline_arrow_back_24);
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("avatar/"+user.username+".jpg");
        if (user.avatar) {
            storageReference = FirebaseStorage.getInstance().getReference().child("avatar/" + user.username + ".jpg");
            try {
                final File localFile = File.createTempFile(user.username, ".jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.avatar.setImageBitmap(bitmap);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public String getItemUsername(int position){
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
                    Intent intent = new Intent();
                    intent.setClass(v.getContext() , ChatActivity.class);
                    intent.putExtra("receiver", username.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });

            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.avatar);
            online = itemView.findViewById(R.id.online);


        }


    }

}
