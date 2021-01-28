package com.gtsr.gtsr.model;

import java.util.ArrayList;

/**
 * Created by Abhilash on 12/17/2018.
 */

public class RangeLimit {

     ArrayList<String> cArray;
    ArrayList<String> rArray;
    ArrayList<String> limitLineTextArray;


    public ArrayList<String> getcArray() {
        return cArray;
    }

    public void setcArray(ArrayList<String> cArray) {
        this.cArray = cArray;
    }

    public ArrayList<String> getrArray() {
        return rArray;
    }

    public void setrArray(ArrayList<String> rArray) {
        this.rArray = rArray;
    }

    public ArrayList<String> getLimitLineTextArray() {
        return limitLineTextArray;
    }

    public void setLimitLineTextArray(ArrayList<String> limitLineTextArray) {
        this.limitLineTextArray = limitLineTextArray;
    }

}
