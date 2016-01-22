package com.iblue.BluePuzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainMenu extends BaseActivity {

    SharedPreferences settings;
    SoundPool sp;
    int scm, wel;
    @Override
    public void onCreate() {
        super.onCreate();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        scm = sp.load(this, R.raw.scm, 1);
        wel = sp.load(this, R.raw.wel, 1);

        addView(R.layout.menu);
        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        boolean start = settings.getBoolean("start", true);

        if(start){
            starter();
            start = false;
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("start", start);
            editor.commit();
        }


    }

    public void starter(){
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.welcomedialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final TextView logs = (TextView) promptsView
                .findViewById(R.id.thanku);
        logs.setTextSize(25);
        logs.setText(R.string.start);
        final TextView hh = (TextView) promptsView
                .findViewById(R.id.ht);

        hh.setText(R.string.htht);
        final TextView ll = (TextView) promptsView
                .findViewById(R.id.lt);

        ll.setText(" Lifez a Game, Play it ");

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                blue(findViewById(R.id.ic));
            }
        });
        alertDialog.show();
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        scm = sp.load(this, R.raw.scm, 1);
        if(id == R.id.play_button) {
            play();
        } else if(id == R.id.settings_button) {
            settings();
        } else if(id == R.id.about_button) {
            about();
        }
    }


    private void play() {
        startActivity(new Intent(this, MainActity.class));
    }

    private void settings() {
        startActivity(new Intent(this, HowToPlay.class));
    }

    private void about() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void blue(View view) {
        YoYo.with(Techniques.RotateIn)
                .duration(700)
                .playOn(view);
        //sp.play(wel, 0.4f, 0.4f, 0, 0, 1);
    }
}
