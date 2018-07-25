package ng.org.faithtabernacle.android.MedivatorBeta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ng.org.faithtabernacle.android.MedivatorBeta.model.MyPlaces;
import ng.org.faithtabernacle.android.MedivatorBeta.model.Remote.IGoogleAPIService;
import ng.org.faithtabernacle.android.MedivatorBeta.model.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    IGoogleAPIService mService;

    private MyPlaces currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );

        //Initialize Service
        mService = Common.getGoogleAPIService ();

        //Request Runtime Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission ();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById ( R.id.bottom_nav );
        bottomNavigationView.setOnNavigationItemSelectedListener ( new BottomNavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId ()) {
                    case R.id.action_church:
                        nearByPlace ( "church" );
                        break;
                }

                switch (item.getItemId ()) {
                    case R.id.action_mall:
                        nearByPlace ( "shopping mall" );
                        break;
                }

                switch (item.getItemId ()) {
                    case R.id.action_restaurant:
                        nearByPlace ( "restaurant" );
                        break;
                }

                switch (item.getItemId ()) {
                    case R.id.action_school:
                        nearByPlace ( "school" );
                        break;

                default:
                break;
                }
                return true;
            }
        } );
    }

    private void nearByPlace(final String placeType) {
        mMap.clear ();
        String url = getUrl(latitude,longitude,placeType);

        mService.getNearByPlaces ( url )
                .enqueue ( new Callback<MyPlaces> () {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if(response.isSuccessful ())
                        {
                            for(int i=0; i>response.body ().getResults ().length; i++)
                            {
                                MarkerOptions markerOptions = new MarkerOptions ();
                                Results googlePlace = response.body ().getResults ()[i];
                                double lat = Double.parseDouble( String.valueOf ( googlePlace.getGeometry ().getLocation ().getLatitude () ) );
                                double lng = Double.parseDouble( String.valueOf ( googlePlace.getGeometry ().getLocation ().getLongitude () ) );
                                String placeName = googlePlace.getName ();
                                String vicinity = googlePlace.getVicinity ();
                                LatLng latLng = new LatLng ( lat,lng );
                                markerOptions.position ( latLng );
                                markerOptions.title ( placeName );
                                if (placeType.equals ( "church" )) {
                                    markerOptions.icon ( BitmapDescriptorFactory.fromResource (R.drawable.ic_church ) );
                                } else if (placeType.equals ( "shopping mall" ))
                                    markerOptions.icon ( BitmapDescriptorFactory.fromResource (R.drawable.ic_mall ) );

                                else if (placeType.equals ( "restaurant" ))
                                    markerOptions.icon ( BitmapDescriptorFactory.fromResource (R.drawable.ic_restaurant ) );

                                else if (placeType.equals ( "school" ))
                                    markerOptions.icon ( BitmapDescriptorFactory.fromResource (R.drawable.icc_school) );

                                else
                                    markerOptions.icon ( BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_ORANGE) );

                                markerOptions.snippet ( String.valueOf ( i ) ); // Assign index for marker

                                //Add Marker to Map
                                mMap.addMarker ( markerOptions );

                                //Move Camera
                                mMap.moveCamera ( CameraUpdateFactory.newLatLng ( latLng ) );
                                mMap.animateCamera ( CameraUpdateFactory.zoomTo ( 11 ) );
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                } );
    }

    private String getUrl(double latitude, double longitude, String placeType) {

        StringBuilder googlePlacesUrl = new StringBuilder ( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" );
        googlePlacesUrl.append ( "location="+latitude+","+longitude );
        googlePlacesUrl.append ( "&radius="+10000 );
        googlePlacesUrl.append ( "&type="+placeType );
        googlePlacesUrl.append ( "&sensor=true" );
        googlePlacesUrl.append ( "&key="+getResources ().getString ( R.string.browser_key ) );
        Log.d ("getUrl", googlePlacesUrl.toString ());

        return googlePlacesUrl.toString ();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case MY_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission ( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null)
                            buildGoogleApiClient ();
                        mMap.setMyLocationEnabled ( true );
                    }
                }

                else
                    Toast.makeText ( this, "Permission denied", Toast.LENGTH_LONG ).show ();
            }
            break;
        }
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale ( this, Manifest.permission.ACCESS_FINE_LOCATION ))
                ActivityCompat.requestPermissions ( this, new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);

            else
            ActivityCompat.requestPermissions ( this, new String[]{

                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_CODE);

             return false;
        } else
                 return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    // Initialize Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient ();
                mMap.setMyLocationEnabled ( true );
            }
        }
            else{
            buildGoogleApiClient ();
            mMap.setMyLocationEnabled ( true );
            }
         //Make event click on marker
        mMap.setOnMarkerClickListener ( new GoogleMap.OnMarkerClickListener () {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //When user selects marker, just get Result and assign to static variable
                Common.currentResult = currentPlace.getResults ()[Integer.parseInt ( marker.getSnippet () )];

                //Start new activity
             startActivity ( new Intent (MapsActivity.this, ViewPlace.class) );
                return true;
            }
        } );
        }

    private synchronized void buildGoogleApiClient() {
     mGoogleApiClient = new GoogleApiClient.Builder (this)
                         .addConnectionCallbacks ( this )
                         .addOnConnectionFailedListener ( this )
                         .addApi ( LocationServices.API )
                         .build ();
          mGoogleApiClient.connect ();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest ();
        mLocationRequest.setInterval ( 1000 );
        mLocationRequest.setFastestInterval ( 1000 );
        mLocationRequest.setPriority ( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );

        if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates ( mGoogleApiClient,mLocationRequest,this );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
      mGoogleApiClient.connect ();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
           mLastLocation = location;
             if(mMarker != null)
                 mMarker.remove ();


           latitude = location.getLatitude ();
           longitude = location.getLongitude ();

           LatLng latLng = new LatLng ( latitude,longitude );
           MarkerOptions markerOptions = new MarkerOptions ()
                   .position ( latLng )
                   .title ( "Your position" )
                   .icon ( BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_GREEN) );

           mMarker = mMap.addMarker ( markerOptions );

           //Move Camera
        mMap.moveCamera ( CameraUpdateFactory.newLatLng ( latLng ) );
        mMap.animateCamera ( CameraUpdateFactory.zoomTo ( 11 ) );

        if(mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates ( mGoogleApiClient, this );
    }
}

