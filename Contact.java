package dianyo.apex;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Contact extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        LocationListener{
    private static final String TAG = Contact.class.getSimpleName();
    private ViewPager contactViewPager;
    private View contactView, infoView, hospitalView;
    private ImageButton contactPageButton, infoPageButton, hospitalPageButton;
    private ImageButton backButton;
    private View slideToggleLine;
    private EditText infoFirstText, infoSecondText;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private CameraPosition mCameraPosition;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        processView();
        back();
        viewPagerController();
        addMap();
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this).addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    private void processView(){
        contactViewPager = (ViewPager) findViewById(R.id.contactViewPager);
        contactPageButton = (ImageButton) findViewById(R.id.contactPageButton);
        infoPageButton = (ImageButton) findViewById(R.id.infoPageButton);
        hospitalPageButton = (ImageButton) findViewById(R.id.hospitalPageButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        slideToggleLine = (View) findViewById(R.id.slideToggleLine);
    }

    private void back() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void viewPagerController(){
        ArrayList<View> viewList = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater().from(this);
        contactView = inflater.inflate(R.layout.page_contact, null);
        infoView = inflater.inflate(R.layout.page_info, null);
        hospitalView = inflater.inflate(R.layout.page_hospital, null);

        viewList.add(contactView);
        viewList.add(infoView);
        viewList.add(hospitalView);

        contactViewPager.setAdapter(new ViewPagerAdapter(viewList));
        contactViewPager.setCurrentItem(0);
        contactViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSlideToggleLinePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        contactPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactViewPager.setCurrentItem(0);
            }
        });
        infoPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactViewPager.setCurrentItem(1);
            }
        });
        hospitalPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactViewPager.setCurrentItem(2);
            }
        });
    }

    private void setSlideToggleLinePosition(int position) {
        RelativeLayout.LayoutParams params
                = (RelativeLayout.LayoutParams) slideToggleLine.getLayoutParams();
        switch (position) {
            case 0:
                params.addRule(RelativeLayout.ALIGN_START, R.id.contactPageButton);
                params.addRule(RelativeLayout.ALIGN_END, R.id.contactPageButton);
                break;
            case 1:
                params.addRule(RelativeLayout.ALIGN_START, R.id.infoPageButton);
                params.addRule(RelativeLayout.ALIGN_END, R.id.infoPageButton);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_START, R.id.hospitalPageButton);
                params.addRule(RelativeLayout.ALIGN_END, R.id.hospitalPageButton);
        }
        slideToggleLine.setLayoutParams(params);
    }

    private void addMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void updateMyMap(){
        if (mMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateMyMap();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateMyMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
    @Override
    public boolean onMyLocationButtonClick(){
        Log.d("debug", "the location button has been clicked");
        updateMyMap();
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", "location has changed");
        mLastKnownLocation = location;
    }
}
