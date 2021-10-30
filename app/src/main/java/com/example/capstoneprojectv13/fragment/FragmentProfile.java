package com.example.capstoneprojectv13.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstoneprojectv13.LoginActivity;
import com.example.capstoneprojectv13.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnSignOut, btnChangePassword, btnChangeProfile;
    private FirebaseAuth mAuth;

    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        btnSignOut = view.findViewById(R.id.BtnSignOut);
        btnChangeProfile = view.findViewById(R.id.changeProfileDilaogBtn);
        btnChangePassword = view.findViewById(R.id.changePasswordDialaogBtn);
        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileDialog();
            }
        });
        return view;
    }

    private void showProfileDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_profile_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setView(view);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPasswordDialog(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_dialog, null);
        EditText passwordEt = view.findViewById(R.id.passwordEt);
        EditText newPasswordEt = view.findViewById(R.id.newPasswordEt);
        EditText cnewPasswordEt = view.findViewById(R.id.cnewPasswordEt);
        Button changePasswordBtn = view.findViewById(R.id.changePasswordBtn);


        final AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setView(view);



        AlertDialog dialog = builder.create();
        dialog.show();
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = passwordEt.getText().toString().trim();
                String newPassword = newPasswordEt.getText().toString().trim();
                String cnewPassword = cnewPasswordEt.getText().toString().trim();
                if(TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(getContext(), "Enter your current password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length() < 8){
                    Toast.makeText(getContext(), "Password must atleast 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(! cnewPassword.equals(newPassword)){
                    Toast.makeText(getContext(), "New password not Match", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                changePassword(oldPassword,newPassword);
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        final FirebaseUser user = mAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Password has been changed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}