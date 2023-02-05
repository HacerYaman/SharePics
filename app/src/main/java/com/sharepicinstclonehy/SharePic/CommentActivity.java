package com.sharepicinstclonehy.SharePic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharepicinstclonehy.SharePic.Adapter.CommentAdapter;
import com.sharepicinstclonehy.SharePic.Model.Comment;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding activityCommentBinding;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    String authorId;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCommentBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = activityCommentBinding.getRoot();
        setContentView(view);

        Intent intent=getIntent();
        postId= intent.getStringExtra("postId");
        authorId= intent.getStringExtra("authorId");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        activityCommentBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserImage();
        getComment();
    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Comment comment= dataSnapshot.getValue(Comment.class);
                            commentList.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user= snapshot.getValue(User.class);
                        if (user.getImageurl().isEmpty()){
                            activityCommentBinding.imageProfile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Picasso.get().load(user.getImageurl()).into(activityCommentBinding.imageProfile);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void postClicked(View view ){
        if(TextUtils.isEmpty(activityCommentBinding.addComment.getText().toString())){
            Toast.makeText(CommentActivity.this, "First add your comment.", Toast.LENGTH_LONG).show();
        }else {
            putComment();
            activityCommentBinding.addComment.setText("");
            addNotification();
        }
    }

    private void putComment() {

        String commentid= FirebaseDatabase.getInstance().getReference("Comments").push().getKey();

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("comment",activityCommentBinding.addComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());//COMMENT PUBLISHER
        hashMap.put("commentid", commentid);

        FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CommentActivity.this, "Comment Added!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CommentActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNotification(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(authorId);

        HashMap<String, Object> map =new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text","commmented.");
        map.put("postid", postId);
        map.put("isPost",true);

        reference.push().setValue(map);
    }
}