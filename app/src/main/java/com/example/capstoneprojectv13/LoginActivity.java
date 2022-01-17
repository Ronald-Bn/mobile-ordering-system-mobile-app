package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    final String TAG = "LoginActivity";
    private TextView btn_ForgetPassword,registerTv;
    private EditText etEmail, etPassword;
    private Button btn_Register, btn_Login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btn_Login =findViewById(R.id.Btn_SignIn);
        btn_Register = findViewById(R.id.Btn_MainRegister);
        btn_ForgetPassword = findViewById(R.id.ForgetPassword);
        registerTv = findViewById(R.id.registerTv);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etEmail.getText().toString().equals("") && !etPassword.getText().toString().equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    etEmail.setVisibility(View.GONE);
                    etPassword.setVisibility(View.GONE);
                    btn_Login.setVisibility(View.GONE);
                    btn_Register.setVisibility(View.GONE);
                    btn_ForgetPassword.setVisibility(View.GONE);
                    registerTv.setVisibility(View.GONE);
                    checkUserAccessLevel(etEmail.getText().toString().trim());
                }else{
                    progressBar.setVisibility(View.GONE);
                    etEmail.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    btn_Login.setVisibility(View.VISIBLE);
                    btn_Register.setVisibility(View.VISIBLE);
                    btn_ForgetPassword.setVisibility(View.VISIBLE);
                    registerTv.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Field can not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                etEmail.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.VISIBLE);
                btn_Login.setVisibility(View.VISIBLE);
                btn_Register.setVisibility(View.VISIBLE);
                btn_ForgetPassword.setVisibility(View.VISIBLE);
                registerTv.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Users");
        databaseReference.orderByChild("Email").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {

                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object status = map.get("status");

                    if(String.valueOf(status).equals("blocked")){
                        progressBar.setVisibility(View.GONE);
                        etEmail.setVisibility(View.VISIBLE);
                        etPassword.setVisibility(View.VISIBLE);
                        btn_Login.setVisibility(View.VISIBLE);
                        btn_Register.setVisibility(View.VISIBLE);
                        btn_ForgetPassword.setVisibility(View.VISIBLE);
                        registerTv.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Blocked by Admin", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }else{
                        login(etEmail.getText().toString(),etPassword.getText().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            progressBar.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            btn_Login.setVisibility(View.GONE);
            btn_Register.setVisibility(View.GONE);
            btn_ForgetPassword.setVisibility(View.GONE);
            registerTv.setVisibility(View.GONE);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("Users");
            databaseReference.orderByChild("Email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object status = map.get("status");

                        if (String.valueOf(status).equals("blocked")) {
                            progressBar.setVisibility(View.GONE);
                            etEmail.setVisibility(View.VISIBLE);
                            etPassword.setVisibility(View.VISIBLE);
                            btn_Login.setVisibility(View.VISIBLE);
                            btn_Register.setVisibility(View.VISIBLE);
                            btn_ForgetPassword.setVisibility(View.VISIBLE);
                            registerTv.setVisibility(View.VISIBLE);
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(LoginActivity.this, "Blocked by Admin", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }else{
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            }
        }
    }
