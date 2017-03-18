package com.example.todolistmanager;

/**
 * Created by Tomer Patel on 3/18/2017.
 */

import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class TodoMessage {

    private String data;
    private String hourCreated;
    //private String todoHour;

    public TodoMessage(String data){
        this.data = data;
        //this.todoHour = todoHour;
        this.hourCreated = new SimpleDateFormat("HH:mm").format(new Date());
        Log.i("Message info", "Date is " + this.hourCreated);

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getHourCreated() {
        return hourCreated;
    }

    public void setHourCreated(String hourCreated) {
        this.hourCreated = hourCreated;
    }

//    public String getTodoHour() {
//        return todoHour;
//    }
//
//    public void setTodoHour(String todoHour) {
//        this.todoHour = todoHour;
//    }


}

