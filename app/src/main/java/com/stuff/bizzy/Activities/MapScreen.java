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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.stuff.bizzy.Models.Building;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static LatLngBounds boundaries = new LatLngBounds(new LatLng(33.770486, -84.407322), new LatLng(33.781467, -84.387799));
    private Map<Building, Marker> markerMap = new HashMap<>();
    private Map<String, Integer> numGroups = new HashMap<>();
    private Marker currentMarker;

    private List<Building> buildings;
    private RecyclerView buildingView;
    private BuildingListAdapter adapter;

    private DatabaseReference ref;
    private DatabaseReference buildingRef;

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
        buildings = new ArrayList<>();
        ref = Database.getReference("groups");
        buildingRef = Database.getReference("buildings");

        //Note: SingleValueEvent always triggers after ChildEvents are finished,
        //so this listener triggers once all buildings are downloaded from server.
        buildingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Building b : buildings) {
                    numGroups.put(b.getName(), 0);
                    markerMap.put(b, mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(b.getCenter()).visible(false)));
//                    Create placeholder (invisible) markers
                }
//              Get groups from database and count how many groups per building
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String buildingName = dataSnapshot.child("location").getValue().toString().trim();
                        Log.d("GroupAdded","Building: "+ buildingName);
                        if (numGroups.get(buildingName) == null) {
                            numGroups.put(buildingName, 0);
                        }
                        numGroups.put(buildingName, numGroups.get(buildingName) + 1);
                        markerMap.get(getBuilding(buildingName)).setTitle(buildingName + "\nNumber of Groups: " + numGroups.get(buildingName));
                        markerMap.get(getBuilding(buildingName)).setVisible(true);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String buildingName = dataSnapshot.child("location").getValue().toString().trim().toLowerCase();
                        numGroups.put(buildingName, numGroups.get(buildingName) - 1);
                        markerMap.get(getBuilding(buildingName)).setTitle(buildingName + "\nNumber of Groups: " + numGroups.get(buildingName));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//        Get building list from server
        buildingRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.child("name").getValue(String.class);
                GenericTypeIndicator<List<Map<String, Double>>> t = new GenericTypeIndicator<List<Map<String,Double>>>() {};
                List<Map<String, Double>> list = dataSnapshot.child("coordinates").getValue(t);
                List<LatLng> coords = new ArrayList<>();
                for (Map<String, Double> map : list) {
                    coords.add(new LatLng(map.get("latitude"), map.get("longitude")));
                }
                Building b = new Building(name, coords);
                buildings.add(b);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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





        //Clicking on a building changes the button to "View Building Groups"
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker = marker;
                switchBottomPanel(R.id.buildingList);
                return false;
            }
        });


        //Clicking off the building changes the button back to "Profile Page"
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

//    Controller for building list search filter
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
                adapter = new BuildingListAdapter(getApplicationContext(), filtered);
                buildingView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Get building from list based on name
     * @param name the name of the building
     * @return the building with the specified namw
     */
    private Building getBuilding(String name) {
        for (Building b : buildings) {
            if (name.equals(b.getName())) {
                return b;
            }
        }
        return null;
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



    public void onProfileClick(View v) {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }


    /**
     * Called when the ListToggle button is clicked
     * @param v the current view
     */
    public void switchToListView(View v) {
        Intent i = new Intent(getApplicationContext(), BuildingActivity.class);
        for (Building b : markerMap.keySet()) {
            if (markerMap.get(b).equals(currentMarker)) {
                i.putExtra("building", b.getName());
                break;
            }
        }
        startActivity(i);
    }




    private void switchBottomPanel(int id) {
        if (id != R.id.profilePage && id != R.id.buildingList) {
            throw new IllegalArgumentException("Can only switch profilePage and BuildingList");
        }
        findViewById(R.id.profilePage).setVisibility(View.INVISIBLE);
        findViewById(R.id.buildingList).setVisibility(View.INVISIBLE);
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
         * Creates an adapter with the specified list of buildings
         * @param c the context
         * @param b the list of buildings to display
         */
        public BuildingListAdapter(Context c, List<Building> b) {
            context = c;
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
            secondLine.setText("Number of Groups: " + numGroups.get(b.getName()));

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
                    switchBottomPanel(R.id.buildingList);
                    Marker marker = markerMap.get(buildings.get(p));
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


