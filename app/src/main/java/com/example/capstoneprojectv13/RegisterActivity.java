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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {




    private EditText firstNameEditText,lastNameEditText, etphonenum, etemail, etpassword, etconfirmpassword, etaddress, etzipcode;
    private Button btn_register;
    private CheckBox checkBox;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        getSupportActionBar().setElevation(0);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        etphonenum = findViewById(R.id.EditTextPhoneNumber);
        etemail = findViewById(R.id.EditTextRegisterEmail);
        etpassword = findViewById(R.id.EditTextPassword);
        etconfirmpassword = findViewById(R.id.EditTextConfirmPassword);
        etaddress = findViewById(R.id.EditTextAddress);
        etzipcode = findViewById(R.id.EditTextZipCode);
        btn_register = findViewById(R.id.Btn_Register);
        checkBox = findViewById(R.id.checkBox);

        dialog = new Dialog(this);

        btn_register.setOnClickListener(v -> {

            if (TextUtils.isEmpty(firstNameEditText.getText()) || TextUtils.isEmpty(lastNameEditText.getText()) || TextUtils.isEmpty(etphonenum.getText()) ||TextUtils.isEmpty(etemail.getText()) || TextUtils.isEmpty(etpassword.getText())
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
                intent.putExtra("fullname", firstNameEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim());
                intent.putExtra("phonenum", etphonenum.getText().toString().trim());
                intent.putExtra("email", etemail.getText().toString().trim());
                intent.putExtra("password", etpassword.getText().toString().trim());
                intent.putExtra("address", etaddress.getText().toString().trim());
                intent.putExtra("zipcode", etzipcode.getText().toString().trim());
                startActivity(intent);
                finish();
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