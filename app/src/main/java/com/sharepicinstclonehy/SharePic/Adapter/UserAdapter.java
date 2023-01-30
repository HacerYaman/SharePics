package com.sharepicinstclonehy.SharePic.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sharepicinstclonehy.SharePic.Fragment.ProfileFragment;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {   //???????????

    private Context mContext;
    private List<User> mUser;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User final_user=mUser.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.searchUsername.setText(final_user.getUsername());
        holder.searchFullname.setText(final_user.getFullname());
        Glide.with(mContext).load(final_user.getImageurl()).into(holder.image_profile);

        isFollowing(final_user.getId(),holder.btn_follow);

        if (final_user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor= mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",final_user.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container_view_tag,new ProfileFragment()).commit();

            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(firebaseUser.getUid()).child("following").child(final_user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(final_user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(final_user.getId());

                }else{
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(firebaseUser.getUid()).child("following").child(final_user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(final_user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }


    private void addNotification(String userid){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> map =new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text","started following you.");
        map.put("postid", "");
        map.put("isPost",false);
        reference.push().setValue(map);
    }


    @Override
    public int getItemCount() {

        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView searchUsername;
        public TextView searchFullname;
        public CircleImageView image_profile;
        public Button btn_follow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            searchUsername=itemView.findViewById(R.id.searchUsernameTextID);
            searchFullname=itemView.findViewById(R.id.searchFullnameTextID);
            image_profile=itemView.findViewById(R.id.image_profileID);
            btn_follow=itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(String userid, Button button ) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

