package com.stuff.bizzy.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.stuff.bizzy.Models.ConnectionClass;
import com.stuff.bizzy.Models.Group;
import com.stuff.bizzy.Models.GroupComparator;
import com.stuff.bizzy.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BuildingActivity extends AppCompatActivity {

    private String buildingName;
    private List<Group> groupList;
    private RecyclerView groupView;
    private GroupListAdapter adapter;
    private String currentGroup = "";

    private ConnectionClass connectionClass = new ConnectionClass();

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
        groupView.setHasFixedSize(true);

        GroupQuery query = new GroupQuery();
        query.execute();



        EditText filter = (EditText) findViewById(R.id.searchBar);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Group> filtered = new ArrayList<>();
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
    }

    public void onLeaveGroupClicked(View v) {
        if (!currentGroup.isEmpty()) {

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
            String numText = group.getNumMembers() + (group.getNumMembers() != 1 ? " people" : " person");
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


    private class GroupQuery extends AsyncTask<Void, Void, List<Group>> {

        @Override
        protected List<Group> doInBackground(Void... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    Log.e("Connection", "Couldn't connect to the database.");
                    return new ArrayList<>();
                } else {
                    String query = "EXECUTE [dbo].[uspMemberCount] @strBuildingName = " + buildingName;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    List<Group> data = new ArrayList<>();
                    while (rs.next()) {
                        String name = rs.getString("Name");
                        int numMembers = rs.getInt("MemberCount");
                        data.add(new Group(name, "", numMembers));
                        Log.d("Group", "Group "+name+" with "+numMembers+" members");
                    }
                    return data;
                }
            } catch (SQLException e) {
                Log.e("Connection", "SQL Exception Occurred: "+e.getMessage());
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            groupList = groups;
//            Collections.sort(groupList, new GroupComparator(GroupComparator.Method.GROUP_SIZE));
            adapter.notifyDataSetChanged();
        }
    }
}
