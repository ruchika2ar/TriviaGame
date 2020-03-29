package com.ruchika.triviagame;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ruchi on 14-03-2018.
 */

public class Question {
    String question;
    String correctAns;
    String type;
    ArrayList<String> incorrectAns;

    Question(String question, String correctAns,String type ,ArrayList<String> incorrectAns) {
        this.question = question;
        this.correctAns = correctAns;
        this.type = type;
        this.incorrectAns = incorrectAns;
    }
}
