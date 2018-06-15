package com.ynfante.crimer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ynfante.crimer.Adapters.PostListAdapter;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.R;

import java.util.ArrayList;

public class PostsFragment extends Fragment implements EventListener<QuerySnapshot>{
    private final String TAG = PostsFragment.class.getSimpleName();
    private FloatingActionButton newPostFab;
    private RecyclerView postsListRecycler;
    private PostListAdapter postsAdapter;
    private FirebaseFirestore database;


    public PostsFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        newPostFab = view.findViewById(R.id.posts_new_post_fab);
        newPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).goToCreatePost();

            }
        });

        postsListRecycler = view.findViewById(R.id.posts_list_recycler);

        postsAdapter = new PostListAdapter(getActivity());
        LayoutManager manager = new LinearLayoutManager(getActivity());
        postsListRecycler.setAdapter(postsAdapter);
        postsListRecycler.setLayoutManager(manager);

        postsListRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        database = FirebaseFirestore.getInstance();
        database.collection("posts").addSnapshotListener(this);


        return view;

    }


    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
        Log.d(TAG, "GETTING POSTS");
        ArrayList<Post> newPosts = new ArrayList<Post>(queryDocumentSnapshots.toObjects(Post.class));
        Log.d(TAG, newPosts.toString());
        postsAdapter.updateData(newPosts);

    }
}
