package com.iblue.BluePuzzle;

import android.view.View;

public class HowToPlay extends BaseActivity {

    private View gView;

    @Override
    public void onCreate() {
        super.onCreate();

        getWindow().getAttributes().windowAnimations = R.style.win_animation;

        gView = addView(R.layout.activity_how_to_play);
    }

}
