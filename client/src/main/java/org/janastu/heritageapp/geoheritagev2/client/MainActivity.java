package org.janastu.heritageapp.geoheritagev2.client;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Sprite;
import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;*/

import org.geojson.Point;
import org.janastu.heritageapp.geoheritagev2.client.pojo.*;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.geojson.*;
import org.janastu.heritageapp.geoheritagev2.client.activity.*;
import org.json.JSONObject;

import java.util.*;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_PASSWORD = "password";
    public static final String PREFS_ACCESS_TOKEN = "access_token";
    private static final String TAG = "MainActivity";

    HeritageCategory[] heritageCategories;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     * Fragment managing the behaviors, iteractions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //  private MapView mapView = null;

    private MapView mMapView;
    private IMapController mMapController;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    View rootView;
    private String titlePopup, descPopup, urlPopup;
    private List<String> heritageCategoriesStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();



        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        /** Create a mapView and give it some properties */

        try {


   /*   mapView = (MapView)findViewById (R.id.mapview);

      rootView = mapView.getRootView();
      mapView.setStyleUrl(Style.MAPBOX_STREETS);
      mapView.setCenterCoordinate(new LatLng(11.931285486695971, 79.83333349227905));
      mapView.setZoomLevel(15);
      mapView.onCreate(savedInstanceState);


      MapView.InfoWindowAdapter i = new MapView.InfoWindowAdapter() {
          @Nullable
          @Override
          public View getInfoWindow(@NonNull Marker marker) {

              View popup = getLayoutInflater().inflate(R.layout.popup, null );
              TextView  t = (TextView)popup.findViewById(R.id.title);
              t.setText(marker.getTitle());
              Log.d(TAG, "getInfoWindow");
              return null;
          }
      };
      mapView.setInfoWindowAdapter(i);

      mapView.setOnInfoWindowClickListener(new MapView.OnInfoWindowClickListener() {
          @Override
          public boolean onMarkerClick(@NonNull Marker marker) {

              Log.d(TAG, "clclclcl");
              return false;
          }
      });

      SpriteFactory spriteFactory = mapView.getSpriteFactory();

      Drawable drawable = ContextCompat.getDrawable( getApplicationContext(), R.drawable.ic_drawer);
      Sprite icon = spriteFactory.fromDrawable(drawable);

      //Sprite icon = spriteFactory.fromDrawable(drawable);


       new DrawGeoJSON().execute();*/
            heritageCategoriesStringList = new ArrayList<String>();
            heritageCategoriesStringList.clear();
            heritageCategoriesStringList.add("Built Heritage");
            heritageCategoriesStringList.add("Natural Heritage");
            heritageCategoriesStringList.add("Intangible Heritage  ");
            heritageCategoriesStringList.add("Spiritual Heritage ");
            heritageCategoriesStringList.add("Lived Experience ");
            heritageCategoriesStringList.add("Organizations ");
            heritageCategoriesStringList.add("Urban Life");
            heritageCategoriesStringList.add("Village Life");
            heritageCategoriesStringList.add("Tamil Heritage");
            heritageCategoriesStringList.add("Colonial Influence");

            mMapView = (MapView) findViewById(R.id.mapview2);
            // mMapView.setTileSource(TileSourceFactory.MAPNIK);
            mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);

            //   mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            mMapController = mMapView.getController();
            mMapController.setZoom(13);


            GeoPoint gPt = new GeoPoint(11.857674384942547, 79.79164123535155);
            mMapController.setCenter(gPt);



            new DefaultResourceProxyImpl(this);

            new DrawGeoJSONOSM().execute();

            //   new DrawGeoJSONOSM().execute();

        } catch (Exception e) {
            Log.e(TAG, "ERRR mapsss" + e);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();


        Log.d(TAG, "cliekced" + position);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.action_capture_image) {
            Intent i = new Intent(getApplicationContext(), CaptureImageActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_capture_audio) {
            Intent i = new Intent(getApplicationContext(), CaptureAudioActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_capture_video) {
            Intent i = new Intent(getApplicationContext(), CaptureVideoActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_upload) {
            Intent i = new Intent(getApplicationContext(), ListFileUpLoadActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnPlay(View v) {
        Log.d(TAG, "clicked play");

        TextView map_popup_header = (TextView) v.findViewById(R.id.textViewDesc); //textViewDesc
        //  map_popup_header.setText(item.getTitle());

        TextView map_popup_body = (TextView) v.findViewById(R.id.textViewUrl);
        Log.d(TAG, "clicked play title " + titlePopup);
        Log.d(TAG, "clicked play desc" + descPopup);
        Log.d(TAG, "clicked play map_popup_body url " + urlPopup);
        //   map_popup_body_cycle.setText(item.getUid());

        //set up text

        //   map_popup_body.setText(item.getSnippet());

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    //////


    ///end inner class
    private class GetCategoryTask extends AsyncTask < String, Void, HeritageCategory[] > {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage(getResources()
                    .getString(R.string.login_progress_signing_in));
            mProgressDialog.show();
        }

        @Override
        protected HeritageCategory[] doInBackground(String...params) {
            User registered = null;
            HeritageCategory[] r = null;
            try {

                RestServerComunication.setContext(getApplicationContext());

                r = RestServerComunication.getAllCategories(getApplicationContext());

            } catch (Exception e) {
                Log.e("Get Category Task", "Error Retreiving  in: " + e.getMessage());
            }
            return r;
        }

        @Override
        protected void onPostExecute(HeritageCategory[] result) {
            mProgressDialog.dismiss();
            if (result != null) {
                heritageCategories = result;

                Toast.makeText(getApplicationContext(),
                        "Category Retreival Successful" + " ID: ", Toast.LENGTH_LONG).show();


            } else {
                heritageCategories = new HeritageCategory[5];
                heritageCategories[0] = new HeritageCategory();
                Toast.makeText(getApplicationContext(), "Category Retreival Failure ", Toast.LENGTH_LONG).show();
            }

    /*  textviewContent = new String[heritageCategories.length];
      for (int i = 0 ; i <heritageCategories.length;++i) {
          Log.d("ARRAY", "" + i + heritageCategories[i].toString());
          textviewContent[i] =  heritageCategories[i].getCategoryName();
      }*/
        }
    }
    //

    private class DrawGeoJSON extends AsyncTask < Void, Void, List < MarkerInfo >> {
        @Override
        protected List < MarkerInfo > doInBackground(Void...voids) {

            ArrayList < MarkerInfo > points = new ArrayList < MarkerInfo > ();

            try {
                // Load GeoJSON file
                JSONObject json = null; //
                FeatureCollection fc = RestServerComunication.getAllFeatures(getApplicationContext());

                List < Feature > fList = fc.getFeatures();

                for (Feature temp: fList) {
                    //  Log.d(TAG, " properties " + temp.getProperties().toString() );

                    Point p = (Point) temp.getGeometry();
                    LngLatAlt coordinatesFromRest = p.getCoordinates();
     /*  LatLng localLatLng = new LatLng();

       localLatLng.setLongitude(coordinatesFromRest.getLongitude());
       localLatLng.setLatitude(coordinatesFromRest.getLatitude());
       MarkerInfo m = new MarkerInfo();
       m.setLatLngMapBoxMobile(localLatLng);
       Map<String, Object> properties = temp.getProperties();
       Object p1 = properties.get("description");
        String   p11 = (String )p1;
           m.setDescription(p11);;

       Object p2 = properties.get("title");
       String   p22 = (String )p2;
       m.setTitle(p22);;

       Object p3 = properties.get("marker-color");
       String   p33 = (String )p3;
       m.setMarkerColor( p33);;
       points.add(m);*/


                }

            } catch (Exception e) {
                Log.e(TAG, "Exception GeoJSON: " + e);
            }
            return points;
        }

        @Override
        protected void onPostExecute(List < MarkerInfo > points) {
            super.onPostExecute(points);

            if (points.size() > 0) {


                for (MarkerInfo p: points) {

                    // Draw Points on MapView
     /* SpriteFactory spriteFactory = mapView.getSpriteFactory();
      Drawable drawable = ContextCompat.getDrawable( getApplicationContext(), R.drawable.ic_drawer );
      Sprite icon = spriteFactory.fromDrawable(drawable);


      LatLng langLoatVar = p.getLatLngMapBoxMobile();
      MarkerOptions a = new MarkerOptions()
              .position(langLoatVar)
              .title(p.getTitle())
              .snippet(p.getDescription() );

      Marker m  = a.getMarker();
      m.isInfoWindowShown();


      mapView.addMarker(new MarkerOptions()
              .position(langLoatVar)
              .title(p.getTitle())
              .snippet(p.getDescription() )




      );*/
                }
            }
        }
    }


    // OSM Droid
    List < Feature > fList;
    ArrayList < OSMMarkerInfo > points;

    private class DrawGeoJSONOSM extends AsyncTask < Void, Void, List < OSMMarkerInfo >> {
        @Override
        protected List < OSMMarkerInfo > doInBackground(Void...voids) {

            points  = new ArrayList < OSMMarkerInfo > ();

            try {
                // Load GeoJSON file
           //

                RestServerComunication.setContext(getApplicationContext());

                FeatureCollection fc = RestServerComunication.getAllFeatures(getApplicationContext());

                fList = fc.getFeatures();

                for (Feature temp: fList) {
                     Log.d(TAG, " properties " + temp.getProperties().toString() );

                    Point p = (Point) temp.getGeometry();
                    LngLatAlt coordinatesFromRest = p.getCoordinates();
                    OSMMarkerInfo m = new OSMMarkerInfo();
                    m.setLatLngOSM(coordinatesFromRest);
                    Map < String, Object > properties = temp.getProperties();
                    Object p1 = properties.get("description");
                    String p11 = (String) p1;
                    m.setDescription(p11);

                    Object p2 = properties.get("title");
                    String p22 = (String) p2;
                    m.setTitle(p22);

                    Object markerColorJson = properties.get("marker-color");
                    String markerColorJsonStr = (String) markerColorJson;
                    m.setMarkerColor(markerColorJsonStr);

                    Object urlJson = properties.get("url");
                    String urlJsonStr = (String) urlJson;
                    m.setUrl(urlJsonStr);

                    Object type = properties.get("mediatype");//mediatype
                    String typeStr = (String) type;
                    Log.d(TAG, "setting mediatype typeStr "+typeStr);
                    m.setMediaType(typeStr);


                    Object category = properties.get("category");
                    String categoryStr = (String) category;
                    Log.d(TAG, "setting category "+categoryStr);
                    m.setCategory(categoryStr);

                    Object language = properties.get("language");
                    String languageStr = (String) language;
                    m.setLanguauge(languageStr);


                    points.add(m);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception GeoJSON: " + e);
            }
            return points;
        }

        @Override
        protected void onPostExecute(List < OSMMarkerInfo > points) {
            super.onPostExecute(points);

            redrawPoints();
        }
    }



    ////
    public List<OSMMarkerInfo> listContainsString(List<OSMMarkerInfo> list, List<String> categories)
    {
        List<OSMMarkerInfo> filteredList = new ArrayList<OSMMarkerInfo>();

        Iterator<OSMMarkerInfo> iter = list.iterator();
        while(iter.hasNext())
        {
            OSMMarkerInfo s = iter.next();
            Log.d(TAG,"OSMMarkerInfo"+s.toString());

            if(s!= null ) {
                Log.d(TAG, "seacrhing for "+s.getCategory() +categories.toString());
                if (categories.contains(s.getCategory())) {
                    //return true;
                    Log.d(TAG, "adding for "+s.getCategory());
                    filteredList.add(s);
                }
            }
        }

        return filteredList;

    }

    public void redrawPoints()
    {

        //check if there exists any item in the checked array list -

        // for (checkedCategory : checkedCategoryList)
        /*{}


         */



      
       //
        // List<OSMMarkerInfo> result = listContainsString( points,  heritageCategoriesStringList );

        List<OSMMarkerInfo> result = points;

        if (result.size() > 0) {
            Log.d(TAG, "Redrawing points");

            for (OSMMarkerInfo p: result) {

                showHeritagePoint(p);

                ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
                mMapView.getOverlays().add(myScaleBarOverlay);

                //                    new PathOverlay(Color.RED, (ResourceProxy) this);
                //   mMapController.setCenter (geoPointC);
                mMapView.invalidate();
            }
        }
    }

    public static String replaceSpa(String str) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                strBuffer.append("%20");
            } else {
                strBuffer.append(str.charAt(i));
            }
        }
        return strBuffer.toString();
    }

    public void showHeritagePoint(OSMMarkerInfo m) {

        Drawable heritageLocationDrawable = null;
        String mediaType = m.getMediaType();
        heritageLocationDrawable = this.getResources().getDrawable(
                R.drawable.mapicon);
        if(mediaType != null) {


            if (mediaType.contains("AUDIO")) {

                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapicon);
            } else if (mediaType.contains("VIDEO")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapvideoicon);
            } else if (mediaType.contains("IMAGE")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapimageicon);
            } else if (mediaType.contains("TEXT")) {
                heritageLocationDrawable = this.getResources().getDrawable(
                        R.drawable.mapicontext);
            }
        }
        else
        {
            heritageLocationDrawable = this.getResources().getDrawable(
                    R.drawable.mapicon);
        }


        //based on the marker-color

        MapItemizedOverlay itemizedoverlayForRestaurant = new MapItemizedOverlay(
                heritageLocationDrawable, this);

        m.getLatLngOSM().getLatitude();;
        GeoPoint myPoint1 = new GeoPoint(m.getLatLngOSM().getLatitude(), m.getLatLngOSM().getLongitude());

        // OverlayItem overlayitem2 = new OverlayItem(m.getTitle(),m.getDescription(),m.getUrl() , myPoint1);

        String url = m.getUrl();
        String encodedUrl = null;

        encodedUrl = replaceSpa(url);

        OverlayItem overlayitem2 = new OverlayItem("Title :- " + m.getTitle(), "Description :- " + m.getDescription(), "Url :- " + encodedUrl, myPoint1);

        itemizedoverlayForRestaurant.addOverlay(overlayitem2);

        mMapView.getOverlays().add(itemizedoverlayForRestaurant);
    }

    public class MapItemizedOverlay extends ItemizedOverlay < OverlayItem > {
        private ArrayList < OverlayItem > mOverlays = new ArrayList < OverlayItem > ();
        private Context mContext;

        public MapItemizedOverlay(Drawable defaultMarker, Context context) {
            // super(boundCenterBottom(defaultMarker));
            super(defaultMarker, new DefaultResourceProxyImpl(context));
            mContext = context;
        }

        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        @Override
        public int size() {
            return mOverlays.size();
        }

        @Override
        protected boolean onTap(int index) {
            OverlayItem item = mOverlays.get(index);

            Log.d("Title", item.getTitle());
            Log.d("Snippet", item.getSnippet());
            Log.d("Id", item.getUid());

            titlePopup = item.getTitle();
            descPopup = item.getSnippet();
            urlPopup = item.getUid();
            //set up dialog
            Dialog dialog = new Dialog(mContext);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.popupmap);
            //dialog.setTitle("This is my custom dialog box");

            dialog.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!
            item.getUid(); //textViewTitle

            TextView map_popup_body_cycle = (TextView) dialog.findViewById(R.id.textViewTitle);
            map_popup_body_cycle.setText(item.getUid());

            //map_popup_body_cycle.setText(Html.fromHtml("<a href="+item.getSnippet()+">"+item.getUid()+"</a>"));

            //set up text
            TextView map_popup_header = (TextView) dialog.findViewById(R.id.textViewDesc); //textViewDesc
            map_popup_header.setText(item.getTitle());

            TextView map_popup_body = (TextView) dialog.findViewById(R.id.textViewUrl);
            map_popup_body.setText(item.getSnippet());

            map_popup_body.setText(Html.fromHtml(item.getSnippet()));
            Linkify.addLinks(map_popup_body, Linkify.ALL);
            map_popup_body.setMovementMethod(LinkMovementMethod.getInstance());

            //now that the dialog is set up, it's time to show it
            dialog.show();

            return true;
        }



        @Override
        public boolean onSnapToItem(int i, int i1, android.graphics.Point point, IMapView iMapView) {
            return false;
        }
    }








}