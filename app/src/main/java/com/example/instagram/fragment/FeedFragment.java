package com.example.instagram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterFeed;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerFeed;
    private AdapterFeed adapterFeed;
    private List<Feed> feedList = new ArrayList<>();
    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference feedRef;
    private String loggedUserId;


    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        //initial config
        loggedUserId = UserFirebaseHelper.getUserId();
        feedRef = FirebaseConfig.getFirebase()
                .child("feed")
                .child(loggedUserId);

        //init components
        recyclerFeed = view.findViewById(R.id.recyclerFeed);

        //set recyclerview
        adapterFeed = new AdapterFeed(feedList, getActivity() );
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerFeed.setAdapter( adapterFeed );

        return view;
    }

    private void listFeed(){

        feedList.clear();

        valueEventListenerFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    feedList.add( ds.getValue(Feed.class) );
                }
                Collections.reverse(feedList);
                adapterFeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        listFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener( valueEventListenerFeed );
    }
}
