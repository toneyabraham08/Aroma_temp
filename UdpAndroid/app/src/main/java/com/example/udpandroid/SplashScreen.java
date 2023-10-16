package com.example.udpandroid;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

public class SplashScreen extends AppCompatActivity {
  
    private static int SPLASH_SCREEN_TIME_OUT=3000;
    ImageView centerImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                     WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_main);

        centerImg = findViewById(R.id.imageView);
        /*RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(2);
        anim.setDuration(100);*/

        AlphaAnimation alpha = new AlphaAnimation(0, 100);
        alpha.setDuration(2000);

        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        android.R.anim.fade_in);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefFile",MODE_PRIVATE);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.startAnimation(alpha);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getBoolean("user_logged",false)) {
                    Intent i = new Intent(SplashScreen.this,
                            PropertiesActivity.class);
                    startActivity(i,
                            ActivityOptions
                                    .makeSceneTransitionAnimation(SplashScreen.this, centerImg, "splash_image").toBundle());
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this,
                            WelcomeActivity.class);
                    startActivity(i,
                            ActivityOptions
                                    .makeSceneTransitionAnimation(SplashScreen.this, centerImg, "splash_image").toBundle());
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}