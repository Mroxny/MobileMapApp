package com.mroxny.mobilemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleOverlay;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        MapTileProviderBase base = new MapTileProviderBasic(ctx,TileSourceFactory.MAPNIK);
        map = (MapView) findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTileProvider(base);
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        location = new Location();
        Init();
    }

    public void GoToMyLocation(View view){
        if(location.isGpsOn(this)) {
            GeoPoint myLocation = this.mLocationOverlay.getMyLocation();
            map.getController().animateTo(myLocation);
            return;
        }
        location.SetRequest(this);
        if(location.isGpsOn(this)) GoToMyLocation(view);
    }

    public void ZoomIn(View view){
        if(map.canZoomIn()) map.getController().zoomIn();

    }

    public void ZoomOut(View view){
        if(map.canZoomOut()) map.getController().zoomOut();
    }


    private void Init(){

        Context context = getApplicationContext();
        map.setMinZoomLevel(4.0);
        map.setMaxZoomLevel(21.0);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(GetLastLocation());

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context),map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        /*this.mScaleOverlay = new ScaleBarOverlay(map);
        this.mScaleOverlay.enableScaleBar();
        this.mScaleOverlay.setCentred(true);
        this.mScaleOverlay.setScaleBarOffset(map.getHeight(),map.getWidth()/2);
        map.getOverlays().add(this.mScaleOverlay);*/

        this.mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        this.mCompassOverlay.enableCompass();
        this.mCompassOverlay.setCompassCenter(40,60);
        map.getOverlays().add(this.mCompassOverlay);

    }

    private GeoPoint GetLastLocation(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Double lat = Double.longBitsToDouble(sharedPref.getLong("Lat",0));
        Double lon = Double.longBitsToDouble(sharedPref.getLong("Lon",0));
        return new GeoPoint(lat,lon);
    }

    private void SetLastLocation(){
        if(location.isGpsOn(this)) {
            GeoPoint myLocation = this.mLocationOverlay.getMyLocation();
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("Lat",Double.doubleToRawLongBits(myLocation.getLatitude()));
            editor.putLong("Lon",Double.doubleToRawLongBits(myLocation.getLongitude()));
            editor.commit();
        }
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SetLastLocation();
        map.onPause();
    }
}