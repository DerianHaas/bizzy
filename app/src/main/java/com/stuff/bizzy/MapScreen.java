package com.stuff.bizzy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.stuff.bizzy.BuildingList.buildings;

public class MapScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String user = "Me"; //TODO Get current user from login/profile

    private HashMap<Building, Polygon> polygonMap;
    public static LatLngBounds boundaries = new LatLngBounds(new LatLng(33.770486, -84.407322), new LatLng(33.781467, -84.387799));
    private Set<Group> groups;
    private Map<Marker, Group> groupMarkers;
    private Marker currentMarker;

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

        groups = new HashSet<>(); //TODO Get from server
        groups.add(new Group(new LatLng(33.774496, -84.396266)));//example group
        groups.add(new Group(new LatLng(33.774651, -84.397263)));//example group
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
                    findViewById(R.id.joinGroup).setVisibility(View.VISIBLE);
                    findViewById(R.id.joinPanel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.joinPanel).setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


        //Clicking off the groups changes the button back to "Create Group"
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentMarker = null;
                findViewById(R.id.joinGroup).setVisibility(View.INVISIBLE);
                findViewById(R.id.createGroup).setVisibility(View.VISIBLE);
                findViewById(R.id.joinPanel).setVisibility(View.INVISIBLE);
                findViewById(R.id.joinPanel).setVisibility(View.VISIBLE);
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
                findViewById(R.id.joinPanel).setVisibility(View.VISIBLE);
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
                findViewById(R.id.joinPanel).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Called when the "Join this Group" button is clicked
     * @param v the current view
     */
    public void onJoinGroupClick(View v) {
        boolean b = groupMarkers.get(currentMarker).addToGroup(user);
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
}
