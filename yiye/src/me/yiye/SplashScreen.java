package me.yiye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    private ImageView slashBgImageView;
    private ImageView logoImageView;
    private Button loginBtn;

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
                if (YiyeApplication.user != null) { //若已经登录，跳到主页
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } else { // 未登录显示登录按钮
                    logoImageView.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        shlashAnimation.setFillAfter(true);

        slashBgImageView = (ImageView) findViewById(R.id.imageview_splash_bg);
        slashBgImageView.setAnimation(shlashAnimation);
        slashBgImageView.setVisibility(View.VISIBLE);

        loginBtn = (Button) findViewById(R.id.btn_splash_login);
        logoImageView = (ImageView) findViewById(R.id.imageview_splash_logo);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManagerActivity.launch(SplashScreen.this);
            }
        });
    }
}
