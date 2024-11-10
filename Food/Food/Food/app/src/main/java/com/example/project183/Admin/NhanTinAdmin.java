package com.example.project183.Admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Adapter.MessageAdapter;
import com.example.project183.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import java.util.List;

public class NhanTinAdmin extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private ImageView imgBackNt;
    private MessageAdapter messageAdapter;
    private List<ChatMessage> messages;
    private DatabaseReference messagesRef;
    private static final String ADMIN_ID = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_tin_admin);

        initializeViews();
        setupFirebase();
        setupRecyclerView();
        setupListeners();
        loadMessages();
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessagesadm);
        editTextMessage = findViewById(R.id.editTextMessageadm);
        buttonSend = findViewById(R.id.buttonSendadm);
        imgBackNt = findViewById(R.id.imgbackntadm);
    }

    private void setupFirebase() {
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, ADMIN_ID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupListeners() {
        buttonSend.setOnClickListener(v -> sendMessage());
        imgBackNt.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            String messageId = messagesRef.push().getKey();
            ChatMessage message = new ChatMessage(messageId, ADMIN_ID, content, true);

            if (messageId != null) {
                messagesRef.child(messageId).setValue(message)
                        .addOnSuccessListener(aVoid -> {
                            editTextMessage.setText("");
                            recyclerViewMessages.smoothScrollToPosition(0);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(NhanTinAdmin.this,
                                        "Lỗi gửi tin nhắn", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void loadMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                if (message != null) {
                    messages.add(0, message);
                    messageAdapter.notifyItemInserted(0);
                    recyclerViewMessages.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NhanTinAdmin.this,
                        "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}