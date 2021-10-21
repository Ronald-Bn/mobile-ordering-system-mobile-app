package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private Button btn_SendForgetPassword;
    private EditText etSendEmail;
    private String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();
        etSendEmail = findViewById(R.id.etEmail);

        btn_SendForgetPassword = findViewById(R.id.Btn_ForgetPassword);
        btn_SendForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etSendEmail.getText().toString().trim();
                if(!email.equals("")){
                    forgetPass();
                }else{
                    Toast.makeText(PasswordActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void forgetPass(){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                    finish();
                }else{
                    Toast.makeText(PasswordActivity.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
