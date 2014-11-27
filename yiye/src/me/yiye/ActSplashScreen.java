package me.yiye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ActSplashScreen extends Activity {

    private ImageView slashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_splash_screen);

        Animation slashAnimation = AnimationUtils.loadAnimation(this, R.anim.slash_pic_animation);
        slashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(ActSplashScreen.this, MainActivity.class);
                startActivity(intent);
                ActSplashScreen.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slashAnimation.setFillAfter(true);

        slashImageView = (ImageView) findViewById(R.id.imageview_splash);
        slashImageView.setAnimation(slashAnimation);
        slashImageView.setVisibility(View.VISIBLE);
    }
}
