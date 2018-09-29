package com.example.mohit.demoflickrsearch;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Blob;
import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;

public class MainActivity extends BaseActivity {
    MyDatabase myDatabase;
    SQLiteDatabase sqLiteDatabase = null;
    private static final String TAG = "MainActivity";

    PhotoAdapter photoAdapter;
    int page = 1;
    int columns = 2;
    //mohit
    RecyclerView.LayoutManager layoutManager;
    String FlickrQuery_url = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
    String FlickrQuery_tag = "&tags=";
    String FlickrQuery_key = "&api_key=";
    String FlickrApiKey = "4c66bd189e2c891405365e8aab672fbf";
    ArrayList<String> photoList;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    RequestQueue requestQueue;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        textView = (TextView)findViewById(R.id.toolbar);
        textView.setText("Please Search something");

        //mohit
//        FloatingActionButton refresh = findViewById(R.id.fab);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: refresh called");
//                onResume();
//            }
//        });
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        photoList = new ArrayList<>();

        myDatabase = new MyDatabase(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString(FLICKR_QUERY,null).apply();
        columns = sharedPreferences.getInt(COLUMNS,2);
        //mohit
        layoutManager = new GridLayoutManager(this,columns);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(layoutManager);


        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Implementation
                Log.d(TAG,"key = " + key);
                if (key == COLUMNS) {
                    columns = prefs.getInt(COLUMNS,2);
                    Log.d(TAG,"selected option = " + columns);

//                    GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
//                        @Override
//                        public int getSpanSize(int position) {
//
//                            return 3;
//                        }
//                    };

                    ((GridLayoutManager)layoutManager).setSpanCount(columns);
//                    ((GridLayoutManager)layoutManager).setSpanSizeLookup(onSpanSizeLookup);
//                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,columns));

//                    photoAdapter.notifyDataSetChanged();
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

//        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this,recyclerView,this));
//        recycleViewAdapter = new FlickrRecycleViewAdapter(new ArrayList<PhotoData>(),this);
        photoAdapter = new PhotoAdapter(photoList,this);
        recyclerView.addOnScrollListener(new Scroll());
//        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setAdapter(photoAdapter);
//        String url ="https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1";
//        RawJsonData getJson = new RawJsonData(this);
//        getJson.execute(url);
    }


    public class Scroll extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int visibleCount = layoutManager.getChildCount();
            int totalCount = layoutManager.getItemCount();
            int last = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            if (last >= totalCount/1.2) {
                android.util.Log.d(TAG,"last = " + last + "  total = " + totalCount);
                getNetworkQuery();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main,menu);
        Log.d(TAG, "onCreateOptionsMenu: Menu created");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_search){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

//        if(item.getItemId()==R.id.action_settings){
//            Toast.makeText(this,"Options",Toast.LENGTH_SHORT).show();
//            return true;
//        }

        if(item.getItemId()==R.id.two){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putInt(COLUMNS,2).apply();
            columns = 2;
//            ((GridLayoutManager)layoutManager).setSpanCount(columns);
            return true;
        }

        if(item.getItemId()==R.id.three){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putInt(COLUMNS,3).apply();
            columns = 3;
//            ((GridLayoutManager)layoutManager).setSpanCount(columns);
            return true;
        }

        if(item.getItemId()==R.id.four){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putInt(COLUMNS,4).apply();
            columns = 4;
            ((GridLayoutManager)layoutManager).setSpanCount(columns);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: start");

        //mohit GetFlickrJsonData flickrJsonData = new GetFlickrJsonData("en-us",true,this);

//        flickrJsonData.executeOnSameThread("android,oreo");
        page = 1;
        photoList.clear();
        photoAdapter.notifyDataSetChanged();
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String queryTags = sharedPreferences.getString(FLICKR_QUERY,"");
//        if(queryTags != null && queryTags.length()>0){
//            //mohit flickrJsonData.execute(queryTags);
//            Log.d("mohit:","mohit: query tags = " + queryTags);
//            getNetworkQuery(queryTags);
//        }
        getNetworkQuery();
        Log.d(TAG, "onResume: ends");
    }

    public void getNetworkQuery() {

//        boolean internetConnection = isNetworkAvailable();
//        Log.d("mohit:","mohit: inetrnetConnection = " + internetConnection);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryTags = sharedPreferences.getString(FLICKR_QUERY,"");
        if(queryTags == null || queryTags.length() == 0){
            //mohit flickrJsonData.execute(queryTags);
            textView.setText("Please Search something");
            return;
        }
//        String qString = FlickrQuery_url + FlickrQuery_key + FlickrApiKey
//                +  FlickrQuery_tag + queryTags +"&extras=date_taken,owner_name,description";

        changeText("Please Wait!!! Loading Images");

        final String qString = FlickrQuery_url + FlickrQuery_key + FlickrApiKey
                +  FlickrQuery_tag + queryTags +"&format=json" + "&page="+page;

        Log.d(TAG,"getNetworkQuery string = " + qString);

        requestQueue = SingletonRequestQueue.getInstance(this).getRequestQueue();


//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(qString, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONObject photos = response.getJSONObject("photos");
//                    JSONArray photo = photos.getJSONArray("photo");
//                    if (photo.length() > 0) {
//                        JSONObject first = photo.getJSONObject(0);
//
//                        String picOwner = first.getString("owner");
//                        String picID = first.getString("id");
//                        String secretID = first.getString("secret");
//                        String serverID = first.getString("server");
//
//                        Log.d("mohit:","mohit: onResponse array length = " + photo.length() +
//                        " 1st picID = " + picID + " 1st secretID = " + secretID + " 1st server ID = " + serverID);
//
//                    }
//                }catch (Exception e) {
//                    Log.d("mohit:","mohit: onResponse Exception = " + e.toString());
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET,qString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    JSONObject JsonObject = new JSONObject(response);
//                    JSONObject Json_photos = JsonObject.getJSONObject("photos");
//                    JSONArray photo = Json_photos.getJSONArray("photo");
                    Log.d(TAG,"volley onResponse() = ");

                    networkResult(response);

                    try {
                        sqLiteDatabase = myDatabase.getWritableDatabase();
//                        sqLiteDatabase.execSQL("INSERT INTO " + MyDatabase.TABLE_NAME + " VALUES (" + qString
//                                + "," + response + ");");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MyDatabase.COLUMN_ID,qString);
                        contentValues.put(MyDatabase.COLUMN_NAME,response.getBytes());
                        long rows = sqLiteDatabase.insert(MyDatabase.TABLE_NAME,null,contentValues);
                        Log.d(TAG,"value inserted in db for key = " + qString
                        + " rows inserted = " + rows);
                    } catch (Exception e) {
                        Log.d(TAG,"sql excpetion = " + e.getMessage());
                    } finally {
                        if (sqLiteDatabase != null)
                            sqLiteDatabase.close();
                    }

//                    JSONObject searchResults = new JSONObject(response.substring(14, response.length() - 1));
//                    JSONArray photos = searchResults.getJSONObject("photos").getJSONArray("photo");
                    //We have only one photo in this exercise
//                    if (photos.length() > 0) {
//                        for (int i = 0; i < photos.length(); i++) {
//                            JSONObject photoObject = photos.getJSONObject(i);
//
////                            String picOwner = photoObject.getString("owner");
//                            String photoID = photoObject.getString("id");
//                            String farmID = photoObject.getString("farm");
//                            String secretID = photoObject.getString("secret");
//                            String serverID = photoObject.getString("server");
//
//                            String imageUrl = "https://farm"+farmID+".staticflickr.com/"
//                            + serverID + "/" + photoID+"_"+secretID+".jpg";
//
//                            photoList.add(imageUrl);
//                            photoAdapter.notifyItemInserted(photoList.size()-1);
//
//                            //https://www.flickr.com/services/api/misc.urls.html
//
////                            Log.d("mohit:", "mohit: onResponse array length = " + photos.length() +
////                                    " 1st picID = " + picID + " 1st secretID = " + secretID + " 1st server ID = " + serverID);
//
//                        }
//                    }
//                    photoAdapter.notifyDataSetChanged();
//                    page++;

                } catch (Exception e) {
                    Log.d(TAG,"volley exception = " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"volley onErrorResponse = " + error.toString());
                Cursor cursor = null;
                try {
                    sqLiteDatabase = myDatabase.getReadableDatabase();
                    cursor = sqLiteDatabase.query(MyDatabase.TABLE_NAME,
                            new String[]{MyDatabase.COLUMN_NAME}, MyDatabase.COLUMN_ID + " like ?", new String[]{qString}, null, null, null);
                    if (cursor != null && cursor.getCount() > 0) {

                        cursor.moveToFirst();
                        do {
                            byte[] responseBlob = cursor.getBlob(cursor.getColumnIndex(MyDatabase.COLUMN_NAME));
                            String response = new String(responseBlob);
                            networkResult(response);
                            Log.d(TAG,"response from DB = ");
                        } while (cursor.moveToNext());
                    } else {
                        changeText("Internet not available and search result not present");
                    }
                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }

        });

