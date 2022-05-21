package com.grinleaf.tp14librarysearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends AppCompatActivity {

    ImageView iv;
    Animation ani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        iv= findViewById(R.id.intro_iv);

        ani= AnimationUtils.loadAnimation(this, R.anim.anim_intro);
        iv.startAnimation(ani);

        ani.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent= new Intent(IntroActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}