package com.research.ageac.nongamified;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.research.ageac.nongamified.quizlibrary.MCQBaseClass;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleChoiceQuizActivity extends AppCompatActivity {

    private TextView questionView;
    private Button btnChoice1;
    private Button btnChoice2;
    private Button btnChoice3;
    private Button btnChoice4;
    private ImageView btnExpandedQuestionView;
    ScrollView scrollView;

    private TextView topicNameTxtv, levelNStageNameTxtv;
    private TextView questionsCountTxtv;

    private ImageView backButton;

    private int answer;
    private int score = 0;
    private int questionNumber = 0;
    private int quesCount = 0;

    DBAdapter dbAdapter;

    MCQBaseClass quiz;

    int attempts, failedAttempts;
    int level, stage, maxStages;
    String topic;

    Intent backIntent;

    AppUsage appUsage, quizStart;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        setContentView(R.layout.activity_single_choice_quiz);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        appUsage = new AppUsage();

        attempts = getIntent().getExtras().getInt("attempts");
        failedAttempts = getIntent().getExtras().getInt("attemptsFailed");

        topic = getIntent().getExtras().getString("topic");
        level = getIntent().getExtras().getInt("level");
        stage = getIntent().getExtras().getInt("stage");
        maxStages = getIntent().getExtras().getInt("maxStages");

        questionView = findViewById(R.id.single_choice_quiz_question);
        btnChoice1 = findViewById(R.id.single_choice_quiz_choice1);
        btnChoice2 = findViewById(R.id.single_choice_quiz_choice2);
        btnChoice3 = findViewById(R.id.single_choice_quiz_choice3);
        btnChoice4 = findViewById(R.id.single_choice_quiz_choice4);

        scrollView = findViewById(R.id.question_scrollview);
        btnExpandedQuestionView = findViewById(R.id.question_dialog_opener);

        topicNameTxtv = findViewById(R.id.txtv_single_choice_quiz_topic_name);
        setTopicText();

        levelNStageNameTxtv = findViewById(R.id.txtv_single_choice_quiz_level_n_stage_name);
        levelNStageNameTxtv.setText("L-" + level + "/S-" + stage);

        questionsCountTxtv = findViewById(R.id.single_choice_quiz_question_count);

        dbAdapter = new DBAdapter(SingleChoiceQuizActivity.this);

        String quizName = "com.research.ageac.nongamified.quizlibrary." + topic + ".level" + level + ".Stage" + stage;

        try {
            quiz = (MCQBaseClass) Class.forName(quizName).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        backButton = findViewById(R.id.single_choice_quiz_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnExpandedQuestionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpandedQuestionViewDialog();
            }
        });

        startQuiz();


        btnChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == 0) {
                    score = score + 1;
                    updateQuestion();
                } else {
                    showFailureDialog();
                }
            }
        });

        btnChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == 1) {
                    score = score + 1;
                    updateQuestion();
                } else {
                    showFailureDialog();
                }
            }
        });

        btnChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == 2) {
                    score = score + 1;
                    updateQuestion();
                } else {
                    showFailureDialog();
                }
            }
        });

        btnChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer == 3) {
                    score = score + 1;
                    updateQuestion();
                } else {
                    showFailureDialog();
                }
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


    private void startQuiz() {
        quizStart = new AppUsage();
        quizStart.setDateTimeActivityStart(new Date());
        questionNumber = 0;
        score = 0;
        attempts++;
        quesCount = quiz.getQuestionsCount();
        updateQuestion();
    }

    private void updateQuestion() {
        if (questionNumber < quesCount) {
            scrollView.scrollTo(0,0);
            questionView.setText(quiz.getQuestion(questionNumber));
            btnChoice1.setText(quiz.getChoice1(questionNumber));
            btnChoice2.setText(quiz.getChoice2(questionNumber));
            btnChoice3.setText(quiz.getChoice3(questionNumber));
            btnChoice4.setText(quiz.getChoice4(questionNumber));

            setScrollbarVisibility();

            answer = quiz.getCorrectAnswer(questionNumber);
            questionNumber++;
            questionsCountTxtv.setText("" + questionNumber + "/" + quesCount);
        } else {
            showSuccessDialog();
        }
    }

    private void setScrollbarVisibility() {
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int scrollViewHeight = scrollView.getHeight();
                int childHeight = scrollView.getChildAt(0).getHeight();
                if(childHeight<=scrollViewHeight)
                {
                    btnExpandedQuestionView.setVisibility(View.GONE);
                }
                else
                    btnExpandedQuestionView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showExpandedQuestionViewDialog() {
        AlertDialog.Builder expandedQuestionViewDialogBuilder = new AlertDialog.Builder(SingleChoiceQuizActivity.this);
        View view = LayoutInflater.from(SingleChoiceQuizActivity.this).inflate(R.layout.question_expanded_view_layout, null);

        TextView question = view.findViewById(R.id.single_choice_quiz_question_expanded_view);
        question.setText(quiz.getQuestion(questionNumber - 1));

        expandedQuestionViewDialogBuilder.setView(view);
        final AlertDialog expandedQuestionViewDialog = expandedQuestionViewDialogBuilder.create();
        expandedQuestionViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        expandedQuestionViewDialog.setCanceledOnTouchOutside(false);
        expandedQuestionViewDialog.show();
    }

    private void showSuccessDialog() {

        addEntryInStatsDB("Success");

        AlertDialog.Builder successDialogBuilder = new AlertDialog.Builder(SingleChoiceQuizActivity.this);
        View view = LayoutInflater.from(SingleChoiceQuizActivity.this).inflate(R.layout.stage_success_dialog_layout, null);

        Button btnHome = view.findViewById(R.id.btn_stage_success_return_home);
        Button btnRetry = view.findViewById(R.id.btn_stage_success_try_again);
        Button btnNextStage = view.findViewById(R.id.btn_stage_success_next_stage);

        dbAdapter.updateAttempts(topic, level, stage, attempts, quesCount);
        if (attempts - failedAttempts == 1) {
            dbAdapter.updateStageStatus(topic, level, stage);
        }

        if (stage == maxStages) {
            btnNextStage.setEnabled(false);
            btnNextStage.setClickable(false);
            btnNextStage.setVisibility(View.GONE);
        }

        successDialogBuilder.setView(view);
        final AlertDialog successDialog = successDialogBuilder.create();
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.show();

        successDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                successDialog.dismiss();
                backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                SingleChoiceQuizActivity.this.finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                Intent intent = new Intent(SingleChoiceQuizActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                startQuiz();
            }
        });

        btnNextStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                Intent stageContentViewIntent = new Intent(SingleChoiceQuizActivity.this, StageInstructionsActivity.class);
                stageContentViewIntent.putExtra("topic", topic);
                stageContentViewIntent.putExtra("level", level);
                stageContentViewIntent.putExtra("stage", stage + 1);
                stageContentViewIntent.putExtra("maxStages", maxStages);
                startActivityForResult(stageContentViewIntent, 111);
            }
        });

    }

    private void showFailureDialog() {
        addEntryInStatsDB("Failure");

        AlertDialog.Builder failureDialogBuilder = new AlertDialog.Builder(SingleChoiceQuizActivity.this);
        View view = LayoutInflater.from(SingleChoiceQuizActivity.this).inflate(R.layout.stage_failure_dialog_layout, null);

        Button btnHome = view.findViewById(R.id.btn_stage_failure_return_home);
        Button btnRetry = view.findViewById(R.id.btn_stage_failure_try_again);

        dbAdapter.updateAttempts(topic, level, stage, attempts, quesCount - questionNumber + 1);
        ++failedAttempts;
        dbAdapter.increaseFailedAttempts(topic, level, stage);
        dbAdapter.increaseWrongAnswersCount(topic, level, stage);

        failureDialogBuilder.setView(view);
        final AlertDialog failureDialog = failureDialogBuilder.create();
        failureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failureDialog.setCanceledOnTouchOutside(false);
        failureDialog.show();

        failureDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                failureDialog.dismiss();
                backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                SingleChoiceQuizActivity.this.finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failureDialog.dismiss();
                Intent intent = new Intent(SingleChoiceQuizActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failureDialog.dismiss();
                startQuiz();
            }
        });
    }

    private void addEntryInStatsDB(String result) {
        DateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        Date endDate = new Date();
        Date startDate;
        startDate = quizStart.getDateTimeActivityStart();
        long diff = quizStart.getTimeDifference(startDate, endDate);
        dbAdapter.insertQuizStatus(topic, level, stage, sdfDate.format(startDate), sdfTime.format(startDate), sdfDate.format(endDate), sdfTime.format(endDate), diff, result);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleChoiceQuizActivity.this);
        builder.setMessage("You will lose all your progress.\nAre you sure you still want to quit this quiz now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        addEntryInStatsDB("Left");

                        dbAdapter.updateAttempts(topic, level, stage, attempts, questionNumber);
                        dbAdapter.increaseWrongAnswersCount(topic, level, stage);
                        dbAdapter.increaseFailedAttempts(topic, level, stage);
                        backIntent = new Intent();
                        setResult(RESULT_OK, backIntent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("End Quiz?");
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                finish();
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        Date startDate = new Date();
        appUsage.setDateTimeActivityStart(startDate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        Date endDate = new Date();
        Date startDate;
        startDate = appUsage.getDateTimeActivityStart();
        long diff = appUsage.getTimeDifference(startDate, endDate);
        if (diff > 5) {
            Log.e("On Stop - ", "SCQ Activity");
            new DaysStreakResolver().resolveDaysStreak(this.getApplicationContext());
            if (sdfDate.format(startDate).equalsIgnoreCase(sdfDate.format(endDate))) {
                dbAdapter.addProgressEntry(sdfDate.format(startDate), sdfTime.format(startDate), sdfDate.format(endDate), sdfTime.format(endDate), diff, "SCQA");
            } else {
                long postDiff = 0;
                try {
                    postDiff = appUsage.getTimeDifference(sdfDate.parse(sdfDate.format(endDate) + " 00:00:00"), endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dbAdapter.addProgressEntry(sdfDate.format(startDate), sdfTime.format(startDate), sdfDate.format(endDate), "23:59:59", diff - postDiff, "SCQA");
                dbAdapter.addProgressEntry(sdfDate.format(endDate), "00:00:00", sdfDate.format(endDate), sdfTime.format(endDate), postDiff, "SCQA");
            }
        }
    }

}