package com.example.musicchatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.example.musicchatapp.Adapters.MessagesAdapter;
import com.example.musicchatapp.Models.Message;
import com.example.musicchatapp.R;
import com.example.musicchatapp.databinding.ActivityGroupChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderuid;
    com.example.musicchatapp.databinding.ActivityGroupChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Group chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        senderuid= FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending Image....");
        dialog.setCancelable(false);
        messages=new ArrayList<>();
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt=binding.messageBox.getText().toString();
                Date date= new Date();
                Message message=new Message(messageTxt,senderuid,date.getTime());
                binding.messageBox.setText("");
                database.getReference().child("public")
                .push()
                .setValue(message);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}