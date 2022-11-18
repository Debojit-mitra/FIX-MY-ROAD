package com.bunny.fixmyroad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView splash_logo;
    private TextView splash_fix_my_road, splash_slogan,developedby;
    private LinearLayout splash_screen_liner_layout;

    Animation topAnimation,bottomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splash_logo = findViewById(R.id.splash_logo);
        splash_fix_my_road = findViewById(R.id.splash_fix_my_road);
        splash_slogan = findViewById(R.id.splash_slogan);
        developedby = findViewById(R.id.developedby);
        splash_screen_liner_layout = findViewById(R.id.splash_screen_liner_layout);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        splash_logo.setAnimation(topAnimation);
        splash_fix_my_road.setAnimation(topAnimation);
        splash_slogan.setAnimation(topAnimation);
        developedby.setAnimation(bottomAnimation);
        splash_screen_liner_layout.setAnimation(topAnimation);

        int SPLASH_SCREEN = 2300;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);


    }
}