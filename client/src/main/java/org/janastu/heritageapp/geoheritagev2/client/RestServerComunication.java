package org.janastu.heritageapp.geoheritagev2.client;

/**
 * Created by Graphics-User on 1/18/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.geojson.FeatureCollection;
import org.janastu.heritageapp.geoheritagev2.client.pojo.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RestServerComunication {

    private static final String GET_ALL_LANGUAGES = "/api/heritageLanguages2";
    private static final String REGISTER_USER = "/api/registerForMobile";
    static String serverUrl;
    static Context context;
    static String userName;
    static String password;



    final static String TAG = "RestServerComunication";

    //final static String UPLOAD_SERVER_URI = "http://10.0.2.2:8080/"; //http:///192.168.56.1/"
    // final static String UPLOAD_SERVER_URI = "http://192.168.56.1:8080"; //http:///192.168.56.1/"
    //final static String UPLOAD_SERVER_URI = "http://192.168.1.19:8082/heritageweb/"; //http:///192.168.56.1/"
   // final static String UPLOAD_SERVER_URI = "http://192.168.1.22:8080/"; //http:///192.168.56.1/"
    final static String UPLOAD_SERVER_URI = "http://pondy.openrun.com:8080/heritageweb/";
    //final static String UPLOAD_SERVER_URI = "http://pondy.openrun.com:8080/heritageweb/";
    private static String SIGN_IN = "/api/imageGeoTagHeritageFromMobile";
    private static String LOG_IN = "/api/authenticate";
    ;
    private static String GET_IMAGE_FOR_ID = "/api/imageGeoTagHeritageEntitysPost/1";

    private static final String GET_ALLCATEGORY = "/api/heritageCategorys2";

    private static final String GET_ALL_TAGS = "/api/allGeoTagHeritageEntitysGeoJson";

    private static final String UPLOAD_CREATE_MEDIA = "api/createAnyMediaGeoTagHeritageFromMobile";
    public RestServerComunication(Context context) {
        this.context = context;
    }

    public static void setContext(Context c)
    {context = c; }

    public static void init()
    {

        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);

        userName  = settings.getString(MainActivity.PREFS_USERNAME , "");
        password = settings.getString(MainActivity.PREFS_PASSWORD , "");
        String currentToken = settings.getString(MainActivity.PREFS_ACCESS_TOKEN , "");

        Log.d(TAG, "SharedPreferences userName"+userName);
        Log.d(TAG, "SharedPreferences password"+password);
        Log.d(TAG, "SharedPreferences currentToken"+currentToken);
    }

    public static Picture uploadPicture(String accessToken, String fileToUpload) throws RestClientException {
        RestTemplate rest = new RestTemplate();
        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        formConverter.setCharset(Charset.forName("UTF8"));

        rest.getMessageConverters().add(formConverter);

        String uploadUri = UPLOAD_SERVER_URI;

        HashMap<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("accessToken", accessToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("application/json")));

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("picture", new FileSystemResource(fileToUpload));
        Picture response = rest.postForObject(uploadUri, parts, Picture.class, urlVariables);
        return response;
    }


    public static MResponseToken registerUser(String username, String password, String emailId, String residentstatus, String agestatus, String specialmessage)

    // public static RegisterResponse registerUser()
    {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", username);
        map.add("password", password);
        if (residentstatus.contains("Resident"))
            map.add("residentstatus", "true");
        else
            map.add("residentstatus", "false");
        map.add("agestatus", agestatus + "");
        //emailId
        map.add("emailId", emailId);
        map.add("specialmessage", specialmessage);
        //

        //Log.d(TAG, "logging with "+"username"+userName + "password"+password);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + REGISTER_USER);

        URI url = builder.build().toUri();


        MResponseToken response = null;
        try {

            response = restTemplate.postForObject(url.toString(), request, MResponseToken.class);
            Log.d(TAG, "register response" + response);

        } catch (Exception e) {

            MResponseToken response2 = new MResponseToken();
            response2.setCode(RestReturnCodes.EXCEPTION);
            response2.setStatus("NOTOK");
            response2.setMessage("EXCEPTION" + e.getMessage());
            return response2;

            //   response.setToken("LOGFAIL - "+e);
        }

        return response;
    }

    public static LoginResponse authenticate(String userNameVar, String passwordVar) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", userNameVar);
        map.add("password", passwordVar);
        Log.d(TAG, "logging with " + "username" + userNameVar + "password" + passwordVar);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + LOG_IN);

        URI url = builder.build().toUri();

        LoginResponse response = null;
        try {

            response = (LoginResponse) restTemplate.postForObject(url, request, LoginResponse.class);
            return response;

        } catch (Exception e) {

            response = new LoginResponse();
            response.setToken("LOGFAIL - " + e);
        }

        return response;


    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageCategory[] getAllCategories(Context c)

    {

        HeritageCategoryListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALLCATEGORY);

        URI url = builder.build().toUri();

        HeritageCategory[] array = (HeritageCategory[]) restTemplate.postForObject(url, request, HeritageCategory[].class);


        Log.d(TAG, "From Server received category objects: - " + array.length);

        return array;

    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static FeatureCollection getAllFeatures(Context c)

    {

        HeritageCategoryListPojo heritageCategoryListPojo;
        init();

        Log.d(TAG, "getAllFeatures userName"+ userName);
        Log.d(TAG, "getAllFeatures password"+ password);
        LoginResponse response = authenticate(userName, password);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_TAGS);

        URI url = builder.build().toUri();

        FeatureCollection fc = (FeatureCollection) restTemplate.postForObject(url, request, FeatureCollection.class);


        Log.d(TAG, "From Server received succesds");
        try {
            Log.d(TAG, "From Server received category objects: - " + fc.toString());
        } catch (Exception e) {
            Log.e("ERROR   COLLECTION", e.toString());
        }


        return fc;

    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static HeritageLanguage[] getAllLanguages(Context c)

    {

        HeritageCategoryListPojo heritageCategoryListPojo;
        LoginResponse response = authenticate( userName,   password);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_ALL_LANGUAGES);

        URI url = builder.build().toUri();

        HeritageLanguage[] array = (HeritageLanguage[]) restTemplate.postForObject(url, request, HeritageLanguage[].class);


        Log.d(TAG, "From Server received HeritageLanguage objects: - " + array.length);

        return array;

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static ImageGeoTagHeritageEntityDTO getImageDetailsFromServer(Context c, int id) {


        ImageGeoTagHeritageEntityDTO imageGeoTagHeritageEntityDTO = new ImageGeoTagHeritageEntityDTO();


        //AUTHNTICATE AND CALL API;

        LoginResponse response = authenticate( userName,   password);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Set headers to include multiform


////////////////////////////////////


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("x-auth-token", response.getToken());

        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);


        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + GET_IMAGE_FOR_ID);

        URI url = builder.build().toUri();

        imageGeoTagHeritageEntityDTO = (ImageGeoTagHeritageEntityDTO) restTemplate.postForObject(url, request, ImageGeoTagHeritageEntityDTO.class);


        Log.d(TAG, "From Server : - " + imageGeoTagHeritageEntityDTO.toString());


        return imageGeoTagHeritageEntityDTO;

    }

    /*
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)

    public static  boolean postSignInDataToserver1(Context c, String title, String description, String category, String language,  File mpictureFile    ) {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI +  SIGN_IN);


        URI url = builder.build().toUri();

        // Set headers to include multiform

        HttpHeaders header = new HttpHeaders();

        header.set("Connection", "Close");

        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {

            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("title", "tirtler");
            parts.add("name", "aaaa");



            parts.add("picture", new FileSystemResource(mpictureFile));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            restTemplate.getMessageConverters().add(
                    new FormHttpMessageConverter());

            HttpEntity<Object> request = new HttpEntity<Object>(parts, header);        //

            Log.d("", "The reaponse is: "+restTemplate.postForLocation(url, request));

        } catch (Exception e) {
            Log.d("Picture", "UPLOAD EXCEPTION  " + e);
            return false;

        }
        Log.d("Picture", "UPLOAD PHOTO DONE");
        return true;

    }
*/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean postSignInDataToserver2(Context c, String title, String description, String category, String language, File mpictureFile, String latitude, String longitude, String mediatype) {


//UPLOAD_CREATE_MEDIA
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(UPLOAD_SERVER_URI + UPLOAD_CREATE_MEDIA);


        URI url = builder.build().toUri();

        // Set headers to include multiform

        HttpHeaders header = new HttpHeaders();

        header.set("Connection", "Close");

        header.setContentType(MediaType.MULTIPART_FORM_DATA);


        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        header.setAccept(mediaTypeList);

        try {

            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("title", title);
            parts.add("description", description);
            parts.add("category", category);
            parts.add("language", language);
            parts.add("latitude", latitude);
            parts.add("longitude", longitude);
            parts.add("mediatype", mediatype + "");


            parts.add("picture", new FileSystemResource(mpictureFile));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            restTemplate.getMessageConverters().add(
                    new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(
                    new StringHttpMessageConverter());
            HttpEntity<Object> request = new HttpEntity<Object>(parts, header);
            //

            Log.d("", "Calling REST to upload file");

            MediaResponse response = null;


                response = restTemplate.postForObject(url, request, MediaResponse.class);

                //Log.d("", "The reaponse is: "+restTemplate.postForLocation(url, request));

            Log.d(TAG,response.toString());

            } catch (Exception e) {
                Log.d("Picture", "UPLOAD EXCEPTION  " + e);
                return false;

            }
            Log.d("Picture", "UPLOAD PHOTO DONE");
            return true;


    }


}
