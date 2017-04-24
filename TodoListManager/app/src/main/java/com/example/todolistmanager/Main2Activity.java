package com.example.todolistmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import 	android.Manifest;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.message;
import static com.example.todolistmanager.MainActivity.i;
import static java.security.AccessController.getContext;

public class Main2Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mLogOutBtn;

    RecycleAdapter adapter;
    ArrayList<TodoMessage> todoList;

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
        try {


            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        //startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        //moveTaskToBack(true);
                        finish();
                    }
                }
            };
            mLogOutBtn = (Button) findViewById(R.id.signOutBtn);
            mLogOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();

                }
            });
            todoList = new ArrayList<>();

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(llm);
            adapter = new RecycleAdapter();
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_Button);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final View inflaterView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
                    final DatePicker datePicker = (DatePicker) inflaterView.findViewById(R.id.datePicker);
                    datePicker.setMinDate(System.currentTimeMillis() - 1000);
                    final TimePicker timePicker = (TimePicker) inflaterView.findViewById(R.id.timePicker1);

                    final EditText taskEditText = (EditText) inflaterView.findViewById(R.id.editTextNewTask);
                    final AlertDialog dialog = new AlertDialog.Builder(Main2Activity.this)
                            .setTitle("Add A New Task")
                            .setMessage("What do I have to do now?")
                            .setView(inflaterView)
                            .create();
                    dialog.show();
                    Button addButton = (Button) inflaterView.findViewById(R.id.AddButton);
                    addButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            final String timeStr;
                            if (timePicker.getMinute() < 10) {
                                timeStr = String.valueOf(timePicker.getHour()) + ":0" + String.valueOf(timePicker.getMinute());
                            } else {
                                timeStr = String.valueOf(timePicker.getHour()) + ":" + String.valueOf(timePicker.getMinute());

                            }
                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth() + 1;
                            int year = datePicker.getYear();
                            final String dateStr = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                            Log.i("debugging", timeStr);

                            final TodoMessage msgObj = new TodoMessage(String.valueOf(taskEditText.getText()), timeStr, dateStr, "");


                            //second section
                            //save it to the firebase db
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            String key = database.getReference("users").child(mAuth.getCurrentUser().getUid()).push().getKey();
                            msgObj.setIdMsg(key);
                            Map<String, Object> childUpdates = new HashMap<>();
                            HashMap<String, String> todo = new HashMap<String, String>();
                            todo.put("data", msgObj.getData());
                            todo.put("hourCreated", msgObj.getHourCreated());
                            todo.put("todoHour", msgObj.getTodoDate());
                            todo.put("todoDate", msgObj.getTodoHour());
                            todo.put("idMsg", msgObj.getIdMsg());

                            childUpdates.put(key, todo);
                            database.getReference("users").child(mAuth.getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        finish();
                                    }
                            todoList.add(msgObj);
                            adapter.notifyItemInserted(todoList.size() - 1);
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    Button cancelButton = (Button) inflaterView.findViewById(R.id.CancelButton);
                    cancelButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        catch(Exception ex){
            String TAG = "sdsd";
            Log.d(TAG, ex.getMessage());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String path = "users/" + mAuth.getCurrentUser().getUid();
            database.getReference(path).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            todoList.clear();

                            Log.w("TodoApp", "getUser:onCancelled " + dataSnapshot.toString());
                            Log.w("TodoApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                TodoMessage todo = data.getValue(TodoMessage.class);
                                todoList.add(todo);
                            }

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
        catch(Exception ex){
            String TAG = "sdsd";
            Log.d(TAG, ex.getMessage());
        }
    }
    private class RecycleAdapter extends RecyclerView.Adapter {


        @Override
        public int getItemCount() {
            return todoList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_message_item, parent, false);
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_message_item,
                    parent, false);
            SimpleItemViewHolder pvh = new SimpleItemViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) holder;
            viewHolder.position = position;

            if(position % 2 == 0) {
                ((SimpleItemViewHolder) holder).setBackGroundColor(Color.parseColor("#58D3F7"));
            }
            else{
                ((SimpleItemViewHolder) holder).setBackGroundColor(Color.parseColor("#F5DA81"));
            }

            TodoMessage todo = todoList.get(position);

            ((SimpleItemViewHolder) holder).todoMessageTextView.setText(todo.getData());
            ((SimpleItemViewHolder) holder).hourCreatedTextView.setText(todo.getHourCreated());
            ((SimpleItemViewHolder) holder).dateTextView.setText(todo.getTodoDate());
            ((SimpleItemViewHolder) holder).hourTextView.setText(todo.getTodoHour());

        }

        public final  class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView todoMessageTextView;
            TextView hourCreatedTextView;
            TextView hourTextView;
            TextView dateTextView;
            CardView cv;
            public int position;
            public SimpleItemViewHolder(final CardView cv) {
                super(cv);
                cv.setOnClickListener(this);
                this.cv = cv;
                this.todoMessageTextView = (TextView) cv.findViewById(R.id.todo_message_body);
                this.hourCreatedTextView = (TextView) cv.findViewById(R.id.todo_message_create_time);
                this.hourTextView = (TextView) cv.findViewById(R.id.dateTodo);
                this.dateTextView = (TextView) cv.findViewById(R.id.timeTodo);

                cv.setLongClickable(true);
                cv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        PopupMenu popup = new PopupMenu(v.getContext(), cv);
                        popup.getMenu().add("Delete");
                        final String msgg = todoMessageTextView.getText().toString();

                        if(msgg.length() == 4) {
                            if (msgg.substring(0, 4).equals("Call")) {
                                popup.getMenu().add("Call");
                            }
                        }
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.popup_men, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getTitle().equals("Delete")) {
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, todoList.size());

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    String path = "users/" + mAuth.getCurrentUser().getUid();
                                    database.getReference(path).child(String.valueOf(todoList.get(position).getIdMsg())).setValue(null);
                                    Toast.makeText(v.getContext(), "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                    todoList.remove(position);

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
                                        if (ActivityCompat.checkSelfPermission(Main2Activity.this.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return true;
                                        }
                                        startActivity(phoneIntent);
                                        //Toast.makeText(Main2Activity.this.getApplicationContext(), "Calling..." + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(Main2Activity.this.getApplicationContext(), "Invalid Phone Number!" + item.getTitle(), Toast.LENGTH_SHORT).show();
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
                this.cv.setBackgroundColor(colorBG);
            }

            @Override
            public void onClick(View view) {

            }
        }
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


