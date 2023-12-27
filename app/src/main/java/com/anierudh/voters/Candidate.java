package com.anierudh.voters;

import com.google.firebase.firestore.FieldValue;

public class Candidate {
    private String name;
    private String email;
    private int age;
    private String dob;
    private String documentId;
    private int voteCount; // New field for storing the vote count

    // Default constructor required for Firestore
    public Candidate() {
        // Required empty public constructor
    }

    // Constructor with parameters
    public Candidate(String name, String email, int age, String dob) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.dob = dob;
        this.voteCount = 0; // Initialize vote count to 0
    }

    // Getter and setter for the 'name' field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and setters for other fields

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
