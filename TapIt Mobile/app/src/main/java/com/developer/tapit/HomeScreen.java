package com.developer.tapit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends Activity {

    private Button withdraw;
    private Button details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        withdraw= (Button) findViewById(R.id.button_withdraw);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openwithdraw();
            }
        });

        details=(Button) findViewById(R.id.button_details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openbb();
            }
        });
    }
    public void openwithdraw(){
        Intent with_intent = new Intent(this,Biometrics.class);
        startActivity(with_intent);
    }
    public void openbb(){
        Intent bb_intent = new Intent(this,BankBalance.class);
        startActivity(bb_intent);
    }


}

