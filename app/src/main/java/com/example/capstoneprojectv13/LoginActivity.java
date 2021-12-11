package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                    login(etEmail.getText().toString(),etPassword.getText().toString());
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
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            Log.d("TAG", "onSuccess" + documentSnapshot.getData());

            if(documentSnapshot.getString("isUsers") != null){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            progressBar.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            btn_Login.setVisibility(View.GONE);
            btn_Register.setVisibility(View.GONE);
            btn_ForgetPassword.setVisibility(View.GONE);
            registerTv.setVisibility(View.GONE);
            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("isUsers") != null){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            });
        }
    }
}