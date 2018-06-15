package com.ynfante.crimer.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ynfante.crimer.MainActivity;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class PostListAdapter  extends RecyclerView.Adapter<PostListAdapter.PostViewHolder>{

    Context activityContext;
    ArrayList<Post> posts;

    public PostListAdapter(Context activityContext) {
        this.activityContext = activityContext;
        posts = new ArrayList<>();
    }

    public void updateData (ArrayList<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        final Post post = posts.get(position);
        if(post.getImageUrl() != null ) {
            Glide.with(activityContext)
                    .load(post.getImageUrl())
                    .into(holder.postImage);
        }

        if( post.getUser().getPhotoUrl() != null) {
            Glide.with(activityContext)
                    .load(post.getUser().getPhotoUrl())
                    .into(holder.displayPicture);
        }

        holder.content.setText(post.getContent());
        holder.title.setText(post.getTitle());
        holder.username.setText("@"+post.getUser().getUsername());
        holder.name.setText(post.getUser().getName());


        if(post.getLocation().getPlace() != null ) {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(post.getLocation().getPlace());
        } else {
            holder.location.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activityContext).goToDetailedActivity(post);
            }
        });





    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView name, username, title, content, location;
        ImageView displayPicture;
        ImageView postImage;
        View layout;

        public PostViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_post_user);
            username = itemView.findViewById(R.id.item_post_username);
            title = itemView.findViewById(R.id.item_post_title);
            content = itemView.findViewById(R.id.item_post_content);
            location = itemView.findViewById(R.id.item_post_location);
            displayPicture = itemView.findViewById(R.id.item_post_display_picture);
            postImage = itemView.findViewById(R.id.item_post_image);
            layout = itemView.findViewById(R.id.item_post_ly);


        }
    }
}
