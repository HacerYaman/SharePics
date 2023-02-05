package com.sharepicinstclonehy.SharePic;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sharepicinstclonehy.SharePic.Model.User;
import com.sharepicinstclonehy.SharePic.databinding.ActivityEditProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private ActivityEditProfileBinding binding;
    private FirebaseUser firebaseUser;
    private StorageTask task;
    private StorageReference storageReference;
    Uri imageDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");

        registerLauncher();

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        binding.fullname.setText(user.getFullname());
                        binding.username.setText(user.getUsername());
                        binding.bio.setText(user.getBio());
                        Picasso.get().load(user.getImageurl()).into(binding.imageProfile);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                            Intent intent = new Intent(EditProfileActivity.this, StartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                });
                builder.show();
            }
        });

    }

    private void saveChanges(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", binding.fullname.getText().toString());
        map.put("username", binding.username.getText().toString());
        map.put("bio", binding.bio.getText().toString());

        uploadImage();

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid()).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfileActivity.this, "Changes Saved!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void changePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Sovviy permission needed for gallery.", Snackbar.LENGTH_INDEFINITE).setAction("Gimme the permission bitch.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    public void uploadImage() {
        if (imageDat != null) {
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpeg");

            task = fileRef.putFile(imageDat);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return  fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("imageurl").setValue(url);

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                imageDat = intentFromResult.getData();
                                binding.imageProfile.setImageURI(imageDat);
                            }
                        }
                    }
                });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(EditProfileActivity.this,"Permisson needed! >:[",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}