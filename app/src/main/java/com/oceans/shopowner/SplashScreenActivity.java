package com.oceans.shopowner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.oceans.shopowner.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000; ///set to 3000

    Animation topAnim, bottomAnim;
    ImageView image,logo;
    SharedPreferences isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        isLogin = getSharedPreferences("login",MODE_PRIVATE);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logo = findViewById(R.id.krayan);
        logo.setAnimation(bottomAnim);
        try{
            Glide.with(getApplicationContext())
                    .load(R.drawable.circle_cropped)
                    .centerCrop()
                    .useAnimationPool(true)
                    .into(logo);
        }catch (Exception e){
            Glide.with(getApplicationContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/grocery-a3260.appspot.com/o/KRAYN%2Fcircle-cropped.png?alt=media&token=bc8a5bb9-599c-405f-8f98-e6f3577fe409")
                    .centerCrop()
                    .useAnimationPool(true)
                    .into(logo);
        }
        /*
        //Hooks
        image = findViewById(R.id.imageView);


        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLogin.getBoolean("login",false)){
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent1 = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent1);
                }
            }
        },SPLASH_SCREEN);
    }
}