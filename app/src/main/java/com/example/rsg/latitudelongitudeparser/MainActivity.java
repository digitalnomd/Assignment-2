package com.example.rsg.latitudelongitudeparser;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView longVal_, latVal_;
    private Button query_;
    private LocationManager locationManager_;
    DBHelperClass dbhelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        longVal_ = (TextView) findViewById(R.id.textViewLongVal);
        latVal_ = (TextView) findViewById(R.id.textViewLatVal);
        query_ = (Button) findViewById(R.id.buttonQuery);

        query_.setOnClickListener(this);
        dbhelper = new DBHelperClass(this); //present context
        database = dbhelper.getWritableDatabase();
        startLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.buttonQuery:
                Cursor temp = fetchLastLoc();

                do {
                    latVal_.setText(temp.getString(2));
                    longVal_.setText(temp.getString(3));
                }while(temp.moveToNext());

             break;
        }
    }

    public Cursor fetchLastLoc() {
        //TODO modify query to select last location only
        return database.rawQuery("SELECT * FROM location ORDER BY id DESC LIMIT 1;", null);
        //return database.query("location", new String[]{"id","timestamp", "longitude", "latitude"}, null, null, null, null, null);
    }


    private class storeLocationData extends Thread{

        double longitude, latitude;
        String timestamp;
        public storeLocationData(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
            String timestamp = sdf.format(c.getTime());
        }
        @Override
        public void run() {
            ContentValues values = new ContentValues();
            values.put("timestamp", timestamp);
            values.put("longitude", longitude);
            values.put("latitude", latitude);

            database.insert("location", null, values);
        }
    }


    public void startLocation() {

        locationManager_ = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);

        locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                new storeLocationData(location.getLatitude(), location.getLongitude()).start();

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
        });
    }

}
