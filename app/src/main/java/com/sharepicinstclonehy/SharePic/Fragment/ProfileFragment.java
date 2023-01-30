package com.sharepicinstclonehy.SharePic.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharepicinstclonehy.SharePic.Adapter.PhotoAdapter;
import com.sharepicinstclonehy.SharePic.EditProfileActivity;
import com.sharepicinstclonehy.SharePic.Model.Post;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.PostActivity;
import com.sharepicinstclonehy.SharePic.R;
import com.sharepicinstclonehy.SharePic.StartActivity;
import com.sharepicinstclonehy.SharePic.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;

    FirebaseUser firebaseUser;

    String profileid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding=FragmentProfileBinding.inflate(inflater, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileid=firebaseUser.getUid();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("profileId", "none");

        if (data.equals("none")) {
            profileid = firebaseUser.getUid();
        } else {
            profileid = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        binding.recyclerViewPic.setHasFixedSize(true);
        binding.recyclerViewPic.setLayoutManager(new GridLayoutManager(getContext(),3));

        myPhotoList= new ArrayList<>();
        photoAdapter= new PhotoAdapter(getContext(),myPhotoList);
        binding.recyclerViewPic.setAdapter(photoAdapter);
        //--------------
        binding.recyclerViewSaves.setHasFixedSize(true);
        binding.recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(),3));

        mySavedPosts= new ArrayList<>();
        postAdapterSaves= new PhotoAdapter(getContext(),mySavedPosts);
        binding.recyclerViewSaves.setAdapter(postAdapterSaves);

        binding.recyclerViewPic.setVisibility(View.VISIBLE);
        binding.recyclerViewSaves.setVisibility(View.GONE);

        //-----------
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPosts();
        //-------------

        if (profileid.equals(firebaseUser.getUid())){
            binding.editProfile.setText("Edit Profile");
        }else{
            checkFollowingStatus();
        }

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

                builder.setTitle("Log Out");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("Yea.. Nah",null);
                builder.setPositiveButton("Lemme Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finishAffinity();
                        Intent intent= new Intent(getActivity(), StartActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

        binding.myPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recyclerViewPic.setVisibility(View.VISIBLE);
                binding.recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        binding.mySaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recyclerViewPic.setVisibility(View.GONE);
                binding.recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.editProfile.getText().toString().equals("Edit Profile")){
                    Intent intent= new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                }else{
                    if (binding.editProfile.getText().toString().equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following").child(profileid).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("followers").child(profileid).setValue(true);

                        addNotification();

                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following").child(profileid).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("followers").child(profileid).removeValue();
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void getSavedPosts() {
        List<String>  savedIds= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            savedIds.add(dataSnapshot.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child("Posts").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mySavedPosts.clear();
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            Post post=dataSnapshot.getValue(Post.class);
                                            for (String id: savedIds){
                                                if (post.getPostid().equals(id)){
                                                    mySavedPosts.add(post);
                                                }
                                            }
                                        }
                                        postAdapterSaves.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference()
                .child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myPhotoList.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                            Post post= dataSnapshot.getValue(Post.class);

                            if (post.getPublisher().equals(profileid)){
                                myPhotoList.add(post);
                            }
                        }
                        Collections.reverse(myPhotoList);
                        photoAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(profileid).exists()){
                            binding.editProfile.setText("following");
                        }else{
                            binding.editProfile.setText("follow");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void addNotification(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> map =new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text","started following you.");
        map.put("postid", "");
        map.put("isPost",false);

        reference.push().setValue(map);
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(profileid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getImageurl()).into(binding.profilePic);
                        binding.username.setText(user.getUsername());
                        binding.fullname.setText(user.getFullname());
                        binding.bio.setText(user.getBio());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getFollowersAndFollowingCount() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(profileid);

        databaseReference.child("followers").addValueEventListener(new ValueEventListener() {               //getting follower count
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followers.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        databaseReference.child("following").addValueEventListener(new ValueEventListener() {               //getting followes count
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.following.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference()
                .child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int counter=0;
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Post post= dataSnapshot.getValue(Post.class);

                            if (post.getPublisher().equals(profileid)) counter++;
                        }
                        binding.posts.setText(String.valueOf(counter));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}