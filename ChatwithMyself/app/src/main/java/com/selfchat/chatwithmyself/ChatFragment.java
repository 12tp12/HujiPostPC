package com.selfchat.chatwithmyself;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HP on 13-Mar-17.
 */

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Toast toast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getContext(),"Message can't be empty", Toast.LENGTH_SHORT);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("debugging", "in onSaveInstanceState");

        outState.putSerializable("adapterList", adapter.getmMessages());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.chat_layout, container, false);
        recyclerView = (RecyclerView) pView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null)
        {
            adapter.setmMessages((ArrayList<Message>)savedInstanceState.getSerializable("adapterList"));
            Log.i("debugging", "restored last messages...");
        }
        return pView;
    }

    public void onMessageSend(String data)
    {
        Log.i("message adder", "message is " + data);
        if(!TextUtils.isEmpty(data))
        {
            adapter.addItem(new Message(data));
            toast.cancel();
        }
        else
        {
            toast.show();
        }
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

        public ArrayList<Message> getmMessages() {
            return this.mMessages;
        }

        public void setmMessages(ArrayList<Message> mMessages) {
            this.mMessages = mMessages;
            notifyDataSetChanged();
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
