package com.example.instagram.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.FriendProfileActivity;
import com.example.instagram.R;
import com.example.instagram.adapter.AdapterSearch;
import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    //Widget
    private SearchView searchViewSearch;
    private RecyclerView recyclerSearch;
    private List<User> usersList;
    private DatabaseReference usersRef;
    private AdapterSearch searchAdapter;
    private String loggedUserId;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchViewSearch = view.findViewById(R.id.searchViewPesquisa);
        recyclerSearch = view.findViewById(R.id.recyclerPesquisa);

        //initial config
        usersList = new ArrayList<>();
        usersRef = FirebaseConfig.getFirebase()
                .child("usuarios");
        loggedUserId = UserFirebaseHelper.getUserId();

        //set RecyclerView
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchAdapter = new AdapterSearch(usersList, getActivity());
        recyclerSearch.setAdapter(searchAdapter);

        //set click event
        recyclerSearch.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerSearch,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User selectedUser = usersList.get(position);
                        Intent i = new Intent(getActivity(), FriendProfileActivity.class);
                        i.putExtra("usuarioSelecionado", selectedUser );
                        startActivity( i );

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //set searchview
        searchViewSearch.setQueryHint(getResources().getString(R.string.search_users));
        searchViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textTyped = newText.toUpperCase();
                searchUsers( textTyped );
                return true;
            }
        });



        return view;
    }

    private void searchUsers(String text){

        usersList.clear();

        if( text.length() >= 2 ){

            Query query = usersRef.orderByChild("name")
                    .startAt(text)
                    .endAt(text + "\uf8ff" );

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    usersList.clear();

                    for( DataSnapshot ds : dataSnapshot.getChildren() ){

                        User user = ds.getValue(User.class);

                        //check if it's the logged User
                        if ( loggedUserId.equals( user.getId() ) )
                            continue;

                        usersList.add( user );

                    }

                    searchAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}
