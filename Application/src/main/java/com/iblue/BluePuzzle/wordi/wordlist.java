package com.iblue.BluePuzzle.wordi;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by samsaini on 10/22/2015.
 */

public class wordlist {
    final static String tag = "Wordlist";
    public String[] WORD_LIST;
    public String[] usedWords;
    AssetManager mngr;
    public int usedIndex;
    Context mcontext;
    public wordlist(Context myContext) {
        mcontext = myContext;
        mngr = mcontext.getAssets();
        init();
        setMaxUseable();
    }
    void init(){
        try {
            InputStream is = mngr.open("words.txt");
            int size = is.available();
            WORD_LIST = new String[size];
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            int i = 0;
            while ((line = r.readLine()) != null) {
                if(line.length() < 8) {
                    WORD_LIST[i] = line;
                    i++;
                }
            }
            is.close();
        }
        catch (IOException ex) {
            return;
        }
    }
    //to set used words
    void setMaxUseable(){
        int MAX = 100;
        usedWords = new String[MAX];
        usedIndex = 0;
    }

    public int matchRow(String input, int pos){
        int points = 0;
        String input2 = input;
        int pos2 = pos;
        for(int i = 0; i < pos; i++){
            input2 = input;
            int k = input2.length();
            Log.d(tag, "pos2 at : " + pos2);
            Log.d(tag, "substring : " + input2);
            for(int ii = k; ii >= pos2; ii--){
                if (Arrays.asList(WORD_LIST).contains(input2) && (!(Arrays.asList(usedWords).contains(input2)))) {
                    Log.d(tag, "found : " + input2);
                    usedWords[usedIndex] = input2;
                    usedIndex++;
                    points += input2.length();
                }
                input2 = input2.substring(0,ii);
            }
pos2--;
            input = input.substring(1);
        }
        return points;
    }
}
