package com.example.todolistmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import 	android.app.AlertDialog;
import 	android.widget.EditText;
import android.util.Log;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private static final String TODO_FRAGMENT = "Todo Fragment";
    TodoFragment todoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        todoFragment = (TodoFragment) fragmentManager.findFragmentByTag(TODO_FRAGMENT);
        if (todoFragment == null)
        {
            todoFragment = new TodoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Log.i("debugging", "commiting new fragment...");
            transaction.add(R.id.chat_fragment_container, todoFragment, TODO_FRAGMENT);
            transaction.commit();
        }
        else
        {
            Log.i("debugging", "retained new fragment...");
        }
        Log.i("debugging", "setting onclick");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_Button);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText taskEditText = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("What do I have to do now?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                //////

                                Log.i("debugging", "calling onMessageSend...");
                                todoFragment.onMessageSend(task);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }


}
