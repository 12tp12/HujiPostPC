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
import android.widget.Button;
import java.util.ArrayList;
import android.content.Intent;
import android.net.Uri;
import android.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.ActivityCompat;
import 	android.Manifest;
import android.content.pm.PackageManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import 	java.util.regex.Pattern;
import java.util.regex.Matcher;

import static android.R.attr.data;
import static android.R.attr.id;

public class TodoFragment extends Fragment{

    private RecyclerView recyclerView;
    private Adapter adapter;
    private Toast toast;
    private FirebaseAuth mAuth =  FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final String TAG = "dfdfdf";
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
        View pView = null;
        try {
            pView = inflater.inflate(R.layout.todo_layout, container, false);
            recyclerView = (RecyclerView) pView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new Adapter();
            recyclerView.setAdapter(adapter);
            if (savedInstanceState != null) {
                adapter.setmMessages((ArrayList<TodoMessage>) savedInstanceState.getSerializable("adapterList"));
                Log.i("debugging", "restored last messages...");
            }
        }
        catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }

        return pView;
    }

    public void onMessageSend(TodoMessage msg)
    {
        Log.i("message adder", "message is " + msg.getData());
        if(!TextUtils.isEmpty(msg.getData()))
        {
            adapter.addItem(msg);
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
            TextView hourTextView;
            TextView dateTextView;
            CardView cv;
            //final String msg;


            protected ViewHolder(final CardView cv)
            {
                super(cv);
                this.cv = cv;
                this.todoMessageTextView = (TextView) cv.findViewById(R.id.todo_message_body);
                //this.msg = this.todoMessageTextView.toString();
                this.hourCreatedTextView = (TextView) cv.findViewById(R.id.todo_message_create_time);
                this.hourTextView = (TextView) cv.findViewById(R.id.dateTodo);
                this.dateTextView = (TextView) cv.findViewById(R.id.timeTodo);
//
                cv.setLongClickable(true);
                cv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(getContext(), cv);
                        popup.getMenu().add("Delete");
                        final String msgg = todoMessageTextView.getText().toString();

                        if(msgg.substring(0,4).equals("Call")){
                            popup.getMenu().add("Call");
                        }
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.popup_men, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getTitle().equals("Delete")) {
                                    int position = getAdapterPosition();
                                    adapter.getmMessages().remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getmMessages().size());
                                    Toast.makeText(getContext(), "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child( String.valueOf(adapter.mMessages.get(position).getIdMsg())).setValue(null);

                                }
                                else {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    Pattern pattern = Pattern.compile("Call\\s([0-9-()]+)");
                                    Matcher matcher = pattern.matcher(msgg);
                                    if (matcher.find()) {
                                        //System.out.println(matcher.group(0));
                                        Log.i("debuggingdd", matcher.group(1).toString());
                                        String  tel = "tel:"+matcher.group(1).toString();
                                        phoneIntent.setData(Uri.parse(tel));
                                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return true;
                                        }
                                        startActivity(phoneIntent);
                                        Toast.makeText(getContext(), "Calling..." + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Invalid Phone Number!" + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                return true;
                            }
                        });

                        popup.show();
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
                holder.setBackGroundColor(Color.parseColor("#58D3F7"));
            }
            else{
                holder.setBackGroundColor(Color.parseColor("#F5DA81"));
            }
            holder.todoMessageTextView.setText(currentMessage.getData());
            holder.hourCreatedTextView.setText(currentMessage.getHourCreated());
            holder.dateTextView.setText(currentMessage.getTodoDate());
            holder.hourTextView.setText(currentMessage.getTodoHour());

        }

    }
}