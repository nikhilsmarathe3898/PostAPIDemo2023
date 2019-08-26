package com.localgenie;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.localgenie.R;

public class ProgressActivity extends AppCompatActivity {

    Animation hide, visible;
    ProgressBar progress_bar;
    ImageView ivTickCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        progress_bar = findViewById(R.id.progress_bar);
        ivTickCheck = findViewById(R.id.ivTickCheck);
        hide = AnimationUtils.loadAnimation(this, R.anim.scaledown_progress_animation);
        visible = AnimationUtils.loadAnimation(this, R.anim.scaleup_progress_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.GONE);
                progress_bar.startAnimation(hide);
                ivTickCheck.setVisibility(View.VISIBLE);
                ivTickCheck.startAnimation(visible);

            }
        },3000);
    }
}
