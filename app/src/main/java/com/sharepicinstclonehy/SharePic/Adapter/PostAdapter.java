package com.sharepicinstclonehy.SharePic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharepicinstclonehy.SharePic.CommentActivity;
import com.sharepicinstclonehy.SharePic.Fragment.PostDetailFragment;
import com.sharepicinstclonehy.SharePic.Fragment.ProfileFragment;
import com.sharepicinstclonehy.SharePic.Model.Post;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.R;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post= mPost.get(position);
        Glide.with(mContext).load(post.getImageurl()).into(holder.post_image);

        if (post.getDescription().equals("")){
            holder.descriptionnn.setVisibility(View.GONE);
        }else{
            holder.descriptionnn.setVisibility(View.VISIBLE);
            holder.descriptionnn.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());

        //----------------
        isLiked(post.getPostid(),holder.like);
        numOfLikes(post.getPostid(),holder.likes);
        getComments(post.getPostid(), holder.comments);
        isSaved(post.getPostid(),holder.save);
        //----------------

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPublisher(),post.getPostid());

                }else{
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);

                SharedPreferences postinfoforcomment= mContext.getSharedPreferences("POSTIDINFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= postinfoforcomment.edit();
                editor.putString("postid", post.getPostid());
                editor.putString("authorId", post.getPublisher());
                editor.apply();


            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference()
                            .child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container_view_tag, new ProfileFragment()).commit();
            }
        });


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container_view_tag, new ProfileFragment()).commit();


            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                        .putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container_view_tag, new ProfileFragment()).commit();

            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {        //when user clicked a photo in user profile
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
        return mPost.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save;
        public TextView username,likes,publisher,descriptionnn,comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile= itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save =itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            descriptionnn=itemView.findViewById(R.id.descriptionnn);
            publisher=itemView.findViewById(R.id.publisher);
            comments=itemView.findViewById(R.id.comments);
        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String id){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postId, ImageView imageView){

        FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(firebaseUser.getUid()).exists()){
                            imageView.setImageResource(R.drawable.ic_heart);
                            imageView.setTag("liked");
                        }else{
                            imageView.setImageResource(R.drawable.ic_like);
                            imageView.setTag("like");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void isSaved(String postId, ImageView imageView) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                .getReference().child("Saves").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("save");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void numOfLikes(String postId, TextView text){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount()+" likes");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getComments(String postId, TextView text){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText("View All "+ snapshot.getChildrenCount()+ " Comments");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addNotification(String userid, String postid){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> map =new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text","liked your post.");
        map.put("postid", postid);
        map.put("isPost",true);

        reference.push().setValue(map);
    }

}

