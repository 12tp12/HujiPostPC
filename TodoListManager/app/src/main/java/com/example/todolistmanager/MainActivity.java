package com.example.todolistmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import 	android.app.AlertDialog;
import 	android.widget.EditText;
import android.util.Log;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Button;
import java.text.DateFormat;
import android.support.v4.app.ActivityCompat;
import 	android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import 	android.support.v4.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TODO_FRAGMENT = "Todo Fragment";
    TodoFragment todoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

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

                final View inflaterView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
               final DatePicker datePicker = (DatePicker) inflaterView.findViewById(R.id.datePicker);
                datePicker.setMinDate(System.currentTimeMillis()-1000);
                final TimePicker timePicker = (TimePicker) inflaterView.findViewById(R.id.timePicker1);


                final EditText taskEditText = (EditText) inflaterView.findViewById(R.id.editTextNewTask);
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add A New Task")
                        .setMessage("What do I have to do now?")
                        .setView(inflaterView)
                        .create();
                dialog.show();
                Button addButton = (Button)inflaterView.findViewById(R.id.AddButton);
                addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        final String timeStr;
                        if(timePicker.getMinute() < 10) {
                            timeStr = String.valueOf(timePicker.getHour()) + ":0" + String.valueOf(timePicker.getMinute());
                        }
                        else{
                            timeStr = String.valueOf(timePicker.getHour()) + ":" + String.valueOf(timePicker.getMinute());

                        }
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;
                        int year = datePicker.getYear();
                        final String dateStr =  String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
                        Log.i("debugging", timeStr);
                        todoFragment.onMessageSend(String.valueOf(taskEditText.getText()),timeStr,dateStr);
                        dialog.dismiss();
                    }
                });
                Button cancelButton = (Button)inflaterView.findViewById(R.id.CancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){

                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}