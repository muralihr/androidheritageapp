package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.widget.ListView;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;


public class ListFileUpLoadActivity extends Activity {


    private static final String TAG = "ListFileUpLoadActivity";
    GeoTagMediaDBHelper geoTagMediaDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_file_up_load);

        geoTagMediaDBHelper = new GeoTagMediaDBHelper(getApplicationContext());


        ListView listView = (ListView) findViewById(R.id.downloadListView);

        List<DownloadInfo> downloadInfo  =  getAllDownloadInfo();

        listView.setAdapter(new DownloadInfoArrayAdapter(getApplicationContext(), R.id.downloadListView, downloadInfo));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    public List<DownloadInfo> getAllDownloadInfo() {

    List<DownloadInfo> downloadInfo = new ArrayList<DownloadInfo>();

        String name;
        Cursor cursor = geoTagMediaDBHelper.getAllWaypoint();

        int count = geoTagMediaDBHelper.numberOfRows();

        for (int i = 0 ; i <= count ; i++)
        {

            Cursor c = geoTagMediaDBHelper.getWaypoint(i);

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                String filename = cursor.getString(cursor.getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_NAME));
                Log.d(TAG, "file name" + filename);
            }
        }

        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {


                Integer id = cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_ID));

                String title = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_TITLE));

                String latitude =  cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LATITUDE));
                String longitude =  cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LONGITUDE));
                String urlOrfileLink = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_NAME));

                String desc = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_DESCRPITION));
                String heritageCategory = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_CATEGORY));
                String heritageLanguage = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_LANGUAGE));

                String  media_type = cursor.getString(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_MEDIA_TYPE));

                Integer fileSize = cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_FILE_SIZE));

                boolean fileUploadstatus = (cursor.getInt(cursor
                        .getColumnIndex(GeoTagMediaDBHelper.DATA_COLUMN_UPLOAD_STATUS) )== 1);


                DownloadInfo d =  new DownloadInfo( id, title,   desc,   latitude,   longitude ,  urlOrfileLink ,   media_type,fileUploadstatus );
                d.setHeritageCategory(heritageCategory);
                d.setHeritageLanguage(heritageLanguage);
                d.setmFileSize(fileSize);

                downloadInfo.add(d);
                cursor.moveToNext();
            }
        }
    return downloadInfo;
    }

}
