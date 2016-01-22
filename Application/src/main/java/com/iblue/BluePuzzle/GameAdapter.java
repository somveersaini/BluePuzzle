package com.iblue.BluePuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iblue.BluePuzzle.wordi.board;
import com.iblue.BluePuzzle.wordi.wordlist;


/**
 * Created by samsaini on 10/26/2015.
 */

public class GameAdapter extends BaseAdapter {
    private final static  String TAG = "GameAdater";
    public static String player1 = "Player one";
    public static String player2 = "Player two";
    private Context context;
    private String HIGH_SCORE = "highscore";
    public char input='x';
    public int posx = 0;
    public int posy = 0;
    board b;
    wordlist w;
    int currentScore = 0;
    int currentScore2 = 0;
    int highScore = 0;
    public int COLUMNS;
    TextView pOne;
    TextView pTwo;
    TextView pstats1;
    TextView pstats2;
    TextView highScoreTv;

    //TextView terminal;
    public String logs = "Blue Puzzle\n\n***  LOGS  ***\n";
    int turn = 0; //0 for playerOne and 1 for playerTwo
    SharedPreferences settings;

    public GameAdapter(Context context, int column) {
        this.context = context;
        b = new board();
        w = new wordlist(context);
        COLUMNS = column;
        settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        highScore = getHighScore();
        pOne = (TextView) ((Activity) context).findViewById(R.id.pOneScoreTv);
        pTwo = (TextView) ((Activity) context).findViewById(R.id.pTwoScoreTv);
        pstats1 = (TextView) ((Activity) context).findViewById(R.id.pstats1);
        pstats2 = (TextView) ((Activity) context).findViewById(R.id.pstats2);
        highScoreTv = (TextView) ((Activity) context).findViewById(R.id.highTv);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int hh = height/11;
        int ww = width/3 - 10;
        pOne.setWidth(ww);
        pOne.setHeight(hh);
        pTwo.setWidth(ww);
        pTwo.setHeight(hh);
        highScoreTv.setWidth(ww);
        highScoreTv.setHeight(hh);


        setHighScoreTv();
        setCurrentScoreTv(0);
        setCurrentScoreTv(1);
        setTerminal();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.item2, null);
            TextView text = (TextView) gridView.findViewById(R.id.grid_item2);
                text.setText("");



            text.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    if ((str != null) && (str != "")) {
                        setText(str.toLowerCase(), position);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });

        } else { gridView = (View) convertView; }
        return gridView;
    }
    public void setTurn(){
        turn = 1;
    }


    private void setText(String str, int position) {
        if(str != null && (str.length() == 1)) {
            input = str.charAt(0);
            posx = position/COLUMNS;
            posy = position%COLUMNS;
            input(posx, posy, input);
        }
    }


    //gameplay

    public void input(int positionx, int positiony, char inp){
        String  rows, cols;
        int pRow, pCol;
        int points;
        if(!b.isfull()) {
            b.setChar(positionx, positiony, inp);
            rows = b.getRow(positionx, positiony);
            cols = b.getCol(positionx, positiony);
            pRow = b.getRowPos(positionx, positiony);
            pCol = b.getColPos(positionx, positiony);
            Log.d("strings ", rows + " " + cols);
            points = w.matchRow(rows, pRow) + w.matchRow(cols, pCol);
            addCurrentScore(points, turn);
            setCurrentScoreTv(turn);
            terminalOut();
            nextTurn();
        }

    }

    public void nextTurn(){
        if(turn == 0){ turn = 1; }
        else {  turn = 0;  }
    }
    public void addCurrentScore(int points, int turn){
        if(turn == 0) {
            currentScore += points;
        }
        else {
            currentScore2 += points;
        }
        if(currentScore > highScore){
            highScore = currentScore;
            setHighScoreTv();
        }
        if(currentScore2 > highScore){
            highScore = currentScore2;
            setHighScoreTv();
        }
    }
    public void setHighScoreTv(){
        highScoreTv.setText(" High Score \n" + String.valueOf(highScore));
        YoYo.with(Techniques.Pulse)
                .duration(500)
                .playOn(highScoreTv);

        recordHighScore();
    }
    public void setCurrentScoreTv(int turn){
        if(turn == 0) {
            pOne.setText("  " + player1 + " \n" + String.valueOf(currentScore));
            YoYo.with(Techniques.Pulse)
                    .duration(500)
                    .playOn(pOne);

        }
        else {
            pTwo.setText("  " + player2 + " \n" + String.valueOf(currentScore2));
            YoYo.with(Techniques.Pulse)
                    .duration(500)
                    .playOn(pTwo);

        }
    }

    private void recordHighScore() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(HIGH_SCORE, highScore);
        editor.commit();
    }

    public void setName(String name){
        player1 = name;
        setCurrentScoreTv(0);
    }
    public void setOponentName(String name){
        player2 = name;
        turn = 1;
        setCurrentScoreTv(1);
    }
    public void setTerminal(){
        pstats1.setText("Heyy there!!! Wassup??");
        pstats2.setText("Nuthing!! Lets play Blue puzzle");
    }
    int currentUsedIndex = 0;
    public void terminalOut(){
        String player, stats;
        if(turn == 0){
            player = player1;
        }
        else {
            player = player2;
        }
        logs += "\n  " + player + " made -> ";
        stats = "  " + player + " got ";
        if(currentUsedIndex < w.usedIndex) {
            while (currentUsedIndex < w.usedIndex) {
                logs += w.usedWords[currentUsedIndex] + " ";
                stats += w.usedWords[currentUsedIndex] + " ";
                currentUsedIndex++;
            }
        }else {
           // terminal.append("nuthing ");
            logs += "nuthing ";
            stats += "nuthing ";
        }
        if(turn == 0){
            pstats1.setText(stats);
            YoYo.with(Techniques.FlipInX)
                    .duration(700)
                    .playOn(pstats1);
        }
        else {
            pstats2.setText(stats);
            YoYo.with(Techniques.FlipInX)
                    .duration(700)
                    .playOn(pstats2);
        }
    }
    private int getHighScore() {
        return settings.getInt(HIGH_SCORE, 0);
    }

    @Override
    public int getCount() {
        return COLUMNS*COLUMNS;
    }

    @Override
    public String getItem(int pos) {
        return String.valueOf(b.getChar(pos/COLUMNS,pos%COLUMNS));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}