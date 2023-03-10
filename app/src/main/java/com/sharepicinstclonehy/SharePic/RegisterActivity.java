package com.sharepicinstclonehy.SharePic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sharepicinstclonehy.SharePic.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegisterBinding;            //view binding
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = activityRegisterBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();

        activityRegisterBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(activityRegisterBinding.checkbox.isChecked()){
                    activityRegisterBinding.registerButton.setEnabled(true);
                }else{
                    activityRegisterBinding.registerButton.setEnabled(false);
                }
            }

        });

        activityRegisterBinding.goPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(RegisterActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });

        activityRegisterBinding.regloginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    public void registerClicked(View view){

        progressDialog= new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Wait a sec darling..");
        progressDialog.show();

        String str_username= activityRegisterBinding.regusernameEditTxt.getText().toString();
        String str_fullname= activityRegisterBinding.regfullnameEditTxt.getText().toString();
        String str_email= activityRegisterBinding.regemailEditTxt.getText().toString();
        String str_password= activityRegisterBinding.regpasswordEditTxt.getText().toString();

        if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){

            Toast.makeText(RegisterActivity.this,"Incomplete Information >:[",Toast.LENGTH_LONG).show();

        }else{
            register(str_username,str_fullname,str_email,str_password);
        }
    }

    private void register(String username, String fullname, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            HashMap<String, Object> hashMap= new HashMap<>();

                            hashMap.put("username",username);
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/newig14.appspot.com/o/userrr.png?alt=media&token=68acd621-2dd9-49d8-8a1a-991e6e2bf5ad");

                            hashMap.put("email",email);
                            hashMap.put("id",mAuth.getCurrentUser().getUid());

                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).
                                    setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password. Keep your password min 6 char.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
