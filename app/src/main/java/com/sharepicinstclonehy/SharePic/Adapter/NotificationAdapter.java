package com.sharepicinstclonehy.SharePic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharepicinstclonehy.SharePic.Fragment.PostDetailFragment;
import com.sharepicinstclonehy.SharePic.Fragment.ProfileFragment;
import com.sharepicinstclonehy.SharePic.Model.Notification;
import com.sharepicinstclonehy.SharePic.Model.Post;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification= mNotification.get(position);

        holder.comment.setText(notification.getText());

        getUser(holder.imageProfile,holder.username,notification.getUserid());

        if (!notification.isPost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());
        }else{
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!notification.isPost()){
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            .putString("postId", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container_view_tag,new PostDetailFragment()).commit();

                }else{
                    mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("profileId", notification.getUserid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container_view_tag,new ProfileFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile= itemView.findViewById(R.id.image_profile);
            postImage=itemView.findViewById(R.id.post_image);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }

    private void getUser(ImageView imageView, TextView textView, String userId ){  //id yi dene bi

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageView.setImageResource(R.drawable.user);
                }else{
                    Picasso.get().load(user.getImageurl()).into(imageView);
                }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPostImage(ImageView imageView, String postid){

        FirebaseDatabase.getInstance().getReference("Posts").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Picasso.get().load(post.getImageurl()).placeholder(R.drawable.user).into(imageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
