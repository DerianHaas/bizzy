package com.stuff.bizzy.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.Models.Group;
import com.stuff.bizzy.Models.GroupComparator;
import com.stuff.bizzy.Models.User;
import com.stuff.bizzy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class BuildingActivity extends AppCompatActivity {

    private String buildingName;
    private List<Group> groupList;
    private RecyclerView groupView;
    private GroupListAdapter adapter;
    private DatabaseReference ref;
    private DatabaseReference inGroupRef;

    private String currentGroup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

//        Get building name from intent
        buildingName = getIntent().getStringExtra("building");
        TextView t = (TextView)findViewById(R.id.nameLabel);
        t.setText(buildingName);


        groupList = new ArrayList<>();
        groupView = (RecyclerView) findViewById(R.id.list);
        groupView.setHasFixedSize(true);
        adapter = new GroupListAdapter(this, groupList);
        groupView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupView.setLayoutManager(llm);

        ref = Database.getReference("groups");
        if (ref != null) {
//            Get list of groups currently in this building
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> data = dataSnapshot.getValue(t);
                    Group group = new Group(data.get("location").toString(),data.get("name").toString(),data.get("details").toString());
                    Map<String, Boolean> users = (Map<String, Boolean>) data.get("users");
                    group.setUsers(users == null ? new ArrayList<String>() : new ArrayList<>(users.keySet()));
                    group.setUid(dataSnapshot.getKey());
                    if (group.getLocation().equalsIgnoreCase(buildingName)) {
                        Log.d("GroupList", "Found group "+group.getName());
                        groupList.add(group);
                        Collections.sort(groupList, new GroupComparator(GroupComparator.Method.GROUP_SIZE));
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> data = dataSnapshot.getValue(t);
                    Group group = new Group(data.get("location").toString(),data.get("name").toString(),data.get("details").toString());
                    Map<String, Boolean> users = (Map<String, Boolean>) data.get("users");
                    group.setUsers(users == null ? new ArrayList<String>() : new ArrayList<>(users.keySet()));
                    group.setUid(dataSnapshot.getKey());
                    if (group.getLocation().equalsIgnoreCase(buildingName)) {
                            groupList.remove(group);
                            groupList.add(group);
                            Collections.sort(groupList, new GroupComparator(GroupComparator.Method.GROUP_SIZE));
                            adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (groupList.remove(dataSnapshot.getValue(Group.class))) {
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        inGroupRef = Database.getReference("users/"+Database.currentUser.getUid()+"/currentGroup");
        inGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentGroup = dataSnapshot.getValue(String.class);
                Log.d("CurrentGroup", "Current Group: "+ currentGroup);
                findViewById(R.id.leaveGroup).setVisibility(currentGroup.isEmpty() ? View.GONE : View.VISIBLE);
                findViewById(R.id.createGroup).setVisibility(currentGroup.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyItemRangeChanged(0, adapter.groups.size(), Collections.singletonList(currentGroup));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        EditText filter = (EditText) findViewById(R.id.searchBar);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Group> filtered = new ArrayList<Group>();
                for (Group g : groupList) {
                    if (g.getName().contains(s)) {
                        filtered.add(g);
                    }
                }
                adapter = new GroupListAdapter(getApplicationContext(), filtered);
                groupView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboardFrom(getApplicationContext(), v);
                }
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.sortingSpinner);
        ArrayAdapter<CharSequence> spinadapter = ArrayAdapter.createFromResource(this, R.array.group_sort_option,
                android.R.layout.simple_spinner_item);
        spinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Collections.sort(groupList, new GroupComparator(GroupComparator.Method.values()[position]));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //region Keyboard Hiding Methods
    /**
     * Hides keyboard from activity
     * @param activity the activity called from
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hides the keyboard from the current view
     * @param context the current context
     * @param view the current view
     */
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //endregion

    /**
     * Joins the specified group if able
     * @param g the group to join
     */
    private void joinGroup(final Group g) {
        final FirebaseUser currentUser = Database.currentUser;
        if (currentGroup.isEmpty()) {
            ref.child(g.getUid()).child("users/"+currentUser.getUid()).setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully joined group", Toast.LENGTH_SHORT).show();
                                inGroupRef.setValue(g.getUid());
                                g.addToGroup(new User(currentUser));
                                adapter.notifyDataSetChanged();
                                //TODO Send user to group chat
                            } else {
                                Toast.makeText(getApplicationContext(), "There was an error joining a group.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You are already in a group.", Toast.LENGTH_LONG).show();
        }
    }

    public void onLeaveGroupClicked(View v) {
        if (!currentGroup.isEmpty()) {
            ref.child(currentGroup+"/users/"+Database.currentUser.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    inGroupRef.setValue("");

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "You are not currently in a group.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon action bar is clicked; go to parent activity
                this.finish();
                return true;
            case R.id.activity_login:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateGroupClick(View v) {
        if (currentGroup.isEmpty()) {
            Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
            i.putExtra("building", buildingName);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "You are already in a group!", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * An adapter that displays group information in a ListView
     */
    private class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
        /**
         * Describes how a member of the RecyclerView will be laid out
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView firstLine;
            public TextView secondLine;
            public Button joinGroup;
            public Button icon;

            public ViewHolder(View itemView) {
                super(itemView);
                firstLine = (TextView) itemView.findViewById(R.id.firstLine);
                secondLine = (TextView) itemView.findViewById(R.id.secondLine);
                joinGroup = (Button) itemView.findViewById(R.id.joinGroup);
                joinGroup.setVisibility(currentGroup.isEmpty() ? View.VISIBLE : View.GONE);
                icon = (Button) itemView.findViewById(R.id.icon);
            }
        }

        private final Context context;
        private final List<Group> groups;

        /**
         * Creates an adapter with the specified list of groups
         * @param c the context
         * @param g the list of groups to display
         */
        public GroupListAdapter(Context c, List<Group> g) {
            context = c;
            groups = g;
        }

        /**
         * @return the context of this adapter
         */
        public Context getContext() {
            return context;
        }

        @Override
        public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.group_list_item_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Group group = groups.get(position);
            TextView firstLine = holder.firstLine;
            TextView secondLine = holder.secondLine;
            Button joinButton = holder.joinGroup;
            String numText = group.getNumPeople() + (group.getNumPeople() != 1 ? " people" : " person");
            firstLine.setText(group.getName() + " - " + numText);
            secondLine.setText(group.getDetails());

            final int p = holder.getAdapterPosition();
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGroup(group);
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                holder.joinGroup.setVisibility(currentGroup.isEmpty() ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }
    }
}
