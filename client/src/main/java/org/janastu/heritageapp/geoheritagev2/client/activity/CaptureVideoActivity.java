package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import android.widget.*;
import org.janastu.heritageapp.geoheritagev2.client.MainActivity;
import org.janastu.heritageapp.geoheritagev2.client.pojo.AppConstants;
import org.janastu.heritageapp.geoheritagev2.client.services.ReverseGeocodingService;



import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.services.AddressResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationResultListener;
import org.janastu.heritageapp.geoheritagev2.client.services.LocationService;


import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

public class CaptureVideoActivity extends AppCompatActivity   implements LocationResultListener, AddressResultListener {

    private static final String TAG = "CaptureVideoActivity" ;
    LocationService locationService;
    private Address currentAddress;
    private Location currentLocation;
    private MapView mapView = null;
    private EditText editTextTitle, editTextDescription;
    File mediaFile;
    private String recordFileName="heritagevid";
    GeoTagMediaDBHelper geoTagMediaDBHelper = new GeoTagMediaDBHelper(this);

    String heritageCategory  ;
    String heritageLanguage  ;

    private static final int VIDEO_CAPTURE = 101;
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_capture_video);
        setContentView(R.layout.activity_design_capture_video_screen);
        initSpinnerAndMap(savedInstanceState);
    }

    private void initSpinnerAndMap(Bundle savedInstanceState) {


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

        Button storeButton  = (Button) findViewById(R.id.btnStoreVideo);

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
                /// if curr
                if(currentLocation == null)
                {

                    Toast.makeText(getApplicationContext(), " Enable GPS - GPS Co-ordinates missing ", Toast.LENGTH_SHORT).show();
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

                Integer mediaType = AppConstants.VIDEOTYPE;
                String urlOrfileLink;
                if(mediaFile != null) {
                    urlOrfileLink = mediaFile.getAbsolutePath();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), " Video was not taken - Pl record  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                long fileSize = mediaFile.length();
                Log.d(TAG, "video file size"+fileSize);
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


    public long pushToDB(String title, String description, String address, Double latitude,
                            Double longitude, String consolidatedTags, String urlOrfileLink,
                            String heritageCategory, String heritageLanguage,  Integer mediaType, Long fileSize)

    {

        long result = geoTagMediaDBHelper.insertWaypoint(title, description, address, latitude, longitude, consolidatedTags, urlOrfileLink, heritageCategory, heritageLanguage, mediaType, fileSize);
        Log.d("Inserted"  , result +"Insert Video Way Point"+ fileSize + urlOrfileLink);
        return result;



    }

    public void onPlayLocalVideo(View v) {
        VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
     /*   mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.small_video));*/

        mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+recordFileName);
        videoUri = Uri.fromFile(mediaFile);

        mVideoView.setVideoURI(videoUri);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.start();
    }

    public void onStreamVideo(View v) {
        final VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }



    public void onRecordVideo(View v) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    ;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            recordFileName = editTextTitle.getText().toString().trim()+"_H_V_"+timeStamp+".mp4";
            recordFileName.trim();

            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+recordFileName);
            videoUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent, VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
                mVideoView.setVideoURI(videoUri);
                mVideoView.setMediaController(new MediaController(this));
                mVideoView.requestFocus();
                mVideoView.start();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",  Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",  Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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
