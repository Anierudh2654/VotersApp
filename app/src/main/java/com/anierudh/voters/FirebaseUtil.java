package com.anierudh.voters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {
    public static FirebaseFirestore firestore() {
        return FirebaseFirestore.getInstance();
    }
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static String currentemail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("candidates").document(currentUserId());
    }

    public static CollectionReference getcollection(){
        //FirebaseUser curr=FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("chat");
    }
}
