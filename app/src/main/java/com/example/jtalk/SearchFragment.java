package com.example.jtalk;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jtalk.adapter.SearchAdapter;
import com.example.jtalk.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    DatabaseReference databaseReference;
    String username;
    ListView userListView;
    List<User> userList;
    SearchAdapter searchAdapter;
    SearchView searchView;
    Dialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        getAllUser();
        searchView.setOnQueryTextListener(this);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendName = userList.get(position).username;
                showDialog(friendName);
                searchAdapter.getView(position , view , parent);
            }
        });

    }
    void showDialog(String friendName){
        dialog.setContentView(R.layout.dialog_friend);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        //  init view in dialog
        TextView name  =dialog.findViewById(R.id.name);
        CircleImageView avatar = dialog.findViewById(R.id.avatar);
        ImageView addFriend = dialog.findViewById(R.id.add_friend);
        ImageView text = dialog.findViewById(R.id.text);

        name.setText(friendName);



        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Users").child(username).child("Friends").push().setValue(friendName);
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    void getAllUser() {
        databaseReference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                    searchAdapter.getUserList().add(user);
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean isFriend(String username, String friendname) {
        boolean result = true;
        databaseReference.child("Users").child(username).child("Friends").child(friendname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return false;
    }

    private void initView() {
        View view = getView();
        userListView = view.findViewById(R.id.friend_list_view);
        searchView = view.findViewById(R.id.search_view);
        dialog = new Dialog(view.getContext());

        // init firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // list view
        userList = new ArrayList<>();
        searchAdapter = new SearchAdapter(userList);
        userListView.setAdapter(searchAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            SearchFragmentArgs args = SearchFragmentArgs.fromBundle(getArguments());
            username = args.getUsername();
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        searchAdapter.filter(text);
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}