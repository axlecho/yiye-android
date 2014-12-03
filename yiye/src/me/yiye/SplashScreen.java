package me.yiye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    private ImageView slashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Animation shlashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_pic_animation);
        shlashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        shlashAnimation.setFillAfter(true);

        slashImageView = (ImageView) findViewById(R.id.imageview_splash);
        slashImageView.setAnimation(shlashAnimation);
        slashImageView.setVisibility(View.VISIBLE);
    }
}
