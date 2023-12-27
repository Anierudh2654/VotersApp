package com.anierudh.voters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.firestore.Query; // Import Query class
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Vote extends AppCompatActivity {

    private LinearLayout candidatesLayout;
    private boolean hasVoted = false; // Flag to track whether the user has already voted
    private String userId; // User ID obtained from Firebase authentication
    private String votingAllowedDate; // Date retrieved from Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        candidatesLayout = findViewById(R.id.candidatesLayout);

        // Retrieve user ID from Firebase authentication (replace this with your actual authentication logic)
        userId = FirebaseUtil.currentemail();

        // Load candidate names dynamically from Firestore
        loadCandidateNames();

        // Check if the user has already voted
        checkVotingStatus();

        // Fetch voting allowed date from Firebase on app start
        fetchVotingAllowedDate();
    }

    private void fetchVotingAllowedDate() {
        FirebaseFirestore.getInstance().collection("Dates").document("dates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                votingAllowedDate = document.getString("voting_date");
                            } else {
                                // Handle the case where the document doesn't exist
                                Toast.makeText(Vote.this, "Error: Dates document not found in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle errors fetching the document
                            Toast.makeText(Vote.this, "Error fetching dates from Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkVotingStatus() {
        FirebaseFirestore.getInstance().collection("userVotes").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User has already voted
                                hasVoted = true;
                                // You can disable UI elements or handle this situation as needed
                            } else {
                                // User has not voted
                                hasVoted = false;
                            }
                        } else {
                            // Handle the error during Firestore retrieval
                        }
                    }
                });
    }

    private void loadCandidateNames() {
        FirebaseFirestore.getInstance().collection("candidates").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Candidate> candidates = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Candidate candidate = document.toObject(Candidate.class);
                                candidate.setDocumentId(document.getId()); // Set the document ID
                                candidates.add(candidate);
                            }

                            // Create UI elements for each candidate
                            for (Candidate candidate : candidates) {
                                createCandidateLayout(candidate);
                            }
                        } else {
                            // Handle the error during Firestore retrieval
                        }
                    }
                });
    }

    private void createCandidateLayout(Candidate candidate) {
        // Create a new ConstraintLayout for each candidate
        ConstraintLayout candidateLayout = new ConstraintLayout(this);
        candidateLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        candidateLayout.setBackgroundResource(android.R.drawable.btn_default);

        // Create TextView for candidate name
        TextView textViewCandidateName = new TextView(this);
        textViewCandidateName.setId(View.generateViewId());
        textViewCandidateName.setText(candidate.getName());
        textViewCandidateName.setTextSize(18);
        textViewCandidateName.setPadding(16, 16, 16, 16);

        // Set content description for accessibility
        textViewCandidateName.setContentDescription("Vote for " + candidate.getName());

        // Set constraints for TextView
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        textViewCandidateName.setLayoutParams(params);

        // Set click listener
        candidateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCandidateClicked(candidate);
            }
        });

        // Add TextView to ConstraintLayout
        candidateLayout.addView(textViewCandidateName);

        // Add ConstraintLayout to the parent LinearLayout
        candidatesLayout.addView(candidateLayout);
    }

    public void onCandidateClicked(Candidate candidate) {
        // Check if the current date is the voting allowed date
        if (isVotingAllowed() && !hasVoted) {
            // Handle candidate click event
            registerVote(candidate.getDocumentId());

            // Mark the user as voted in Firestore
            markUserAsVoted();

            // Display a toast indicating that the user has voted
            Toast.makeText(this, "You have voted for " + candidate.getName(), Toast.LENGTH_SHORT).show();

            // Refresh the UI with updated vote counts
            loadCandidateNames();

            // Navigate to the main activity
            Intent intent = new Intent(Vote.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (hasVoted) {
            // Display a toast indicating that the user has already voted
            Toast.makeText(this, "You have already voted.", Toast.LENGTH_SHORT).show();
        } else {
            // Display a toast indicating that voting is not allowed on the current date
            Toast.makeText(this, "Voting is allowed only on " + votingAllowedDate, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isVotingAllowed() {
        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());

        // Compare the current date with the voting allowed date
        return currentDate.equals(votingAllowedDate);
    }

    private void registerVote(String candidateDocumentId) {
        DocumentReference candidateRef = FirebaseFirestore.getInstance().collection("candidates")
                .document(candidateDocumentId);

        // Use a transaction to ensure atomicity
        candidateRef.update("voteCount", FieldValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Vote registered successfully
                            // You can add additional handling if needed
                        } else {
                            // Handle the error during Firestore update
                            Toast.makeText(Vote.this, "Failed to register vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void markUserAsVoted() {
        FirebaseFirestore.getInstance().collection("userVotes").document(userId)
                .set(new HashMap<String, Object>())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User marked as voted successfully
                            // You can add additional handling if needed
                        } else {
                            // Handle the error during Firestore update
                            Toast.makeText(Vote.this, "Failed to mark user as voted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
