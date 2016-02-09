package com.github.naofum.zfbitcointrader.Zaif;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjCollectionBalanceCoin implements Parcelable{

    public ObjBalanceCoin BTC;
    public ObjBalanceCoin JPY;

    public ObjCollectionBalanceCoin(){
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        JPY = new ObjBalanceCoin("Yen", "JPY");
    }
    public ObjCollectionBalanceCoin(JSONObject jObj) {
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        JPY = new ObjBalanceCoin("Yen", "JPY");
        try {
            BTC.setBalance((float)jObj.getJSONObject("return").getJSONObject("funds").getDouble("btc"));
            JPY.setBalance((float) jObj.getJSONObject("return").getJSONObject("funds").getDouble("jpy"));
        } catch (JSONException e) {
            Log.e("ObjCollectionBalanceCoin", "Error parsing JSON");
        }
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeParcelable(BTC,i);
        out.writeParcelable(JPY,i);
    }
    public ObjCollectionBalanceCoin(Parcel in){
        this.BTC = in.readParcelable(ObjBalanceCoin.class.getClassLoader());
        this.JPY = in.readParcelable(ObjBalanceCoin.class.getClassLoader());
    }

    public static final Parcelable.Creator<ObjCollectionBalanceCoin> CREATOR = new Parcelable.Creator<ObjCollectionBalanceCoin>() {
        @Override
        public ObjCollectionBalanceCoin createFromParcel(Parcel parcel) {
            return new ObjCollectionBalanceCoin(parcel);
        }

        @Override
        public ObjCollectionBalanceCoin[] newArray(int i) {
            return new ObjCollectionBalanceCoin[i];
        }
    };
}
