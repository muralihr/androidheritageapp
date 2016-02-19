package org.janastu.heritageapp.geoheritagev2.client.activity;

/**
 * Created by Graphics-User on 1/22/2016.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.db.GeoTagMediaDBHelper;
import org.janastu.heritageapp.geoheritagev2.client.pojo.DownloadInfo;

import java.io.File;




/**
 * Simulate downloading a file, by increasing the progress of the FileInfo from 0 to size - 1.
 */
public class FileDownloadTask extends AsyncTask<Void, Integer, Void> {
    private static final String    TAG = FileDownloadTask.class.getSimpleName();
    final DownloadInfo mInfo;
    private final Context context;
    GeoTagMediaDBHelper geoTagMediaDBHelper;
    public FileDownloadTask(Context c, DownloadInfo info) {
        mInfo = info;
        context = c;
        geoTagMediaDBHelper = new GeoTagMediaDBHelper(c);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mInfo.setProgress(values[0]);
        ProgressBar bar = mInfo.getProgressBar();
        if(bar != null) {
            bar.setProgress(mInfo.getProgress());
            bar.invalidate();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "Starting upload for " + mInfo.getUrlOrfileLink());
        mInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);

        File uploadFile = new File(mInfo.getUrlOrfileLink());



        String title = mInfo.getTitle();
        String description = mInfo.getDescription();
        String category  = mInfo.getHeritageCategory();
        String language = mInfo.getHeritageLanguage();

        String latitude  =  mInfo.getLatitude();
        String longitude  = mInfo.getLongitude();
        String mediatype = mInfo.getMediaType();

        boolean result = RestServerComunication.postSignInDataToserver2( context, title, description,category,language,uploadFile,latitude,longitude,mediatype);



        if(result == false) {
            mInfo.setDownloadState(DownloadInfo.DownloadState.FAILED);
            Log.d(TAG, "Updating the status - download failure"+ mInfo.getTitle() + mInfo.getId());
        }
        else {
            mInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);
            Log.d(TAG, "Updating the status - download success"+ mInfo.getTitle() + mInfo.getId());
            geoTagMediaDBHelper.updateWaypointToDownloaded(mInfo.getTitle(), mInfo.getId());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        //mInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);

        //delete the same from the DB;
        //
        if(mInfo.getDownloadState() == DownloadInfo.DownloadState.COMPLETE)
        {

        }
    }

    @Override
    protected void onPreExecute() {
        mInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);
    }

}
