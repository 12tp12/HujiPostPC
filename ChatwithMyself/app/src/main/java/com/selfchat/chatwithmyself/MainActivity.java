package com.selfchat.chatwithmyself;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton sendBtn = (ImageButton) findViewById(R.id.send_btn);
        toast = Toast.makeText(getApplicationContext(), "Message can't be empty",
                Toast.LENGTH_SHORT);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.message_text_box);
                String data = editText.getText().toString();
                editText.setText(null);
                Log.i("message adder", "message is " + data);
                if (!data.trim().equals(""))
                {
                    adapter.addItem(new Message(data));
                    toast.cancel();
                }
                else
                {
                    toast.show();
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        protected class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView messageTextView;
            TextView hourTextView;
            protected ViewHolder(CardView cv)
            {
                super(cv);
                this.messageTextView = (TextView) cv.findViewById(R.id.message_body);
                this.hourTextView = (TextView) cv.findViewById(R.id.message_time);
            }
        }
        private ArrayList<Message> mMessages;

        public Adapter()
        {
            this.mMessages = new ArrayList<>();
        }

        public void addItem(Message message)
        {
            this.mMessages.add(message);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,
                    parent, false);
            return new ViewHolder(cView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message currentMessage = mMessages.get(position);

            holder.messageTextView.setText(currentMessage.getData());
            holder.hourTextView.setText(currentMessage.getHour());
        }
    }
}
