package com.developer.tapit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends Activity {

    Handler handler;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView txt;

    private IsoDepAdapter isoDepAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        handler = new Handler();

        //animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

       image = findViewById(R.id.imageView_ss);
        txt = findViewById(R.id.text_plswait);

        image.setAnimation(topAnim);
        txt.setAnimation(bottomAnim);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(SplashScreen.this,BankBalance.class);
                startActivity(intent);
                finish();

            }
        },5000);


    }


}
