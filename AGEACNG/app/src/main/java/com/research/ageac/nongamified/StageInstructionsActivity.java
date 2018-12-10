package com.research.ageac.nongamified;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.research.ageac.nongamified.quizlibrary.PrerequisitesBaseClass;

import java.text.DecimalFormat;

public class StageInstructionsActivity extends AppCompatActivity {

    String topic;
    int level;
    int stage, maxStages;

    TextView levelNStageNameTxtv, topicNameTxtv, prerequisitesTxtv;
    Button btnStartQuiz;
    ImageView backBtn;

    DBAdapter dbAdapter;
    StageAttemptsDTO stageAttemptsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        setContentView(R.layout.activity_stage_instructions);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        DecimalFormat df = new DecimalFormat("#.00");

        dbAdapter = new DBAdapter(StageInstructionsActivity.this);

        topic = getIntent().getExtras().getString("topic");
        level = getIntent().getExtras().getInt("level");
        stage = getIntent().getExtras().getInt("stage");
        maxStages = getIntent().getExtras().getInt("maxStages");

        stageAttemptsDTO = dbAdapter.getStageAttempts(topic, level, stage);

        topicNameTxtv = findViewById(R.id.txtv_stage_instructions_topic_name);
        levelNStageNameTxtv = findViewById(R.id.txtv_stage_instructions_level_n_stage_name);
        prerequisitesTxtv = findViewById(R.id.prerequisites_txtv);
        String quizName = "com.research.ageac.nongamified.quizlibrary." + topic + ".level" + level + ".Stage" + stage;
        PrerequisitesBaseClass quiz = null;
        try{
            quiz = (PrerequisitesBaseClass) Class.forName(quizName).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        prerequisitesTxtv.setText(quiz.getPrerequisites());

        setTopicText();
        levelNStageNameTxtv.setText("L-" + String.valueOf(level) + "/S-" + String.valueOf(stage));

        backBtn = findViewById(R.id.stage_instructions_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStartQuiz = findViewById(R.id.btn_start_quiz);
        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startQuizIntent;
                if (level == 1) {
                    startQuizIntent = new Intent(StageInstructionsActivity.this, TrueFalseQuizActivity.class);
                } else {
                    startQuizIntent = new Intent(StageInstructionsActivity.this, SingleChoiceQuizActivity.class);
                }
                startQuizIntent.putExtra("topic", topic);
                startQuizIntent.putExtra("level", level);
                startQuizIntent.putExtra("stage", stage);
                startQuizIntent.putExtra("attempts", stageAttemptsDTO.getTotalAttempts());
                startQuizIntent.putExtra("attemptsFailed", stageAttemptsDTO.getFailedAttempts());
                startQuizIntent.putExtra("maxStages", maxStages);
                startActivityForResult(startQuizIntent, 111);
            }
        });
    }

    private void setTopicText() {
        if (topic.equalsIgnoreCase("array"))
            topicNameTxtv.setText("Array");
        if (topic.equalsIgnoreCase("linkedlist"))
            topicNameTxtv.setText("Linked List");
        if (topic.equalsIgnoreCase("stack"))
            topicNameTxtv.setText("Stack");
        if (topic.equalsIgnoreCase("queue"))
            topicNameTxtv.setText("Queue");
        if (topic.equalsIgnoreCase("tree"))
            topicNameTxtv.setText("Tree");
        if (topic.equalsIgnoreCase("bst"))
            topicNameTxtv.setText("BST");
        if (topic.equalsIgnoreCase("heap"))
            topicNameTxtv.setText("Heap");
        if (topic.equalsIgnoreCase("graph"))
            topicNameTxtv.setText("Graph");
        if (topic.equalsIgnoreCase("hashing"))
            topicNameTxtv.setText("Hashing");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent backIntent = new Intent();
        setResult(RESULT_OK, backIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(RESULT_OK, backIntent);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