        requestQueue.add(stringRequest);
    }

    private void changeText() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String text = sharedPreferences.getString(FLICKR_QUERY,null);
        changeText(text);
    }

    private void changeText(String text) {
        if (textView != null)
            textView.setText(text);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void networkResult(String response) {
        try {
            JSONObject searchResults = new JSONObject(response.substring(14, response.length() - 1));
            JSONArray photos = searchResults.getJSONObject("photos").getJSONArray("photo");
            //We have only one photo in this exercise
            if (photos.length() > 0) {
                for (int i = 0; i < photos.length(); i++) {
                    JSONObject photoObject = photos.getJSONObject(i);

//                            String picOwner = photoObject.getString("owner");
                    String photoID = photoObject.getString("id");
                    String farmID = photoObject.getString("farm");
                    String secretID = photoObject.getString("secret");
                    String serverID = photoObject.getString("server");

                    String imageUrl = "https://farm" + farmID + ".staticflickr.com/"
                            + serverID + "/" + photoID + "_" + secretID + ".jpg";

                    photoList.add(imageUrl);
                    photoAdapter.notifyItemInserted(photoList.size() - 1);



                    //https://www.flickr.com/services/api/misc.urls.html

//                            Log.d("mohit:", "mohit: onResponse array length = " + photos.length() +
//                                    " 1st picID = " + picID + " 1st secretID = " + secretID + " 1st server ID = " + serverID);
                }
                page++;
                changeText();
            }
        } catch (Exception e) {
            Log.d(TAG,"exception = " + e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString(FLICKR_QUERY,null).apply();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }


}

