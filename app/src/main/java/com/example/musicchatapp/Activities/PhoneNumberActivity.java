package com.example.musicchatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.musicchatapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(PhoneNumberActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        String ccp = binding.cpp.getTextView_selectedCountry().getText().toString();
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(PhoneNumberActivity.this,OTPActivity.class);
                intent.putExtra("phoneNumber", ccp + binding.phoneBox.getText().toString());
                startActivity(intent);
            }
        });
    }
}