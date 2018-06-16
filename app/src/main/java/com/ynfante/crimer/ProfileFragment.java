package com.ynfante.crimer;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ynfante.crimer.Adapters.PostListAdapter;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.Models.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements EventListener<QuerySnapshot>{
    private final String TAG = ProfileFragment.class.getSimpleName();
    private FloatingActionButton newPostFab;
    private RecyclerView postsListRecycler;
    private PostListAdapter postsAdapter;
    private FirebaseFirestore database;
    private String userId;
    private User profileUser;
    private FirebaseUser signedUser;

    private ImageView profilePicture;
    private TextView name, username;

    private View noPostsMessage;


    public ProfileFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        newPostFab = view.findViewById(R.id.posts_new_post_fab);
        newPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).goToCreatePost();

            }
        });

        signedUser = FirebaseAuth.getInstance().getCurrentUser();
        profilePicture = view.findViewById(R.id.profile_display_picture);
        name = view.findViewById(R.id.profile_user);
        username = view.findViewById(R.id.profile_username);

        noPostsMessage = view.findViewById(R.id.no_posts_msg);

        postsListRecycler = view.findViewById(R.id.posts_list_recycler);

        postsAdapter = new PostListAdapter(getActivity());
        LayoutManager manager = new LinearLayoutManager(getActivity());
        postsListRecycler.setAdapter(postsAdapter);
        postsListRecycler.setLayoutManager(manager);

        postsListRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        database = FirebaseFirestore.getInstance();

        database.collection("users").document(signedUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    profileUser = task.getResult().toObject(User.class);
                    Log.d(TAG, profileUser.getName());
                    database.collection("posts")
                            .whereEqualTo("userId", signedUser.getUid())
                            .orderBy("publishedDate", Query.Direction.DESCENDING)
                            .addSnapshotListener(ProfileFragment.this);
                    updateUserViews();

                } else {
                    Log.e(TAG, task.getException().getMessage());
                }
            }
        });




        return view;

    }


    public void updateUserViews() {
        name.setText(profileUser.getName());
        username.setText("@" + profileUser.getUsername());
        if( profileUser.getPhotoUrl() != null) {
            Glide.with(getActivity()).load(profileUser.getPhotoUrl()).into(profilePicture);
        }

    }


    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
        Log.d(TAG, "GETTING PROFILE POSTS");

        if(e != null) {
            Log.w(TAG, e.getMessage());
        }

        if(queryDocumentSnapshots != null) {
            ArrayList<Post> newPosts = new ArrayList<>(queryDocumentSnapshots.toObjects(Post.class));
            Log.d(TAG, newPosts.toString());
            postsAdapter.updateData(newPosts);
            noPostsMessage.setVisibility( postsAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);

        }





    }
}
