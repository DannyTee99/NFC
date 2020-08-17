package com.developer.tapit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BankBalance extends Activity {

    private TextView fullname;
    private TextView bankbal;
    private TextView withdraw;
    private Button home;
    private Button loggo;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference calref = db.collection("Users").document(UserKey.key);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_balance);

        fullname = (TextView)findViewById(R.id.bb_name);
        bankbal = (TextView) findViewById(R.id.bb_balance);
        withdraw =(TextView)findViewById(R.id.bb_withdraw);
        home =(Button)findViewById(R.id.button_home);
        loggo=(Button)findViewById(R.id.button_loggo);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gohome();
            }
        });

        loggo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    //runs on start of activity
    protected void onStart() {
        super.onStart();
        calref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Bank banks = documentSnapshot.toObject(Bank.class);
                        String name = banks.getName();
                        int amount = banks.getAmount();
                        int lastwithdrawal = banks.getLastwithdrawal();

                        //gets name and bankaccount details from the database
                        //display details
                        fullname.setText(name);
                        bankbal.setText(String.valueOf(amount));
                        withdraw.setText(String.valueOf(lastwithdrawal));
                    }
                });

    }
    public void gohome(){
        Intent home = new Intent(this,HomeScreen.class);
        startActivity(home);
    }
    public void logout(){
        Intent logout = new Intent(this,loginPage.class);
        startActivity(logout);
    }

}
