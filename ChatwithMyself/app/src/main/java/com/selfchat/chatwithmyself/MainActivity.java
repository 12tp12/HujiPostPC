package com.selfchat.chatwithmyself;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private static final String CHAT_FRAGMENT = "Chat Fragment";
    ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("debugging", "initializing fragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        chatFragment = (ChatFragment) fragmentManager.findFragmentByTag(CHAT_FRAGMENT);

        if (chatFragment == null)
        {
            chatFragment = new ChatFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Log.i("debugging", "commiting new fragment...");
            transaction.add(R.id.chat_fragment_container, chatFragment);
            transaction.commit();
        }
        else
        {
            Log.i("debugging", "retained new fragment...");
        }
        Log.i("debugging", "setting onclick");
        ImageButton sendBtn = (ImageButton) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("debugging", "calling onMessageSend...");
                EditText editText = (EditText) findViewById(R.id.message_text_box);
                String data = editText.getText().toString();
                chatFragment.onMessageSend(data);
                editText.setText(null);
            }
        });
    }
}
