package com.selfchat.chatwithmyself;


import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HP on 11-Mar-17.
 */

public class Message {
    private String data;
    private String hour;


    public Message(String data)
    {
        this.data = data;
        this.hour = new SimpleDateFormat("HH:mm").format(new Date());
        Log.i("Message info", "Date is " + this.hour);
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
