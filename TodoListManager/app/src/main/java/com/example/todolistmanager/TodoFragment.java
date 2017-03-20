package com.example.todolistmanager;

/**
 * Created by Tomer Patel on 3/18/2017.
 */
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import 	android.app.AlertDialog;

import java.util.ArrayList;

public class TodoFragment extends Fragment{

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
        View pView = inflater.inflate(R.layout.todo_layout, container, false);
        recyclerView = (RecyclerView) pView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null)
        {
            adapter.setmMessages((ArrayList<TodoMessage>)savedInstanceState.getSerializable("adapterList"));
            Log.i("debugging", "restored last messages...");
        }



        return pView;
    }

    public void onMessageSend(String data)
    {
        Log.i("message adder", "message is " + data);
        if(!TextUtils.isEmpty(data))
        {
            adapter.addItem(new TodoMessage(data));
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
            TextView todoMessageTextView;
            TextView hourCreatedTextView;
            CardView cv;
            protected ViewHolder(final CardView cv)
            {
                super(cv);
                this.cv = cv;
                this.todoMessageTextView = (TextView) cv.findViewById(R.id.todo_message_body);
                this.hourCreatedTextView = (TextView) cv.findViewById(R.id.todo_message_create_time);
                cv.setLongClickable(true);
                cv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        int position = getAdapterPosition();
//                        adapter.notifyItemRemoved(position);
//                        adapter.notifyItemRangeChanged(position, adapter.getmMessages().size());
                        AlertDialog dialog = new AlertDialog.Builder(cv.getContext())
                                .setTitle("Delete Task?")
                                .setMessage("Sure you want to delete this task?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int position = getAdapterPosition();
                                        adapter.getmMessages().remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getmMessages().size());

                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                        dialog.show();
                        return true;
                    }
                });


            }
            public void setBackGroundColor(int colorBG){
                this.cv.setCardBackgroundColor(colorBG);
            }
        }

        private ArrayList<TodoMessage> mMessages;

        public Adapter()
        {
            this.mMessages = new ArrayList<>();
        }

        public ArrayList<TodoMessage> getmMessages() {
            return this.mMessages;
        }

        public void setmMessages(ArrayList<TodoMessage> mMessages) {
            this.mMessages = mMessages;
        }

        public void addItem(TodoMessage message)
        {
            this.mMessages.add(message);
            notifyItemInserted(this.mMessages.size() - 1);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_message_item,
                    parent, false);
            //cView.setCardBackgroundColor();
            return new ViewHolder(cView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TodoMessage currentMessage = mMessages.get(position);
            if(position % 2 == 0) {
                holder.setBackGroundColor(Color.RED);
            }
            else{
                holder.setBackGroundColor(Color.BLUE);
            }
            holder.todoMessageTextView.setText(currentMessage.getData());
            holder.hourCreatedTextView.setText(currentMessage.getHourCreated());

        }

    }
}