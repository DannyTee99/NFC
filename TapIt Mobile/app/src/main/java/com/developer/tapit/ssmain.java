package com.developer.tapit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class ssmain extends Activity {
    Handler handler;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView txt;
    TextView txt1;

    private IsoDepAdapter isoDepAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreenmain);

        handler = new Handler();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image = findViewById(R.id.image_first);
        txt = findViewById(R.id.text_first);
        txt1 = findViewById(R.id.slogan);

        image.setAnimation(topAnim);
        txt.setAnimation(bottomAnim);
        txt1.setAnimation(bottomAnim);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(ssmain.this,loginPage.class);
                startActivity(intent);
                finish();

            }
        },8000);


    }


}

