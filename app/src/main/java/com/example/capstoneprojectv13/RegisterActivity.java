package com.example.capstoneprojectv13;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {




    private EditText etfullname , etphonenum, etemail, etpassword, etconfirmpassword, etaddress, etzipcode;
    private Button btn_register;
    private CheckBox checkBox;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        etfullname = findViewById(R.id.EditTextFullName);
        etphonenum = findViewById(R.id.EditTextPhoneNumber);
        etemail = findViewById(R.id.EditTextRegisterEmail);
        etpassword = findViewById(R.id.EditTextPassword);
        etconfirmpassword = findViewById(R.id.EditTextConfirmPassword);
        etaddress = findViewById(R.id.EditTextAddress);
        etzipcode = findViewById(R.id.EditTextZipCode);
        btn_register = findViewById(R.id.Btn_Register);
        checkBox = findViewById(R.id.checkBox);


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Toast.makeText(this, "" + dpWidth, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + dpHeight, Toast.LENGTH_SHORT).show();

        dialog = new Dialog(this);

        btn_register.setOnClickListener(v -> {

            if (TextUtils.isEmpty(etfullname.getText()) || TextUtils.isEmpty(etphonenum.getText()) ||TextUtils.isEmpty(etemail.getText()) || TextUtils.isEmpty(etpassword.getText())
            || TextUtils.isEmpty(etconfirmpassword.getText()) || TextUtils.isEmpty(etaddress.getText()) || TextUtils.isEmpty(etzipcode.getText())) {
                Toast.makeText(RegisterActivity.this, "You must fill in all of the fields", Toast.LENGTH_SHORT).show();
            } else if (etphonenum.getText().length() != 11) {
                Toast.makeText(RegisterActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
            } else if (!etpassword.getText().toString().trim().equals(etconfirmpassword.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "The password is not match", Toast.LENGTH_SHORT).show();
            }else if (etpassword.getText().toString().trim().length() < 8) {
                Toast.makeText(RegisterActivity.this, "Password must at least 8 characters", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(RegisterActivity.this, PhoneAuthActivity.class);
                intent.putExtra("fullname", etfullname.getText().toString());
                intent.putExtra("phonenum", etphonenum.getText().toString());
                intent.putExtra("email", etemail.getText().toString());
                intent.putExtra("password", etpassword.getText().toString());
                intent.putExtra("address", etaddress.getText().toString());
                intent.putExtra("zipcode", etzipcode.getText().toString());
                startActivity(intent);
            }
        });

    }

    public void termsAndCondition(View view){
        dialog.setContentView(R.layout.terms_condition);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose);
        Button btnOk = dialog.findViewById(R.id.BtnOk);
        dialog.show();

        imageViewClose.setOnClickListener(v -> dialog.cancel());
        btnOk.setOnClickListener(v -> dialog.cancel());
    }
}