package com.developer.tapit;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class loginPage extends Activity {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private Button LoginButton;
    private EditText username;

    private EditText password;

    //Database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersref = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        username = (EditText) findViewById(R.id.editTextUser);
        password = (EditText) findViewById(R.id.editTextPass);

    }

           public void login(View v) {
            usersref.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String data = "";

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Users users = documentSnapshot.toObject(Users.class);
                                    String user = users.getUsername();
                                    String pwd = users.getPassword();


                                    if (username.getText().toString().equals(user) & password.getText().toString().equals(pwd)) {
                                        UserKey.key=documentSnapshot.getId().toString();
                                        Intent home = new Intent(loginPage.this, HomeScreen.class);
                                        startActivity(home);
                                        //Toast.makeText(loginPage.this, UserKey.key, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(loginPage.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(loginPage.this, "Something went wrong, Please Try AGAIN!!", Toast.LENGTH_SHORT).show();
                        }
                    });

                                      }}


