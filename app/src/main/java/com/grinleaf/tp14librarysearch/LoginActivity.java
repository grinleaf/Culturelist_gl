package com.grinleaf.tp14librarysearch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grinleaf.tp14librarysearch.databinding.ActivityLoginBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    Boolean isFirst= true;
    Boolean isChanged= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginProfile.setOnClickListener(v->clickProfile());
        binding.loginBtn.setOnClickListener(v->clickBtn());

        SharedPreferences pref= getSharedPreferences("account",MODE_PRIVATE);
        G.profileImage= pref.getString("profileImage",null);;
        G.nickname= pref.getString("profileNickname",null);

        if(G.nickname!=null){
            Glide.with(LoginActivity.this).load(G.profileImage).into(binding.loginProfile);
            binding.loginNickname.setText(G.nickname);

            isFirst= false;
        }
    }

    void clickProfile(){
        Intent intent= new Intent(Intent.ACTION_PICK).setType("image/*");
        resultLauncher.launch(intent);
//        startActivityForResult(intent,0);
    }
    Uri profileUri;

    ActivityResultLauncher<Intent> resultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()!=RESULT_OK) return;
            profileUri= result.getData().getData();
            Glide.with(LoginActivity.this).load(profileUri).into(binding.loginProfile);

            isChanged= true;
        }
    });

    void clickBtn(){
        if(isFirst||isChanged) uploadProfile();
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void uploadProfile(){
        if(profileUri==null){
            binding.loginProfile.setImageResource(R.drawable.ic_launcher_foreground);
            binding.loginProfile.setBackgroundResource(R.color.profileDefault);
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String filename= "Img_"+sdf.format(new Date())+".png";

        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
        StorageReference userRef= firebaseStorage.getReference("userProfile/"+filename);
        userRef.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                userRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        G.profileImage= uri.toString();
                        G.nickname= binding.loginNickname.getText().toString();

                        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
                        CollectionReference userProfileRef= firebaseFirestore.collection("userProfile");

                        HashMap<String, Object> profile= new HashMap<>();
                        profile.put("profile",G.profileImage);
                        userProfileRef.document(G.nickname).set(profile);

                        SharedPreferences sharedPreferences= getSharedPreferences("account",MODE_PRIVATE);
                        SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.putString("profileImage",G.profileImage);
                        editor.putString("profileNickname",G.nickname);

                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}