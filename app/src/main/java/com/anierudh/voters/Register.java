package com.anierudh.voters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {
    private Candidate candidate;
    private EditText nameEditText;
    private EditText ageEditText;
    private EditText dobEditText;
    private Button submitButton;

    private Date registrationDeadlineDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        submitButton = findViewById(R.id.btnSubmit);
        nameEditText = findViewById(R.id.etName);
        ageEditText = findViewById(R.id.etAge);
        dobEditText = findViewById(R.id.etDob);
        submitButton.setOnClickListener((v) -> details());

        // Fetch registration deadline date from Firebase on app start
        fetchRegistrationDeadlineDate();
    }

    private void details() {
        // Check if the current date is before the registration deadline
        if (isRegistrationAllowed()) {
            String name = nameEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            String dob = dobEditText.getText().toString();
            String email = FirebaseUtil.currentemail();

            // Check if the email already exists in the "candidates" collection
            checkExistingEmail(email, name, age, dob);
        } else {
            // Display a toast indicating that registration is not allowed after the deadline
            Toast.makeText(this, "Registration is allowed only until " + formatDate(registrationDeadlineDate), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRegistrationDeadlineDate() {
        FirebaseUtil.firestore().collection("Dates").document("dates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String registrationDateString = document.getString("registration_date");
                                if (registrationDateString != null) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                        registrationDeadlineDate = sdf.parse(registrationDateString);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        // Handle parsing errors appropriately
                                        Toast.makeText(Register.this, "Error parsing date", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle the case where the date string is null
                                    Toast.makeText(Register.this, "Error: Registration date string is null", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the case where the document doesn't exist
                                Toast.makeText(Register.this, "Error: Dates document not found in Firebase", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle errors fetching the document
                            Toast.makeText(Register.this, "Error fetching dates from Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean isRegistrationAllowed() {
        // Get the current date
        Date currentDate = new Date();

        // Compare the current date with the registration deadline
        return currentDate.before(registrationDeadlineDate) || currentDate.equals(registrationDeadlineDate);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private void checkExistingEmail(String email, String name, int age, String dob) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(Register.this, "You have already registered", Toast.LENGTH_SHORT).show();
                        // Email already exists, handle accordingly
                    } else {
                        // Email doesn't exist, proceed with registration
                        candidate = new Candidate(name, email, age, dob);
                        regCandidate();
                    }
                } else {
                    // Handle the error during Firestore retrieval
                }
            }
        });
    }

    private void regCandidate() {
        FirebaseUtil.currentUserDetails().set(candidate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> updateTask) {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(Register.this, "Successfully registered as a candidate", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(Register.this, Home.class);
                            startActivity(loginIntent);
                            finish();
                        } else {
                            // Handle the error during Firestore update
                        }
                    }
                });
    }

    // Rest of your code...
}
