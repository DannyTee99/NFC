package com.NFC;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import javax.smartcardio.CardException;
import javax.swing.*;
import java.io.FileInputStream;

public class Main {

    private JPanel panel1;

    public static void main(String[] args) throws CardException, InterruptedException, Exception
    {

            FileInputStream serviceAccount =
                    new FileInputStream("**nfcfinal-8b849-firebase-adminsdk-t7g8z-aa94a48d4b.json"); //file location of .json file

            //Firestore connection
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://nfcfinal-8b849.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            Firestore db = FirestoreClient.getFirestore();
            System.out.println("Database connected");

        //1.Start communicator as thread
        try {
            Communicator androidCommunicator = new Communicator();
            androidCommunicator.run();

        }catch (Exception e){
            System.out.println("Scanner is not connected");
        }

    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

