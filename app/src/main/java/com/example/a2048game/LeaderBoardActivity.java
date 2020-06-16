package com.example.a2048game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String APP_NAME = "2048Project";
    private static final String TOP_SCORE_PREF = "TopScore pref";


    SharedPreferences sharedPreferences;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout);
        sharedPreferences = getSharedPreferences(APP_NAME, MODE_PRIVATE);

        String gameMode = Integer.toString(getIntent().getIntExtra("game_mode", 0));

        Long[] leaderBoard = new Long[8];

        leaderBoard[0] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "3" + "3", 0);
        leaderBoard[1] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "4" + "4", 0);
        leaderBoard[2] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "5" + "5", 0);
        leaderBoard[3] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "6" + "6", 0);
        leaderBoard[4] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "4" + "3", 0);
        leaderBoard[5] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "5" + "3", 0);
        leaderBoard[6] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "5" + "4", 0);
        leaderBoard[7] = sharedPreferences.getLong(TOP_SCORE_PREF + gameMode + "6" + "5", 0);

        listView = (ListView)findViewById(R.id.leaderboard_listview);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("3x3" + " : " + Long.toString(leaderBoard[0]));
        arrayList.add("4x4" + " : " + Long.toString(leaderBoard[1]));
        arrayList.add("5x5" + " : " + Long.toString(leaderBoard[2]));
        arrayList.add("6x6" + " : " + Long.toString(leaderBoard[3]));
        arrayList.add("4x3" + " : " + Long.toString(leaderBoard[4]));
        arrayList.add("5x3" + " : " + Long.toString(leaderBoard[5]));
        arrayList.add("5x4" + " : " + Long.toString(leaderBoard[6]));
        arrayList.add("6x5" + " : " + Long.toString(leaderBoard[7]));


        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);


    }
}
