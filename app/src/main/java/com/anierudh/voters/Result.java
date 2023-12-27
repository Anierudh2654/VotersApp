package com.anierudh.voters;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.firebase.firestore.Query; // Import Query class
import com.google.firebase.firestore.QuerySnapshot;

public class Result extends AppCompatActivity {

    private LinearLayout resultLinearLayout;
    private ScrollView resultScrollView;
    private TextView resultTextView;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultLinearLayout = findViewById(R.id.resultLinearLayout);
        resultScrollView = findViewById(R.id.resultScrollView);
        resultTextView = findViewById(R.id.resultTextView);

        // Fetch dates from the Firebase collection
        firestore.collection("Dates").document("dates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String resultDateString = document.getString("results_date");
                                String registrationsDateString = document.getString("registration_date");
                                String votingDateString = document.getString("voting_date");

                                // Example of parsing strings into Date objects
                                Date resultDate = parseDate(resultDateString);
                                Date registrationsDate = parseDate(registrationsDateString);
                                Date votingDate = parseDate(votingDateString);

                                // Call the function to load results
                                loadResults(resultDate);

                            } else {
                                // Handle the case where the document doesn't exist
                                resultTextView.setText("Error: Dates document not found in Firebase");
                            }
                        } else {
                            // Handle errors fetching the document
                            resultTextView.setText("Error fetching dates from Firebase");
                        }
                    }
                });
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing errors appropriately
            return null; // or throw an exception
        }
    }

    private void loadResults(Date resultDate) {
        // Check if it's not yet the result date
        if (isBeforeResultDate(resultDate)) {
            // Display the message indicating when the results will be available
            resultTextView.setText("Results will be available after " + formatDate(resultDate));
        } else {
            // Query to get candidates with the highest vote count
            FirebaseFirestore.getInstance().collection("candidates")
                    .orderBy("voteCount", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String candidateName = document.getString("name");
                                    int voteCount = document.getLong("voteCount").intValue();

                                    // Display the result
                                    displayResult(candidateName, voteCount);
                                }
                            } else {
                                // Handle the error during Firestore retrieval
                            }
                        }
                    });
        }
    }

    private boolean isBeforeResultDate(Date resultDate) {
        // Get the current date
        Date currentDate = new Date();

        // Compare the current date with the result date
        return currentDate.before(resultDate);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private void displayResult(String candidateName, int voteCount) {
        // Create a TextView to display the result
        TextView resultTextView = new TextView(this);
        resultTextView.setText(candidateName + ": " + voteCount + " votes");
        resultTextView.setTextSize(18);

        // Add the TextView to the LinearLayout
        resultLinearLayout.addView(resultTextView);
    }
}
