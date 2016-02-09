package com.github.naofum.zfbitcointrader.Zaif;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ObjCollectionActiveOrderRecord implements Parcelable {
    //Fields
    public ArrayList<ObjOrderRecord> collection;

    //Constructors
    public ObjCollectionActiveOrderRecord(){
        collection = new ArrayList<ObjOrderRecord>();
    }
    public ObjCollectionActiveOrderRecord(final JSONObject jObject) {
        JSONArray names = jObject.names();
        if (names == null || names.length() == 0) return;
        collection = new ArrayList<ObjOrderRecord>();
        try {
            JSONArray jArray = jObject.toJSONArray(names);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject mObj = jArray.getJSONObject(i);
                mObj.put("order_id", names.getString(i));
                ObjOrderRecord item = new ObjOrderRecord(mObj);
                collection.add(item);
            }
        } catch (JSONException e) {
            Log.e("ObjCollectionActiveOrderRecord", "Error parsing JSON");
        }
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("aorecord", collection);
        out.writeBundle(b);
    }
    public static final Creator<ObjCollectionActiveOrderRecord> CREATOR = new Creator<ObjCollectionActiveOrderRecord>() {
        @Override
        public ObjCollectionActiveOrderRecord createFromParcel(Parcel parcel) {
            return new ObjCollectionActiveOrderRecord(parcel);
        }

        @Override
        public ObjCollectionActiveOrderRecord[] newArray(int i) {
            return new ObjCollectionActiveOrderRecord[i];
        }
    };
    public ObjCollectionActiveOrderRecord(Parcel in) {
        Bundle b = in.readBundle(ObjOrderRecord.class.getClassLoader());
        collection = b.getParcelableArrayList("aorecord");

    }
}
