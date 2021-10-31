package com.mroxny.mobilemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private GeocoderNominatim geocoder = new GeocoderNominatim("OsmNavigator/2.4");
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.sv);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
    }

    public void goBack(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}