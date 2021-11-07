package com.mroxny.mobilemapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private GeocoderNominatim geocoder;
    private SearchView searchView;
    private ListView searchList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        geocoder = new GeocoderNominatim("OsmNavigator/2.4");

        SetSearchView();
        searchList = (ListView) findViewById(R.id.search_list);
    }

    private void SetSearchView(){
        searchView = (SearchView) findViewById(R.id.sv);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fillSearchList(GetAddress(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>=3){

                }
                return false;
            }
        });
    }
    private void fillSearchList(@Nullable List<Address> list) {
        if (list != null) {

            //list.sort(new SortPlaces(currentPos));
            List<Map<String, String>> scores = new ArrayList<>();
            for (Address e : list) {
                StringBuilder addressLine2Builder = new StringBuilder();
                for (int i = 1; i <= e.getMaxAddressLineIndex(); i++) {
                    addressLine2Builder.append(e.getAddressLine(i));
                    if (i + 1 <= e.getMaxAddressLineIndex()) {
                        addressLine2Builder.append(", ");
                    }
                }
                Map<String, String> datum = new HashMap<>(2);
                datum.put("adres1", e.getAddressLine(0));
                datum.put("adres2", addressLine2Builder.toString());
                scores.add(datum);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, scores,
                    R.layout.searching_layout,
                    new String[]{"adres1", "adres2"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    double lat = list.get(i).getLatitude();
                    double lon = list.get(i).getLongitude();
                    String s = lat + "," + lon;
                    /*Intent intent = new Intent();
                    Utils.setCoordsToIntent(GeoPoint.fromDoubleString(s, ','), intent);
                    setResult(Activity.RESULT_OK, intent);*/
                    //finish();
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("koordynaty", s);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(SearchActivity.this, "Skopiowano koordynaty", Toast.LENGTH_SHORT).show();
                }
            });

            /*searchList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    double lat = list.get(position).getLatitude();
                    double lon = list.get(position).getLongitude();
                    String s = lat + "," + lon;
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("koordynaty", s);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(SearchActivity.this, "Skopiowano koordynaty", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/
            this.adapter = adapter;
            searchList.setAdapter(this.adapter);
        }

    }

    private List<Address> GetAddress(String query){
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(query,20);
            //addresses.sort(new SortPlaces());
        } catch (IOException e) {
            addresses = null;
        }
        return addresses;
    }

    public void goBack(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}