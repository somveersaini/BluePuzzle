package com.iblue.BluePuzzle.wordi;

/**
 * Created by samsaini on 9/21/2015.
 */
public class board {
    final static String TAG = "MAIN board";
    int NO_OF_ROW = 8;
    int NO_OF_COLUMN = 8;
    char puzz[][] = new char[NO_OF_ROW][NO_OF_COLUMN];
    char empty = ' ';
    public static board instance;


    public board(){
        init();
    }

    //initiallize board puzz
    public void init(){
        for(int i = 0; i < NO_OF_ROW; i++){
            for(int j = 0; j < NO_OF_COLUMN; j++){
                puzz[i][j] = empty;
            }
        }
    }

    //to insert a char in board
    public void setChar(int positionx, int positiony, char input){
        puzz[positionx][positiony] = input;
    }

    //to get a char from board
    public char getChar(int positionx, int positiony){
        return puzz[positionx][positiony];
    }


    //return true if board is full
    public boolean isfull(){
        for(int i = 0; i < NO_OF_ROW; i++){
            for(int j = 0; j < NO_OF_COLUMN; j++){
                if (puzz[i][j] == empty) {
                    return false;
                }
            }
        }
        return true;
    }

    //return no. of empty space {null}  {int}
    public int emptySpaces(){
        int no_of_sapces = 0;
        for(int i = 0; i < NO_OF_ROW; i++){
            for(int j = 0; j < NO_OF_COLUMN; j++){
                if (puzz[i][j] == empty) {
                    no_of_sapces++;
                }
            }
        }
        return no_of_sapces;
    }

    //return down Array {int row , int column}
    public String getDownString(int row, int column){
        String down = "";
        int j = row;
        while(row < NO_OF_ROW){
            if(puzz[row][column] != empty) {
                down += puzz[row][column];
                row++;
            }
            else {
                break;
            }
        }
        return down;
    }

    //return right char Array  {int row, int column}
    public String getRightString(int row, int column){
        String down = "";
        while(column < NO_OF_COLUMN){
            if(puzz[row][column] != empty) {
                down += puzz[row][column];
                column++;
            }
            else {
                break;
            }
        }
        return down;
    }
    //return up char Array  {int row, int column}
    public String getUpString(int row, int column){
        String down = "";
        while(row >= 0){
            if(puzz[row][column] != empty) {
                down = puzz[row][column] + down;
                row--;
            }
            else {
                break;
            }
        }
        return down;
    }
    //return left char Array  {int row, int column}
    public String getLeftString(int row, int column){
        String down = "";
        while(column >= 0){
            if(puzz[row][column] != empty) {
                down = puzz[row][column]+ down;
                column--;
            }
            else {
                break;
            }
        }
        return down;
    }

    public int getRowPos(int row, int column){
        int pos = 0;
        while(column >= 0) {
            if (puzz[row][column] != empty) {
                pos++;
                column--;
            }
            else{
                break;
            }
        }

        return  pos--;
    }
    public int getColPos(int row, int column){
        int pos = 0;
        while(row >= 0) {
            if (puzz[row][column] != empty) {
                pos++;
                row--;
            }
            else{
                break;
            }
        }

        return  pos--;
    }
    public String getRow(int row, int column){
        String str = "";
        String str2 = "";
        str = getLeftString(row, column);
        str2 = getRightString(row, column);
        if(str2.length() > 1){
            str2 = str2.substring(1);
            str = str+str2;
        }
        return str;
    }
    public String getCol(int row, int column){
        String str = "";
        String str2 = "";
        str = getUpString(row, column);
        str2 = getDownString(row, column);
        if(str2.length() > 1){
            str2 = str2.substring(1);
            str = str+str2;
        }
        return str;
    }
}
