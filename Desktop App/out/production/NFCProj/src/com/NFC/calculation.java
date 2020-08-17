package com.NFC;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import java.util.concurrent.ExecutionException;

public class calculation{
    Firestore db = FirestoreClient.getFirestore();  //establish firestore database

    DocumentReference calref = db.collection("Users").document(UserKey.key);

    public void AmountCal() throws ExecutionException, InterruptedException {   // calculates new balance and updates database
        ApiFuture<DocumentSnapshot> future = calref.get();
        // block on response
        DocumentSnapshot document = future.get();
        Amount amount = null;
        if (document.exists()) {
            // convert document to POJO
            amount = document.toObject(Amount.class);
            int amounts = amount.getAmount();

            int total = amounts - UserKey.amount;
            UserKey.total = total;
            System.out.println(total);
        } else {
            System.out.println("No such document!");
        }
    }
    public void Amountadd() throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> future = calref.update("amount", UserKey.total);



        WriteResult result = future.get();
        System.out.println("Write result: " + result);
        System.out.println("Bank Updated");

    }
    public void UpdateWithdraw() throws ExecutionException, InterruptedException {

        ApiFuture<WriteResult> future = calref.update("lastwithdrawal", UserKey.amount);



        WriteResult result = future.get();
        System.out.println("Write result: " + result);
        System.out.println("new amount Updated");

    }
}
