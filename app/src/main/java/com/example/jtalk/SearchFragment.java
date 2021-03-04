package com.example.jtalk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jtalk.adapter.SearchAdapter;
import com.example.jtalk.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements  SearchView.OnQueryTextListener{
    DatabaseReference databaseReference;
    String username;
    ListView userListView;
    List<User> userList;
    SearchAdapter searchAdapter;
    SearchView searchView;



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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        return  view;
    }

    void getAllUser(){
        databaseReference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
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

    private void initView() {
        View view = getView();
        userListView = view.findViewById(R.id.friend_list_view);
        searchView = view.findViewById(R.id.search_view);

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
}