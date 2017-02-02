package com.stuff.bizzy.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.stuff.bizzy.Models.Building;
import com.stuff.bizzy.Models.GroupList;
import com.stuff.bizzy.Models.User;
import com.stuff.bizzy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stuff.bizzy.Models.BuildingList.buildings;
import static com.stuff.bizzy.R.id.buildingList;


public class MapScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private User user = new User("Me", "...", "example@stuff.com"); //TODO Get current user from login/profile

    public static LatLngBounds boundaries = new LatLngBounds(new LatLng(33.770486, -84.407322), new LatLng(33.781467, -84.387799));
    private Map<Marker, Building> markerMap = new HashMap<>();
    private Marker currentMarker;

    private RecyclerView buildingView;
    private BuildingListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        GroupList.refreshGroupList(this);

        buildingView = (RecyclerView) findViewById(R.id.list);
        adapter = new BuildingListAdapter(getApplicationContext(), new ArrayList<>(buildings));
        buildingView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        buildingView.setLayoutManager(llm);

        final EditText filter = (EditText) findViewById(R.id.searchBar);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBuildings(s);
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
                    String s = filter.getText().toString();
                    filterBuildings(s);
                }
            }
        });
        /*Request User Location Permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationEnabled = true;
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        */
        //Set map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setLatLngBoundsForCameraTarget(boundaries);
        mMap.setMinZoomPreference(16);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(boundaries.getCenter(), 17));
        mMap.setPadding(0,0,0,300);

        //Place Building Markers
        placeBuildingMarkers();

        //Clicking on a building changes the button to "View Building Groups"
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker = marker;
                switchBottomPanel(buildingList);
                return false;
            }
        });


        //Clicking off the groups changes the button back to "Create Group"
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentMarker = null;
                switchBottomPanel(R.id.profilePage);
            }
        });

        //Displays info windows
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

    private void filterBuildings(CharSequence s) {
        if (s.length() == 0) {
            findViewById(R.id.list).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.list).setVisibility(View.VISIBLE);
            String str = s.toString().toLowerCase();
            ArrayList<Building> filtered = new ArrayList<>();
            for (Building b : buildings) {
                if (b.getName().toLowerCase().contains(str)) {
                    filtered.add(b);
                }
            }
            if (filtered.isEmpty()) {
                findViewById(R.id.list).setVisibility(View.INVISIBLE);
            } else {
                Collections.sort(filtered, Collections.<Building>reverseOrder());
                adapter = new BuildingListAdapter(getApplicationContext(), filtered);
                buildingView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /*
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
    */
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
     * Called when the "Create Group" button is clicked
     * @param v the current view
     */
    public void onCreateGroupClick(View v) {
        //TODO Create a group and send to server
    }

//    /**
//     * Called when the "Display Buildings" button is clicked.
//     * Displays the building borders and center markers.
//     * @param v the current view
//     */
//    public void toggleBuildingBorders(View v) {
//        for (Building b : buildings) {
//            polygonMap.get(b).setVisible(!polygonMap.get(b).isVisible());
//            b.getCenterMarker().setVisible(!b.getCenterMarker().isVisible());
//        }
//    }

    /**
     * Called when the ListToggle button is clicked
     * @param v the current view
     */
    public void switchToListView(View v) {
        Intent i = new Intent(getApplicationContext(), BuildingActivity.class);
        i.putExtra("building", markerMap.get(currentMarker).getName());
        startActivity(i);
    }

    private void placeBuildingMarkers() {
        for (Building b : buildings) {
            markerMap.put(mMap.addMarker(b.display()), b);
        }
    }


    private void switchBottomPanel(int id) {
        if (id != R.id.profilePage && id != buildingList) {
            throw new IllegalArgumentException("Can only switch profilePage and BuildingList");
        }
        findViewById(R.id.profilePage).setVisibility(View.INVISIBLE);
        findViewById(buildingList).setVisibility(View.INVISIBLE);
        findViewById(id).setVisibility(View.VISIBLE);
        findViewById(R.id.bottomPanel).setVisibility(View.INVISIBLE);
        findViewById(R.id.bottomPanel).setVisibility(View.VISIBLE);
    }
    //endregion


    /**
     * An adapter that displays building information in a ListView
     */
    private class BuildingListAdapter extends RecyclerView.Adapter<MapScreen.BuildingListAdapter.ViewHolder> {
        /**
         * Describes how a member of the RecyclerView will be laid out
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView firstLine;
            public TextView secondLine;
            public Button listButton;
            public Button mapButton;

            public ViewHolder(View itemView) {
                super(itemView);
                firstLine = (TextView) itemView.findViewById(R.id.firstLine);
                secondLine = (TextView) itemView.findViewById(R.id.secondLine);
                listButton = (Button) itemView.findViewById(R.id.viewBuilding);
                mapButton = (Button) itemView.findViewById(R.id.viewMap);
            }
        }

        private final Context context;
        private final List<Building> buildings;

        /**
         * Creates an adapter with the specified list of groups
         * @param c the context
         * @param b the list of buildings to display
         */
        public BuildingListAdapter(Context c, List<Building> b) {
            context = c;
            Collections.sort(b, Collections.<Building>reverseOrder());
            buildings = b;
        }

        /**
         * @return the context of this adapter
         */
        public Context getContext() {
            return context;
        }

        @Override
        public MapScreen.BuildingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.building_list_item_layout, parent, false);
            MapScreen.BuildingListAdapter.ViewHolder viewHolder = new MapScreen.BuildingListAdapter.ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MapScreen.BuildingListAdapter.ViewHolder holder, int position) {
            Building b = buildings.get(position);
            TextView firstLine = holder.firstLine;
            TextView secondLine = holder.secondLine;
            Button listButton = holder.listButton;
            Button mapButton = holder.mapButton;
            firstLine.setText(b.getName());
            secondLine.setText("Number of Groups: " + b.getNumGroups());

            final int p = holder.getAdapterPosition();
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), BuildingActivity.class);
                    i.putExtra("building", buildings.get(p).getName());
                    startActivity(i);

                }
            });
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchBottomPanel(buildingList);
                    Marker marker = currentMarker;
                    for (Marker m : markerMap.keySet()) {
                        if (markerMap.get(m).equals(buildings.get(p))) {
                            marker = m;
                            break;
                        }
                    }
                    findViewById(R.id.list).setVisibility(View.INVISIBLE);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
                    marker.showInfoWindow();
                }
            });
        }

        @Override
        public int getItemCount() {
            return buildings.size();
        }
    }

}

