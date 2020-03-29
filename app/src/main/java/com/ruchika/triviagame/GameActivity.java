package com.ruchika.triviagame;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    TextView tvQues, tvLife, tvNum;
    int lives = 3;
    Button btnA;
    Button btnB;
    Button btnC;
    Button btnD;
    Button btnSkip;
    ProgressBar pbScore;
    int i = 0;
    int numQuestion = 1;
    ArrayList<Question> list;
    int score = 0;
    ProgressBar pBar;
    CountDownTimer t;
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        tvQues = findViewById(R.id.tvQues);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        btnSkip = findViewById(R.id.btnSkip);
        pBar = findViewById(R.id.pBar);
        pBar.setMax(1000);
        tvLife = findViewById(R.id.life);
        tvNum = findViewById(R.id.tvScore);
        pbScore = findViewById(R.id.pbScore);
        pbScore.setMax(6);
        String result = getIntent().getStringExtra("result");
        list = parseJson(result);
        t = new CountDownTimer(10000, 10) {
            @Override
            public void onTick(long l) {
                pBar.setProgress((int) l / 10);
            }

            @Override
            public void onFinish() {
                pBar.setProgress(0);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        i++;
                        if (i < list.size()) {
                            setQuestion(i);
                            numQuestion++;
                        }
                    }
                }, 500);

            }
        };
        setQuestion(i);
    }

    void setQuestion(int index) {
        Question question = list.get(index);
        tvQues.setText(question.question);
        String stNum = String.valueOf(numQuestion) + "/10";
        tvNum.setText(stNum);
        String type = question.type;
        btnA.setBackgroundColor(Color.rgb(195, 195, 229));
        btnB.setBackgroundColor(Color.rgb(68, 50, 102));
        btnC.setBackgroundColor(Color.rgb(195, 195, 229));
        btnD.setBackgroundColor(Color.rgb(68, 50, 102));
        btnC.setVisibility(View.VISIBLE);
        btnD.setVisibility(View.VISIBLE);
        Button correct = btnA;
        if (type.equals("multiple")) {
            Random r = new Random();
            Integer option = 1 + r.nextInt(4);
            ArrayList<String> incorrectAns = question.incorrectAns;
            String a = incorrectAns.get(0);
            String b = incorrectAns.get(1);
            String c = incorrectAns.get(2);

            if (option == 1) {
                btnA.setText(question.correctAns);
                btnB.setText(a);
                btnC.setText(b);
                btnD.setText(c);
                correct = btnA;
            } else if (option == 2) {
                btnA.setText(a);
                btnB.setText(question.correctAns);
                btnC.setText(b);
                btnD.setText(c);
                correct = btnB;
            } else if (option == 3) {
                btnA.setText(b);
                btnB.setText(a);
                btnC.setText(question.correctAns);
                btnD.setText(c);
                correct = btnC;
            } else {
                btnA.setText(c);
                btnB.setText(a);
                btnC.setText(b);
                btnD.setText(question.correctAns);
                correct = btnD;
            }

        } else if (type.equals("boolean")) {

            if (question.correctAns.equals("True")) correct = btnA;
            else correct = btnB;
            btnA.setText("True");
            btnB.setText("False");
            btnC.setVisibility(View.INVISIBLE);
            btnD.setVisibility(View.INVISIBLE);
        }
        t.start();
        final OnClick listener = new OnClick(correct);
        listener.setPressed(false);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lives > 0 && numQuestion <= 10) {
                    t.cancel();
                    lives--;
                    tvLife.setText(String.valueOf(lives));
                    listener.setPressed(false);
                    i++;
                    if (i < list.size() && numQuestion <= 10) setQuestion(i);
                }
            }
        });

        btnA.setOnClickListener(listener);
        btnB.setOnClickListener(listener);
        if (type.equals("multiple")) {
            btnC.setOnClickListener(listener);
            btnD.setOnClickListener(listener);
        }
    }

    class OnClick implements View.OnClickListener {
        boolean pressed;
        View button;

        OnClick(View button) {
            pressed = false;
            this.button = button;
        }

        void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        @Override
        public void onClick(View view) {
            if (i < list.size() && numQuestion <= 10) {
                final String correctAns = list.get(i).correctAns;
                Handler h = new Handler();

                if (!pressed) {
                    if (!((Button) view).getText().toString().equals(correctAns)) {
                        view.setBackgroundColor(Color.rgb(244, 67, 54));
                        button.setBackgroundColor(Color.rgb(0, 200, 83));

                    } else {
                        view.setBackgroundColor(Color.rgb(0, 200, 83));
                        score++;
                        pbScore.setProgress(score);
                        if (mp != null) mp.release();
                        else mp = MediaPlayer.create(GameActivity.this, R.raw.correct);
                    }
                    t.cancel();
                    pressed = true;
                    numQuestion++;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            if (i < list.size() && numQuestion <= 10) {
                                setQuestion(i);
                            }
                        }
                    }, 2000);
                }
            }
        }
    }

    ArrayList<Question> parseJson(String result) {
        ArrayList<Question> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray questionList = jsonObject.getJSONArray("results");
            int count = 0;
            int num = 0;
            while (count < 15) {
                boolean notLarge = true;
                JSONObject question = questionList.getJSONObject(num);
                String q = question.getString("question");
                q = removeEsc(q);
                String correctAns = question.getString("correct_answer");
                correctAns = removeEsc(correctAns);
                String type = question.getString("type");
                JSONArray incorrectAns = question.getJSONArray("incorrect_answers");
                ArrayList<String> incrtA = new ArrayList<>();
                for (int j = 0; j < incorrectAns.length(); j++) {
                    String s = incorrectAns.getString((j));
                    s = removeEsc(s);
                    incrtA.add(s);
                    if (s.length() > 15) {
                        notLarge = false;
                        break;
                    }
                }
                if (notLarge) {
                    Question newQuestion = new Question(q, correctAns, type, incrtA);
                    list.add(newQuestion);
                    count++;
                }
                num++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    String removeEsc(String str) {
        str = str.replace("&quot;", "\"");
        str = str.replace("&#039;", "'");
        str = str.replace("&amp;", "&");
        str = str.replace("&ldquo;", "\"");
        str = str.replace("&rdquo;", "\"");
        return str;
    }
}
