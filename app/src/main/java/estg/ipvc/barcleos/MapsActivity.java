package estg.ipvc.barcleos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private RequestQueue mQueue;
    private GoogleMap mMap;





    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        if(!GPSEnabled){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }else{
            mLocationPermissionsGranted=true;
        }

        mMap = googleMap;



        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            //LatLng SenhorDaCruz = new LatLng(41.531556, -8.619253);
           // mMap.addMarker(new MarkerOptions().position(SenhorDaCruz)
                  //  .title("Senhor da Cruz"));

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            //}
        }

    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 13f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        final ImageButton buttonParse = findViewById(R.id.button2);
        final ImageButton buttonParse1 = findViewById(R.id.button3);
        final ImageButton buttonParse2 = findViewById(R.id.button4);
        final ImageButton buttonParse3 = findViewById(R.id.button5);

        mQueue = Volley.newRequestQueue(this);


        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });


        buttonParse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse1();
            }
        });

        buttonParse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse2();
            }
        });

        buttonParse3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse3();
            }
        });

        getLocationPermission();

    }



    private void jsonParse(){

        mMap.clear();

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String url1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=";
        double url2 =  latitude;
        String url3 = "%2C";
        double url4 = longitude;
        String url5 = "&radius=200&key=AIzaSyCHOaS_2VU4s9jcnOKl5TXYxs6T5mvsM20";

        String url = url1 + url2 + url3 + url4 + url5;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,/*(String)*/null,
                new Response.Listener<JSONObject>() {

                    @Override

                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject loc = jsonArray.getJSONObject(i);
                                JSONObject geometry = loc.getJSONObject("geometry");
                                JSONObject local = geometry.getJSONObject("location");
                                String nome = loc.getString("name");
                                Double x = local.getDouble("lat");
                                Double y = local.getDouble("lng");


                                LatLng po = new LatLng(x,y);

                                MarkerOptions marker = new MarkerOptions().position(po).title(nome);
                                mMap.addMarker(marker);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                            toast2.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

        mQueue.add(request);
    }


    private void jsonParse1(){

        mMap.clear();

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String url1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=cafe&location=";
        double url2 =  latitude;
        String url3 = "%2C";
        double url4 = longitude;
        String url5 = "&radius=200&hasNextPage=true&nextPage()=true&sensor=false&key=AIzaSyCHOaS_2VU4s9jcnOKl5TXYxs6T5mvsM20";

        String url = url1 + url2 + url3 + url4 + url5;
        //String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=41.489781%2C-8.578192&radius=5000&key=AIzaSyASBDS3rmwb38SCjHCcIDo1SKHCMz5_Iwc";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,/*(String)*/null,
                new Response.Listener<JSONObject>() {

                    @Override

                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject loc = jsonArray.getJSONObject(i);
                                JSONObject geometry = loc.getJSONObject("geometry");
                                JSONObject local = geometry.getJSONObject("location");
                                String nome = loc.getString("name");
                                Double x = local.getDouble("lat");
                                Double y = local.getDouble("lng");


                                LatLng po = new LatLng(x,y);

                                MarkerOptions marker = new MarkerOptions().position(po).title(nome);
                                mMap.addMarker(marker);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                            toast2.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

        mQueue.add(request);
    }





    private void jsonParse2(){

        mMap.clear();

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String url1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=hotel&location=";
        double url2 =  latitude;
        String url3 = "%2C";
        double url4 = longitude;
        String url5 = "&radius=200&hasNextPage=true&nextPage()=true&sensor=false&key=AIzaSyCHOaS_2VU4s9jcnOKl5TXYxs6T5mvsM20";

        String url = url1 + url2 + url3 + url4 + url5;
        //String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=41.489781%2C-8.578192&radius=5000&key=AIzaSyASBDS3rmwb38SCjHCcIDo1SKHCMz5_Iwc";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,/*(String)*/null,
                new Response.Listener<JSONObject>() {

                    @Override

                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject loc = jsonArray.getJSONObject(i);
                                JSONObject geometry = loc.getJSONObject("geometry");
                                JSONObject local = geometry.getJSONObject("location");
                                String nome = loc.getString("name");
                                Double x = local.getDouble("lat");
                                Double y = local.getDouble("lng");


                                LatLng po = new LatLng(x,y);

                                MarkerOptions marker = new MarkerOptions().position(po).title(nome);
                                mMap.addMarker(marker);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                            toast2.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

        mQueue.add(request);
    }





    private void jsonParse3(){

        mMap.clear();

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String url1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=pastelaria&location=";
        double url2 =  latitude;
        String url3 = "%2C";
        double url4 = longitude;
        String url5 = "&radius=200&hasNextPage=true&nextPage()=true&sensor=false&key=AIzaSyCHOaS_2VU4s9jcnOKl5TXYxs6T5mvsM20";

        String url = url1 + url2 + url3 + url4 + url5;
        //String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=41.489781%2C-8.578192&radius=5000&key=AIzaSyASBDS3rmwb38SCjHCcIDo1SKHCMz5_Iwc";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,/*(String)*/null,
                new Response.Listener<JSONObject>() {

                    @Override

                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject loc = jsonArray.getJSONObject(i);
                                JSONObject geometry = loc.getJSONObject("geometry");
                                JSONObject local = geometry.getJSONObject("location");
                                String nome = loc.getString("name");
                                Double x = local.getDouble("lat");
                                Double y = local.getDouble("lng");


                                LatLng po = new LatLng(x,y);

                                MarkerOptions marker = new MarkerOptions().position(po).title(nome);
                                mMap.addMarker(marker);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                            toast2.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast toast2 = Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possível carregar os dados", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

        mQueue.add(request);
    }












    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            mMap.setMapType(mMap.MAP_TYPE_HYBRID);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Sem ligação à Internet, não foi possivel aceder ao mapa", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        //mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = true;
        initMap();

    }

}
