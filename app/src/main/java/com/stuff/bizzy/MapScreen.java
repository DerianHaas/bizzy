package com.stuff.bizzy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
    private String user = "Me"; //TODO Get current user from login/profile

    private HashMap<Building, Polygon> polygonMap;
    public static LatLngBounds boundaries = new LatLngBounds(new LatLng(33.770486, -84.407322), new LatLng(33.781467, -84.387799));
    private List<Group> groups;
    private Map<Marker, Group> groupMarkers;
    private Marker currentMarker;
    private ListView groupList;
    public static ArrayAdapter<Group> adapter;
    private SearchView searchBar;

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


        groupList = (ListView) findViewById(R.id.list);
        adapter = new GroupListAdapter(this, groups);
        groupList.setAdapter(adapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchBar = (SearchView) findViewById(R.id.searchBar);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchBar.setSubmitButtonEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.sortingSpinner);
        ArrayAdapter<CharSequence> spinadapter = ArrayAdapter.createFromResource(this, R.array.group_sort_option,
                android.R.layout.simple_spinner_item);
        spinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Group> sorted = GroupList.sortGroups(GroupComparator.Method.values()[position]);
                adapter = new GroupListAdapter(getApplicationContext(), sorted);
                groupList.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        int[] indices = data.getIntArrayExtra("Results");
//        Group[] filtered = new Group[indices.length];
//        Toast.makeText(this, indices.toString(), Toast.LENGTH_SHORT);
//        for (int i = 0; i < indices.length; i++){
//            filtered[i] = groups[indices[i]];
//        }
//        groupList.setAdapter(new GroupListAdapter(this, filtered));
//        groupList.invalidateViews();
//    }

    /**
     * An adapter that displays group information in a ListView
     */
    private class GroupListAdapter extends ArrayAdapter<Group> {
        private final Context context;
        private final List<Group> groups;

        /**
         * Creates an adapter with the specified list of groups
         * @param c the context
         * @param g the list of groups to display
         */
        public GroupListAdapter(Context c, List<Group> g) {
            super(c, -1, g);
            context = c;
            groups = g;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.group_list_item_layout, parent, false);
            TextView firstLine = (TextView) v.findViewById(R.id.firstLine);
            TextView secondLine = (TextView) v.findViewById(R.id.secondLine);
            Button joinButton = (Button) v.findViewById(R.id.joinGroup);
            Button mapButton = (Button) v.findViewById(R.id.icon);
            String numText = groups.get(position).getNumPeople() + (groups.get(position).getNumPeople() > 1 ? " people" : " person");
            firstLine.setText(groups.get(position).getName() + " - " + numText);
            String buildingName = groups.get(position).getBuildingName();
            if (!buildingName.isEmpty()) {
                secondLine.setText("Location: " + buildingName);
            } else {
                secondLine.setText("Location: Not in a Building");
            }
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGroup(groups.get(position));
                }
            });
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToMapView(groups.get(position));
                }
            });
            return v;
        }
    }
}

