package com.stuff.bizzy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stuff.bizzy.BuildingList.buildings;
import static com.stuff.bizzy.R.id.joinGroup;
import static com.stuff.bizzy.R.id.joinPanel;

public class MapScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private User user = new User("Me", "..."); //TODO Get current user from login/profile

    private HashMap<Building, Polygon> polygonMap;
    public static LatLngBounds boundaries = new LatLngBounds(new LatLng(33.770486, -84.407322), new LatLng(33.781467, -84.387799));
    private List<Group> groups;
    private Map<Marker, Group> groupMarkers;
    private Marker currentMarker;
    private RecyclerView groupList;
    public static GroupListAdapter adapter;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 12;
    private boolean isLocationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GroupList.refreshGroupList();
        groups = GroupList.sortGroups(GroupComparator.Method.GROUP_SIZE);


        groupList = (RecyclerView) findViewById(R.id.list);
        groupList.setHasFixedSize(true);
        adapter = new GroupListAdapter(this, groups);
        groupList.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupList.setLayoutManager(llm);

        EditText filter = (EditText) findViewById(R.id.searchBar);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Group> filtered = new ArrayList<Group>();
                for (Group g : groups) {
                    if (g.getName().contains(s)) {
                        filtered.add(g);
                    }
                }
                adapter = new GroupListAdapter(getApplicationContext(), filtered);
                groupList.setAdapter(adapter);
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
                groups = GroupList.sortGroups(GroupComparator.Method.values()[position]);
                adapter = new GroupListAdapter(getApplicationContext(), groups);
                groupList.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Request User Location Permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationEnabled = true;
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        //Set map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setLatLngBoundsForCameraTarget(boundaries);
        mMap.setMinZoomPreference(16);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(boundaries.getCenter(), 17));
        mMap.setPadding(0,0,0,300);

        //Draw the buildings on the map (invisible by default)
        placeBuildings();

        //Check what building each group is in
        for (Group g : groups) {
            for (Building b : buildings) {
                if (PolyUtil.containsLocation(g.getLocation(), b.getCoords(),false)){
                    g.setBuilding(b);
                    break;
                }
            }
        }

        //Place the marker for each group on the map
        placeGroupMarkers();

        //Clicking on a group changes the button to "Join Group"
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker = marker;
                if (isLocationEnabled) {
                    findViewById(R.id.createGroup).setVisibility(View.INVISIBLE);
                    findViewById(joinGroup).setVisibility(View.VISIBLE);
                    findViewById(joinPanel).setVisibility(View.INVISIBLE);
                    findViewById(joinPanel).setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


        //Clicking off the groups changes the button back to "Create Group"
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentMarker = null;
                findViewById(joinGroup).setVisibility(View.INVISIBLE);
                findViewById(R.id.createGroup).setVisibility(View.VISIBLE);
                findViewById(joinPanel).setVisibility(View.INVISIBLE);
                findViewById(joinPanel).setVisibility(View.VISIBLE);
            }
        });

        //Displays group info windows
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());


                info.addView(title);

                return info;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                isLocationEnabled = true;
                findViewById(joinPanel).setVisibility(View.VISIBLE);
            } else {
                // permission denied. Disable the functionality that depends on this permission.
                new AlertDialog.Builder(this).
                        setMessage("You will not be able to create or join a group until this app can access your location!").
                        setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                isLocationEnabled = false;
                findViewById(joinPanel).setVisibility(View.INVISIBLE);
            }
        }
    }

    //region keyboard hiding methods
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

    //region Button click methods
    /**
     * Called when the "Join this Group" button is clicked
     * @param v the current view
     */
    public void onJoinGroupClick(View v) {
        joinGroup(groupMarkers.get(currentMarker));

    }

    /**
     * Joins the specified group if able
     * @param g the group to join
     */
    private void joinGroup(Group g) {
        boolean b = g.addToGroup(user);
        if (b) {
            new AlertDialog.Builder(this).setTitle("Success").
                    setMessage("You successfully joined this study group!").setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            mMap.clear();
            placeGroupMarkers();
            placeBuildings();
            adapter.notifyDataSetChanged();
            //TODO Send user to study group chat/page
            //TODO Send info to server
        } else {
            new AlertDialog.Builder(this).setTitle("Alert").
                    setMessage("An error occurred when joining this group.  Please try again later.").setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    /**
     * Called when the "Create Group" button is clicked
     * @param v the current view
     */
    public void onCreateGroupClick(View v) {
        //TODO Create a group and send to server
    }

    /**
     * Called when the "Display Buildings" button is clicked.
     * Displays the building borders and center markers.
     * @param v the current view
     */
    public void toggleBuildingBorders(View v) {
        for (Building b : buildings) {
            polygonMap.get(b).setVisible(!polygonMap.get(b).isVisible());
            b.getCenterMarker().setVisible(!b.getCenterMarker().isVisible());
        }
    }

    /**
     * Called when the ListToggle button is clicked
     * @param v the current view
     */
    public void switchToListView(View v) {
        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);
        findViewById(R.id.joinPanel).setVisibility(View.INVISIBLE);
        findViewById(R.id.listPanel).setVisibility(View.VISIBLE);

    }

    /**
     * Called when the MapToggle button is clicked
     * @param v the current view
     */
    public void switchToMapButton(View v) {
        switchToMapView(null);
    }

    /**
     * Switches to the map view, then centers it on the specified group
     * @param g the group to focus on
     */
    private void switchToMapView(Group g) {
        findViewById(R.id.listPanel).setVisibility(View.INVISIBLE);
        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);
        findViewById(R.id.joinPanel).setVisibility(View.VISIBLE);
        if (g != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(g.getLocation(), 17));
            for (Marker m : groupMarkers.keySet()) {
                if (m.getPosition().equals(g.getLocation())) {
                    m.showInfoWindow();
                    break;
                }
            }
        }
    }

    private void placeGroupMarkers() {
        groupMarkers = new HashMap<>();
        for (Group g : groups) {
            groupMarkers.put(mMap.addMarker(g.display()), g);
        }
    }

    private void placeBuildings() {
        polygonMap = new HashMap<>();
        for (Building b : buildings) {
            polygonMap.put(b, b.createBuilding(mMap, Color.BLACK));
        }
    }
    //endregion

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
            Group group = groups.get(position);
            TextView firstLine = holder.firstLine;
            TextView secondLine = holder.secondLine;
            Button joinButton = holder.joinGroup;
            Button mapButton = holder.icon;
            String numText = group.getNumPeople() + (group.getNumPeople() > 1 ? " people" : " person");
            firstLine.setText(group.getName() + " - " + numText);
            String buildingName = group.getBuildingName();
            if (!buildingName.isEmpty()) {
                secondLine.setText("Location: " + buildingName);
            } else {
                secondLine.setText("Location: Not in a Building");
            }
            final int p = holder.getAdapterPosition();
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGroup(groups.get(p));
                }
            });
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToMapView(groups.get(p));
                }
            });
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }
    }
}

