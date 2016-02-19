package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;

import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
import org.janastu.heritageapp.geoheritagev2.client.MainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;




public class CaptureAudioActivity extends Activity  implements LocationResultListener, AddressResultListener {

    private static final String TAG =  "CaptureAudioActivity";
    Button play,stop,record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;
    private MapView mapView = null;
    private EditText editTextTitle, editTextDescription;
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);

    String heritageCategory;
    String heritageLanguage;


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(CaptureAudioActivity.this, "Error: " + what + ", " + extra,
                    Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(CaptureAudioActivity.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     setContentView(R.layout.activity_capture_audio);
        setContentView(R.layout.activity_design_capture_audio_screen);

        play=(Button)findViewById(R.id.button3);
        stop=(Button)findViewById(R.id.button2);
        record=(Button)findViewById(R.id.button);



        stop.setEnabled(false);
        play.setEnabled(false);
        Date now = new Date();
        String time2 = now.toString();
        String filename = time2.substring(time2.length()-6,time2.length());

        filename.trim();

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+filename +"_herrec.3gp";;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setOnErrorListener(errorListener);
        myAudioRecorder.setOnInfoListener(infoListener);
        initSpinnerAndMap(savedInstanceState);
        record.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                String address = "Address not found";
                if (currentAddress != null)
                    address = currentAddress.toString();


                if (currentLocation != null) {

                    mapView.addMarker(new MarkerOptions()
                                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                    .title(editTextTitle.getText().toString())
                                    .snippet(address.toString())
                    );
                }


                try {
                    myAudioRecorder.prepare();



                    myAudioRecorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                myAudioRecorder.stop();
                myAudioRecorder.release();


                stop.setEnabled(false);
                play.setEnabled(true);

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });


    }


    public long pushToDB(String title, String description, String address, Double latitude,
                            Double longitude, String consolidatedTags, String urlOrfileLink,
                            String heritageCategory, String heritageLanguage,  Integer mediaType, Long fileSize)

    {

        long id = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage, mediaType, fileSize);
        return id;



    }
    private void initSpinnerAndMap(Bundle savedInstanceState) {


        /** Create a mapView and give it some properties */
        /** Create a mapView and give it some properties */
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.onCreate(savedInstanceState);

        SpriteFactory spriteFactory = mapView.getSpriteFactory();

        locationService = new LocationService();
        locationService.getLocation(getApplicationContext(), this);


        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        Spinner spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        List<String> categories = new ArrayList<String>();
        categories.add("Built Heritage ");
        categories.add("Natural Heritage ");
        categories.add("Intangible Heritage  ");
        categories.add("Spiritual Heritage ");
        categories.add("Lived Experience ");
        categories.add("Organizations ");
        categories.add("Urban Life");
        categories.add("Village Life");
        categories.add("Tamil Heritage");
        categories.add("Colonial Influence");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCategory.setAdapter(dataAdapter);


        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageCategory = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected heritageCategory "+heritageCategory.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


        List<String> languages = new ArrayList<String>();
        languages.add("French ");
        languages.add("Tamil ");
        languages.add("English");

        Spinner spinnerLanguage = (Spinner) findViewById(R.id.spinnerLanguage);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLanguages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        // Drop down layout style - list view with radio button
        dataAdapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerLanguage.setAdapter(dataAdapterLanguages);

        spinnerLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        heritageLanguage = parent.getItemAtPosition(pos).toString();
                        Log.d(TAG, "selected  Language "+heritageLanguage.toString());     //prints the text in spinner item.

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


        ///atore audio

        Button storeButton  = (Button) findViewById(R.id.btnStoreAudio);

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = editTextTitle.getText().toString();
                String description =  editTextDescription.getText().toString();
                if(title == null || title.isEmpty() || description == null || description.isEmpty() )
                {
                    Toast.makeText(getApplicationContext(), " Pls fill in the Title/Description", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double latitude = (  currentLocation.getLatitude()  );
                Double longitude =  ( currentLocation.getLongitude() );

                if(latitude == null || longitude == null)
                {
                    Toast.makeText(getApplicationContext(), "Take Video & Try - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = currentAddress.toString();
                Integer mediaType = AppConstants.AUDIOTYPE;

                String urlOrfileLink;
                if(outputFile != null) {
                    urlOrfileLink = outputFile;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), " Video was not taken - Pl record  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                long fileSize = 0;
                //Log.d(TAG, "video file size"+fileSize);
                String consolidatedTags ="Tag Video";

                long result = pushToDB(  title,   description,   address,   latitude,
                        longitude,   consolidatedTags,   urlOrfileLink,
                        heritageCategory,   heritageLanguage,    mediaType, fileSize);


                if(result == -1)
                {
                    Toast.makeText(getApplicationContext(), "Storage Issue  ", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Stored for Uplload  ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onLocationResultAvailable(Location location) {

        Log.d("location", "location" + location.toString());
        currentLocation = location;

        mapView.setCenterCoordinate(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        mapView.setZoomLevel(15);

        new ReverseGeocodingService(getApplicationContext(), this).execute(location);


    }

    @Override
    public void onAddressAvailable(Address address) {

        currentAddress = address;
        Log.d(TAG, "currentAddress" + address.toString());

    }
}