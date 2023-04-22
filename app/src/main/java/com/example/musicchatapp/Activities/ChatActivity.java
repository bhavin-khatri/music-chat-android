package com.example.musicchatapp.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.musicchatapp.Adapters.MessagesAdapter;
import com.example.musicchatapp.Models.Message;
import com.example.musicchatapp.R;
import com.example.musicchatapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.musicchatapp.R.id.image;

 public class ChatActivity extends AppCompatActivity {

     public static final int CAMERA_ACTION_CODE=101;

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String senderRoom,receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;

     String receiveruid;
     String senderuid;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
         database = FirebaseDatabase.getInstance();
         storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending Image....");
        dialog.setCancelable(false);
        messages=new ArrayList<>();
        adapter=new MessagesAdapter(this,messages,senderRoom,receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        String name=getIntent().getStringExtra("name");
         String profile=getIntent().getStringExtra("image");
         binding.name.setText(name);
         Glide.with(ChatActivity.this).load(profile)
                 .placeholder(R.drawable.defaultavatar)
                 .into(binding.image);

         binding.backArrow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });
        receiveruid=getIntent().getStringExtra("uid");
        senderuid = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(receiveruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status=snapshot.getValue(String.class);
                    if(!status.isEmpty()) {
                        if(status.equals("Offline")){
                            binding.status.setVisibility(View.GONE);
                        }else{
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom=senderuid+receiveruid;
        receiverRoom=receiveruid+senderuid;

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            Message message=snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt=binding.messageBox.getText().toString();
                Date date= new Date();
                Message message=new Message(messageTxt,senderuid,date.getTime());
                binding.messageBox.setText("");
                String randomKey=database.getReference().push().getKey();

                HashMap<String,Object>lastMsgObj=new HashMap<>();
                lastMsgObj.put("lastMsg",message.getMessage());
                lastMsgObj.put("lastMsgTime",date.getTime());
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        HashMap<String,Object>lastMsgObj=new HashMap<>();
                        lastMsgObj.put("lastMsg",message.getMessage());
                        lastMsgObj.put("lastMsgTime",date.getTime());
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    }
                });
            }
        });
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });

         final Handler handler=new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderuid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(usersStoppedTyping,1000);

            }
            Runnable usersStoppedTyping=new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderuid).setValue("Online");
                }
            };
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // getSupportActionBar().setTitle(name);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==25)
         {
             if(data!=null){
                 if(data.getData()!=null){
                     Uri selectedImage= data.getData();
                     Calendar calendar= Calendar.getInstance();
                     StorageReference reference=storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                     dialog.show();
                     reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             if(task.isSuccessful())
                             {
                                 dialog.dismiss();
                                 reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                     @Override
                                     public void onSuccess(Uri uri) {
                                         String filePath=uri.toString();
                                         String messageTxt=binding.messageBox.getText().toString();
                                         Date date= new Date();
                                         Message message=new Message(messageTxt,senderuid,date.getTime());
                                         message.setMessage("photo");
                                         message.setImageUrl(filePath);
                                         binding.messageBox.setText("");
                                         String randomKey=database.getReference().push().getKey();

                                         HashMap<String,Object>lastMsgObj=new HashMap<>();
                                         lastMsgObj.put("lastMsg",message.getMessage());
                                         lastMsgObj.put("lastMsgTime",date.getTime());
                                         database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                         database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                         database.getReference().child("chats")
                                                 .child(senderRoom)
                                                 .child("messages")
                                                 .child(randomKey)
                                                 .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 database.getReference().child("chats")
                                                         .child(receiverRoom)
                                                         .child("messages")
                                                         .child(randomKey)
                                                         .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {

                                                     }
                                                 });
                                                 HashMap<String,Object>lastMsgObj=new HashMap<>();
                                                 lastMsgObj.put("lastMsg",message.getMessage());
                                                 lastMsgObj.put("lastMsgTime",date.getTime());
                                                 database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                                 database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                             }
                                         });
                                         //Toast.makeText(ChatActivity.this,filePath,Toast.LENGTH_SHORT).show();
                                     }
                                 });
                             }
                         }
                     });
                 }
             }
         }
     }
     @Override
     protected void onResume() {
         super.onResume();
         String currenId= FirebaseAuth.getInstance().getUid();
         database.getReference().child("presence").child(currenId).setValue("Online");
     }


     @Override
     protected void onPause() {
         String currenId= FirebaseAuth.getInstance().getUid();
         database.getReference().child("presence").child(currenId).setValue("Offline");
         super.onPause();

     }
     @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}