package com.iblue.BluePuzzle;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AboutActivity extends BaseActivity {
    private View gView;

    @Override
    public void onCreate() {
        super.onCreate();
        getWindow().getAttributes().windowAnimations = R.style.win_animation;

        gView = addView(R.layout.about);
    }

    public void blue(View view) {
        YoYo.with(Techniques.RotateIn)
                .duration(700)
                .playOn(view);
    }
}
