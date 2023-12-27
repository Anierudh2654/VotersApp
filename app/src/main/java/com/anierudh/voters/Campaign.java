package com.anierudh.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.google.firebase.firestore.Query;

public class Campaign extends AppCompatActivity {

    private EditText chatEditText;
    private Button sendButton;
    private LinearLayout chatLinearLayout;
    private ScrollView chatScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        chatEditText = findViewById(R.id.chat);
        sendButton = findViewById(R.id.Sendchat);
        chatLinearLayout = findViewById(R.id.campaignLinearLayout);
        chatScrollView = findViewById(R.id.campaignScrollView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChat();
            }
        });

        // Start listening for chat messages
        startChatListener();
    }

    private void saveChat() {
        isCurrentUserCandidate(new CandidateCheckCallback() {
            @Override
            public void onCandidateChecked(boolean isCandidate, String candidateName) {
                if (isCandidate) {
                    // The current user is a candidate, proceed with saving the chat
                    String chatText = chatEditText.getText().toString();
                    String userName = candidateName;

                    if (userName == null || userName.isEmpty()) {
                        // Handle the case where the user doesn't have a name
                        // You may show an error message or take appropriate action
                        return;
                    }

                    Chat chatObject = new Chat();
                    chatObject.setChat(chatText);
                    chatObject.setName(userName);
                    java.util.Date currentDate = new java.util.Date();

// Convert java.util.Date to com.google.firebase.Timestamp
                    Timestamp timestamp = new Timestamp(currentDate);
                    chatObject.setTimestamp(timestamp);

                    // Assuming "chat" is a collection in Firestore
                    FirebaseFirestore.getInstance().collection("chat").add(chatObject)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> updateTask) {
                                    if (updateTask.isSuccessful()) {
                                        // Handle success, e.g., show a success message
                                        chatEditText.setText(""); // Clear the EditText after sending the message
                                    } else {
                                        // Handle the error during Firestore update
                                        Toast.makeText(Campaign.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Display a toast message indicating that only candidates can participate in the campaign
                    Toast.makeText(Campaign.this, "Only candidates can participate in the campaign", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startChatListener() {
        FirebaseFirestore.getInstance().collection("chat")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        // Handle the error
                        return;
                    }

                    List<DocumentChange> documentChanges = value.getDocumentChanges();

                    for (int i = documentChanges.size() - 1; i >= 0; i--) {
                        DocumentChange dc = documentChanges.get(i);
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            // A new message is added, display it in the chat
                            Chat chat = dc.getDocument().toObject(Chat.class);
                            createChatLayout(chat);
                        }
                    }
                });
    }

    private void isCurrentUserCandidate(CandidateCheckCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("candidates").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // User is a candidate
                                    String candidateName = document.getString("name");
                                    callback.onCandidateChecked(true, candidateName);
                                } else {
                                    // User is not a candidate
                                    callback.onCandidateChecked(false, null);
                                }
                            } else {
                                // Handle the error during Firestore retrieval
                                callback.onCandidateChecked(false, null);
                            }
                        }
                    });
        } else {
            callback.onCandidateChecked(false, null);
        }
    }

    private void createChatLayout(Chat chat) {
        // Create a new ConstraintLayout for each chat
        ConstraintLayout chatLayout = new ConstraintLayout(this);
        chatLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        chatLayout.setBackgroundResource(android.R.drawable.btn_default);

        // Create TextView for chat message
        TextView messageTextView = new TextView(this);
        messageTextView.setText(chat.getName() + ": " + chat.getChat());
        messageTextView.setTextSize(16);

        // Add the TextView to the ConstraintLayout
        chatLayout.addView(messageTextView);

        // Add the ConstraintLayout to the end of the LinearLayout
        chatLinearLayout.addView(chatLayout);

        // Scroll to the bottom to show the latest message
        chatScrollView.fullScroll(View.FOCUS_DOWN);
    }

    interface CandidateCheckCallback {
        void onCandidateChecked(boolean isCandidate, String candidateName);
    }
}
