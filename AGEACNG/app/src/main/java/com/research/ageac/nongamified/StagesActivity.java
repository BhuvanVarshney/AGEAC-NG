package com.research.ageac.nongamified;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StagesActivity extends AppCompatActivity {

    ExpandableHeightGridView level1Grid, level2Grid, level3Grid;
    ArrayList<StageContentDTO> level1StageList = new ArrayList<StageContentDTO>(), level2StageList = new ArrayList<StageContentDTO>(), level3StageList = new ArrayList<StageContentDTO>();
    StageAdapter level1GridAdaptor, level2GridAdaptor, level3GridAdaptor;

    DBAdapter dbAdapter;

    String topic;
    TextView topicNameTxtv;
    ImageView backButton;

    int levelSelected = 0;

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
        setContentView(R.layout.activity_stages);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        topic = getIntent().getExtras().getString("topic");

        topicNameTxtv = findViewById(R.id.txtv_topic_name);
        setTopicText();

        backButton = findViewById(R.id.stage_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        level1Grid = findViewById(R.id.stage_grid_level_1);
        level2Grid = findViewById(R.id.stage_grid_level_2);
        level3Grid = findViewById(R.id.stage_grid_level_3);

        dbAdapter = new DBAdapter(StagesActivity.this);

        level1StageList = dbAdapter.getStagesStatus(topic, 1);
        level2StageList = dbAdapter.getStagesStatus(topic, 2);
        level3StageList = dbAdapter.getStagesStatus(topic, 3);

        level1GridAdaptor = new StageAdapter(this, level1StageList);
        level2GridAdaptor = new StageAdapter(this, level2StageList);
        level3GridAdaptor = new StageAdapter(this, level3StageList);

        level1Grid.setAdapter(level1GridAdaptor);
        level2Grid.setAdapter(level2GridAdaptor);
        level3Grid.setAdapter(level3GridAdaptor);

        level1Grid.setExpanded(true);
        level2Grid.setExpanded(true);
        level3Grid.setExpanded(true);

        level1Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    levelSelected = 1;
                    Intent stageContentViewIntent = new Intent(StagesActivity.this, StageInstructionsActivity.class);
                    stageContentViewIntent.putExtra("topic", topic);
                    stageContentViewIntent.putExtra("level", 1);
                    stageContentViewIntent.putExtra("stage", level1StageList.get(position).getStageNo());
                    stageContentViewIntent.putExtra("maxStages", level1StageList.size());
                    startActivityForResult(stageContentViewIntent, 111);
            }
        });

        level2Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    levelSelected = 2;
                    Intent stageContentViewIntent = new Intent(StagesActivity.this, StageInstructionsActivity.class);
                    stageContentViewIntent.putExtra("topic", topic);
                    stageContentViewIntent.putExtra("level", 2);
                    stageContentViewIntent.putExtra("stage", level2StageList.get(position).getStageNo());
                    stageContentViewIntent.putExtra("maxStages", level2StageList.size());
                    startActivityForResult(stageContentViewIntent, 222);
            }
        });

        level3Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    levelSelected = 3;
                    Intent stageContentViewIntent = new Intent(StagesActivity.this, StageInstructionsActivity.class);
                    stageContentViewIntent.putExtra("topic", topic);
                    stageContentViewIntent.putExtra("level", 3);
                    stageContentViewIntent.putExtra("stage", level3StageList.get(position).getStageNo());
                    stageContentViewIntent.putExtra("maxStages", level3StageList.size());
                    startActivityForResult(stageContentViewIntent, 333);
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

        if (levelSelected == 1) {
            level1StageList = dbAdapter.getStagesStatus(topic, 1);
            level1GridAdaptor = new StageAdapter(this, level1StageList);
            level1Grid.setAdapter(level1GridAdaptor);
            level1Grid.setExpanded(true);
            level2Grid.setExpanded(true);
            level3Grid.setExpanded(true);
        } else if (levelSelected == 2) {
            level2StageList = dbAdapter.getStagesStatus(topic, 2);
            level2GridAdaptor = new StageAdapter(this, level2StageList);
            level2Grid.setAdapter(level2GridAdaptor);
            level1Grid.setExpanded(true);
            level2Grid.setExpanded(true);
            level3Grid.setExpanded(true);
        } else if (levelSelected == 3) {
            level3StageList = dbAdapter.getStagesStatus(topic, 3);
            level3GridAdaptor = new StageAdapter(this, level3StageList);
            level3Grid.setAdapter(level3GridAdaptor);
            level1Grid.setExpanded(true);
            level2Grid.setExpanded(true);
            level3Grid.setExpanded(true);
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
}
