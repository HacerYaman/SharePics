package com.sharepicinstclonehy.SharePic.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharepicinstclonehy.SharePic.Fragment.ProfileFragment;
import com.sharepicinstclonehy.SharePic.MainActivity;
import com.sharepicinstclonehy.SharePic.Model.Comment;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.comImageProfile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SharedPreferences preferences= mContext.getSharedPreferences("POSTIDINFO", Context.MODE_PRIVATE);
        String postId= preferences.getString("postId","");


        Comment comment= mComments.get(position);

        holder.comment.setText(comment.getComment());

        FirebaseDatabase. getInstance().getReference()
                .child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user=snapshot.getValue(User.class);
                        holder.username.setText(user.getUsername());
                        if (user.getImageurl().isEmpty()){
                            holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                FirebaseDatabase.getInstance()
                        .getReference("Comments").child(postId).child(comment.getCommentid()).removeValue();

                return false;
            }
        });*/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String current_user= FirebaseAuth.getInstance().getCurrentUser().getUid();

                //---------------
                if (current_user.equals(comment.getPublisher())){
                    new AlertDialog.Builder(mContext)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this comment?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    FirebaseDatabase.getInstance()
                                            .getReference("Comments").child(postId).child(comment.getCommentid()).removeValue();
                                    FirebaseDatabase.getInstance()
                                            .getReference("Comments").child(postId).child(comment.getPublisher()).removeValue();
                                    FirebaseDatabase.getInstance()
                                            .getReference("Comments").child(postId).child(comment.getComment()).removeValue();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }else {
                    Toast.makeText(mContext, "You don't have permission to make changes.", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return mComments.size();
    }
}

