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
    private String todoHour;
    private String todoDate;
    private String idMsg;

    public TodoMessage(){
        this.data = "";
        this.todoHour = "";
        this.todoDate = "";
        this.hourCreated = "";

    }

    public TodoMessage(String data, String todoHour, String todoDate, String idMsg){
        this.data = data;
        this.todoHour = todoHour;
        this.todoDate = todoDate;
        this.hourCreated = new SimpleDateFormat("HH:mm").format(new Date());
        this.idMsg = idMsg;
        Log.i("Message info", "Date is " + this.hourCreated);

    }

    public String getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(String idMsg) {
        this.idMsg = idMsg;
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

    public String getTodoHour() {
        return todoHour;
    }

    public void setTodoHour(String todoHour) {
        this.todoHour = todoHour;
    }

    public String getTodoDate() {
        return todoDate;
    }

    public void setTodoDate(String todoDate) {
        this.todoDate = todoDate;
    }
}

