package com.example.faizrehman.geofence_broadcast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faizrehman on 1/13/17.
 */

public class Geofence_Sqlite extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofence.db";
    private static final String TABLE_NAME = "geofences";
    private static final String COLUMN_ID = "_id";
    private static final String GEOFENCES_LONGITUDE = "geofence_Long";
    private static final String GEOFENCES_LATITUDE = "geofence_Lat";
    private static final String GEOFENCES_RADIUS = "geofence_Rad";
    private static final String GEOFENCES_KEY = "geofence_Key";
    private static final String GEOFENCE_PLACE = "geofence_Place";




    private static final String COLUMN_GEONAME = "geofence";


    public Geofence_Sqlite(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = String.format("create table %s (%s INTEGER primary key AUTOINCREMENT,%s TEXT,%s REAL,%s REAL,%s REAL,%s TEXT)",TABLE_NAME,COLUMN_ID,GEOFENCES_KEY,GEOFENCES_LONGITUDE,GEOFENCES_LATITUDE,GEOFENCES_RADIUS,GEOFENCE_PLACE);


        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void Addvalue(Model model) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GEOFENCES_KEY, model.getFenceKey());
        values.put(GEOFENCES_LONGITUDE,model.getLongitude());
        values.put(GEOFENCES_LATITUDE,model.getLatitude());
        values.put(GEOFENCES_RADIUS,model.getRadius());
        values.put(GEOFENCE_PLACE,model.getPlace());


        db.insert(TABLE_NAME, null, values);
        db.close();

    }
    public List<Model> getData(){
        List<Model> arrayList = new ArrayList<Model>();

        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("select %s,%s,%s,%s,%s,%s from %s order by %s", COLUMN_ID, GEOFENCES_KEY, GEOFENCES_LATITUDE,GEOFENCES_LONGITUDE,GEOFENCES_RADIUS,GEOFENCE_PLACE,TABLE_NAME, COLUMN_ID);
        Cursor cursor = db.rawQuery(sql, null);


        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String geofenceKey = cursor.getString(1);
            double geofenceLat = cursor.getDouble(2);
            double geofenceLong = cursor.getDouble(3);
            float geofenceRadius = cursor.getFloat(4);
            String place = cursor.getString(5);
            // String checkBoxx = cursor.getString(3);
            Log.d("ID is ", "Msg:" + id+geofenceKey+geofenceLat+geofenceLong+geofenceRadius);
            arrayList.add(new Model(geofenceLong,geofenceLat,geofenceRadius,geofenceKey,place));

        }
        db.close();
        return arrayList;
    }

//    public void UpdateFence(String key){
//
//            ContentValues values = new ContentValues();
//            values.put(PRODUCT_QUANTITY,String .valueOf(quantity));
//
//            Log.d("tag",String .valueOf(quantity));
//            this.getWritableDatabase().update(PRODUCT_TABLE,values,PRODUCT_ID+"="+pos,null);
//    }

    public void deleteGeofence(String pos){
        this.getWritableDatabase().delete(TABLE_NAME,GEOFENCES_KEY+"="+pos,null);
    }
}
