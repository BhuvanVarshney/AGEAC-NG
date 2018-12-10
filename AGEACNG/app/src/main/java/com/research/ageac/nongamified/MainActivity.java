package com.research.ageac.nongamified;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.research.ageac.nongamified.quizlibrary.QuizCounts;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    View btnTopicArray, btnTopicLinkedList, btnTopicStack, btnTopicQueue, btnTopicTrees, btnTopicBST, btnTopicHeap, btnTopicGraph, btnTopicHashing;

    DBAdapter dbAdapter;
    SharedPreferences sharedPreferences;

    BackUpValuesDTO backUpValuesDTO;

    String username;
    String KEY_COMPLETION = "completion";
    String KEY_TOTAL_ACCURACY = "total_accuracy";

    private Boolean exit = false;

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
        setContentView(R.layout.activity_main);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        dbAdapter = new DBAdapter(this);
        backUpValuesDTO = dbAdapter.getBackUpData();

        username = backUpValuesDTO.getUsername();
        TextView txtvUsername = findViewById(R.id.username);
        txtvUsername.setText("Using as : " + username);

        sharedPreferences = getSharedPreferences("Extras", MODE_PRIVATE);
        boolean containKey = sharedPreferences.contains("exists");
        if (!containKey) {

            Log.d("Values Used from - ", "Backup");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("exists", true);

            editor.putInt(BackUpDBKeys.BACKUPDB_KEY_DAYS_STREAK, backUpValuesDTO.getDaysStreak());

            editor.putString(BackUpDBKeys.BACKUPDB_KEY_LAST_ACTIVE_DATE, backUpValuesDTO.getLastActiveDate());
            editor.commit();
        }

        btnTopicArray = findViewById(R.id.btn_topic_array);
        btnTopicLinkedList = findViewById(R.id.btn_topic_linkedlist);
        btnTopicStack = findViewById(R.id.btn_topic_stack);
        btnTopicQueue = findViewById(R.id.btn_topic_queue);
        btnTopicTrees = findViewById(R.id.btn_topic_tree);
        btnTopicBST = findViewById(R.id.btn_topic_bst);
        btnTopicHeap = findViewById(R.id.btn_topic_heap);
        btnTopicGraph = findViewById(R.id.btn_topic_graph);
        btnTopicHashing = findViewById(R.id.btn_topic_hashing);

        btnTopicArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("array");
            }
        });

        btnTopicLinkedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("linkedlist");
            }
        });

        btnTopicStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("stack");
            }
        });

        btnTopicQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("queue");
            }
        });

        btnTopicTrees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("tree");
            }
        });

        btnTopicBST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("bst");
            }
        });

        btnTopicHeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("heap");
            }
        });

        btnTopicGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("graph");
            }
        });

        btnTopicHashing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIntent("hashing");
            }
        });

        ConnectionChecker connectionChecker = new ConnectionChecker(MainActivity.this);
        boolean isConnectionAvailable = connectionChecker.isInternetConnected();
        if (isConnectionAvailable && lastUpdateTimePassed())
            uploadDB();

    }

    private boolean lastUpdateTimePassed() {
        Date todaysDate = null, lastDate = null;
        String lastDBUploadDate;
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lastDBUploadDate = backUpValuesDTO.getLastDBUploadDateTime();
        try {
            lastDate = sdf.parse(lastDBUploadDate);
            todaysDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int phases = (int) ((todaysDate.getTime() - lastDate.getTime()) / 3600000);
        if (phases > 0)
            return true;
        return false;
    }

    private void initializeIntent(String topicSelected) {
        Intent stagesActivityIntent = new Intent(MainActivity.this, StagesActivity.class);
        stagesActivityIntent.putExtra("topic", topicSelected);
        startActivity(stagesActivityIntent);
    }

    private void uploadDB() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("progress/" + username);
        File currentDB = getApplicationContext().getDatabasePath("AGEACver1DB");
        storageReference.putFile(Uri.fromFile(currentDB))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        updateProgress();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Upload DB Error - ", String.valueOf(e));
                    }
                });
    }

    private void updateProgress() {
        int quizzesCompleted = dbAdapter.getCompletedStagesNumber();
        int questionsAttempted;
        int questionsFailed;
        double completion, accuracy;
        completion = (quizzesCompleted * 100) / (double) QuizCounts.totalQuizzes;
        StageAttemptsDTO stageAttemptsDTO = dbAdapter.getQuizAttempts();
        questionsAttempted = stageAttemptsDTO.getQuestionsAttempted();
        questionsFailed = stageAttemptsDTO.getQuestionsAnsweredWrong();
        if (questionsAttempted == 0)
            accuracy = 0;
        else
            accuracy = ((questionsAttempted - questionsFailed) * 100) / (double) questionsAttempted;

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("normal").document(username);
        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_COMPLETION, completion);
        updates.put(KEY_TOTAL_ACCURACY, accuracy);
        updates.put("updatedOn", FieldValue.serverTimestamp());
        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    dbAdapter.updateLastDbUploadDate();
                }
            }
        });
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
    public void onBackPressed() {
        if (exit) {
            backupValues();
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Backup & Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    private void backupValues() {

        boolean containKey = sharedPreferences.contains("exists");
        if (containKey) {
            ArrayList<BackUpDTO> backUpObjectsList = new ArrayList<>();
            BackUpDTO backUpObject;

            backUpObject = new BackUpDTO();
            backUpObject.setKey(BackUpDBKeys.BACKUPDB_KEY_DAYS_STREAK);
            backUpObject.setValue(String.valueOf(sharedPreferences.getInt(BackUpDBKeys.BACKUPDB_KEY_DAYS_STREAK, 1)));
            backUpObjectsList.add(backUpObject);

            DateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            Date current = new Date();

            backUpObject = new BackUpDTO();
            backUpObject.setKey(BackUpDBKeys.BACKUPDB_KEY_LAST_ACTIVE_DATE);
            backUpObject.setValue(sharedPreferences.getString(BackUpDBKeys.BACKUPDB_KEY_LAST_ACTIVE_DATE, sdfDate.format(current)));
            backUpObjectsList.add(backUpObject);

            dbAdapter.updateValues(backUpObjectsList);
            Log.d("Backup - ", "Success");
        }

    }
}