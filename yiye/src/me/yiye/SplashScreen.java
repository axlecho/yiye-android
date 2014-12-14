package me.yiye;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import me.yiye.utils.Tools;

public class SplashScreen extends Activity {

    private ImageView slashBgImageView;
    private ImageView logoImageView;
    private Button loginBtn;
    private View unLoginPad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Tools.getInstance().cleanActivityStack();
        Tools.getInstance().addActivity(this);
        loginBtn = (Button) findViewById(R.id.btn_splash_login);
        logoImageView = (ImageView) findViewById(R.id.imageview_splash_logo);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManagerActivity.launch(SplashScreen.this);
            }
        });

        Animation biggerAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_pic_animation);
        biggerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (YiyeApplication.user != null) { //若已经登录，跳到主页
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        biggerAnimation.setFillAfter(true);

        Animation fadeinAnimation = AnimationUtils.loadAnimation(this,R.anim.splash_fadein_animation);
        Animation fadeinnMoveUpAnimation = AnimationUtils.loadAnimation(this,R.anim.splash_fadein_moveup_animation);
        fadeinnMoveUpAnimation.setFillAfter(true);

        slashBgImageView = (ImageView) findViewById(R.id.imageview_splash_bg);
        slashBgImageView.setAnimation(biggerAnimation);


        logoImageView = (ImageView)findViewById(R.id.imageview_splash_logo);
        logoImageView.setAnimation(fadeinAnimation);


        unLoginPad = findViewById(R.id.view_splash_unlogin_pad);
        if(YiyeApplication.user == null) {
            logoImageView.setAnimation(fadeinnMoveUpAnimation);
            unLoginPad.setAnimation(fadeinAnimation);
            unLoginPad.setVisibility(View.VISIBLE);
        }
        slashBgImageView.setVisibility(View.VISIBLE);
        logoImageView.setVisibility(View.VISIBLE);
    }

    public static void launch(Context context) {
        Intent i = new Intent();
        i.setClass(context, SplashScreen.class);
        context.startActivity(i);
    }
}
