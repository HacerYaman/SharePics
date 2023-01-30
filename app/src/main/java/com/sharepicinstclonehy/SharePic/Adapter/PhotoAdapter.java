package com.sharepicinstclonehy.SharePic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sharepicinstclonehy.SharePic.Fragment.PostDetailFragment;
import com.sharepicinstclonehy.SharePic.Model.Post;
import com.sharepicinstclonehy.SharePic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.photo_item,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post=mPosts.get(position);
        Picasso.get().load(post.getImageurl())
                .placeholder(R.mipmap.ic_launcher).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {        //when user clicked a photo in user profile
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",post.getPostid()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container_view_tag, new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage= itemView.findViewById(R.id.post_image);

        }
    }
}

