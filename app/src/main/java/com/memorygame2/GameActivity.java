package com.memorygame2;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "MyData";
    String intName = "MyString";
    int defaultInt =0;
    int hiScore;

    Animation wobble;

    private SoundPool soundPool;

    // Sound Variable
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    // for out Ui
    TextView txtScore;
    TextView txtDifficulty;
    TextView txtWatchGo;

    // button
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btnReplay;
    Button btnBack;

    // some variable
    int difficultyLevel = 3;
    int[] sequenceToCopy = new int[100];

    private Handler myHandler;

    boolean playSequence = false;

    int elementToPlay = 0;

    // player answer
    int playerResponses;
    int playerScore;
    boolean isResponding;

    boolean waitLastButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        prefs = getSharedPreferences(dataName,MODE_PRIVATE);
        editor = prefs.edit();
        hiScore = prefs.getInt(intName,defaultInt);

        wobble = AnimationUtils.loadAnimation(this,R.anim.wobble);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("jump4.wav");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("blip_select5.wav");
            sample2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("pickup_coin.wav");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("powerup3.wav");
            sample4 = soundPool.load(descriptor, 0);

        } catch (Exception e) {
            Log.i("error", "exception");
        }

        txtScore = findViewById(R.id.txtScore);
        txtScore.setText("Score:" + playerScore);

        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtDifficulty.setText("Level:" + difficultyLevel);

        txtWatchGo = findViewById(R.id.txtWatchGo);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        btnReplay = findViewById(R.id.btnReplay);
        btnBack = findViewById(R.id.btnBack);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        waitLastButton = false;

        myHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                if (playSequence) {
//
//                }
//                block:
                if (playSequence) {
//                    btn1.setVisibility(View.VISIBLE);
//                    btn2.setVisibility(View.VISIBLE);
//                    btn3.setVisibility(View.VISIBLE);
//                    btn4.setVisibility(View.VISIBLE);

                    switch (sequenceToCopy[elementToPlay]) {
                        case 1:
//                            btn1.setVisibility(View.INVISIBLE);
                            btn1.startAnimation(wobble);
                            soundPool.play(sample1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
//                            btn2.setVisibility(View.INVISIBLE);
                            btn2.startAnimation(wobble);
                            soundPool.play(sample2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
//                            btn3.setVisibility(View.INVISIBLE);
                            btn3.startAnimation(wobble);
                            soundPool.play(sample3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
//                            btn4.setVisibility(View.INVISIBLE);
                            btn4.startAnimation(wobble);
                            soundPool.play(sample4, 1, 1, 0, 0, 1);
                            break;
                    }
                    elementToPlay++;

                    if (elementToPlay == difficultyLevel && !waitLastButton) {
                        waitLastButton = true;
                    }else if(elementToPlay >= difficultyLevel) {
                        waitLastButton = false;
                        sequenceFinished();
                    }
                }
                myHandler.sendEmptyMessageDelayed(0, 900);

            }
        };
        myHandler.sendEmptyMessage(0);

    }

    public void createSequence() {
        Random randInt = new Random();
        int ourRandom;
        for (int i = 0; i < difficultyLevel; i++) {
            ourRandom = randInt.nextInt(4);
            ourRandom++;
            sequenceToCopy[i] = ourRandom;
        }
    }

    public void playASequence() {
        createSequence();
        isResponding = false;
        elementToPlay = 0;
        playerResponses = 0;
        txtWatchGo.setText("WATCH!");
        playSequence = true;
    }

    public void sequenceFinished() {
        playSequence = false;

//        btn1.setVisibility(View.VISIBLE);
//        btn2.setVisibility(View.VISIBLE);
//        btn3.setVisibility(View.VISIBLE);
//        btn4.setVisibility(View.VISIBLE);

        txtWatchGo.setText("GO!");
        isResponding = true;
    }

    public void checkElement(int thisElement) {
        if (isResponding) {
            playerResponses++;
            if (sequenceToCopy[playerResponses - 1] == thisElement) {
                //correct
                playerScore = playerScore + ((thisElement + 1) * 2);
                txtScore.setText("Score:" + playerScore);
                if (playerResponses == difficultyLevel) {
                    // got the whole sequence
                    isResponding = false;
                    difficultyLevel++;
                    txtDifficulty.setText("Level:" + difficultyLevel);
                    playASequence();
                }
            } else {
                txtWatchGo.setText("FAILED!");
                isResponding = false;

                if(playerScore > hiScore){
                    hiScore = playerScore;
                    editor.putInt(intName,hiScore);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"New Hi-score",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!playSequence) {
            switch (v.getId()) {
                case R.id.btn1:
                    soundPool.play(sample1, 1, 1, 0, 0, 1);
                    checkElement(1);
                    break;
                case R.id.btn2:
                    soundPool.play(sample2, 1, 1, 0, 0, 1);
                    checkElement(2);
                    break;
                case R.id.btn3:
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    checkElement(3);
                    break;
                case R.id.btn4:
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                    checkElement(4);
                    break;
                case R.id.btnReplay:
                    difficultyLevel = 3;
                    playerScore = 0;
                    txtScore.setText("Score:" + playerScore);
                    playASequence();
                    break;
                case R.id.btnBack:
                    super.onBackPressed();
                    break;
            }
        }
    }
}
