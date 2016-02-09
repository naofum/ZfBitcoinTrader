package com.github.naofum.zfbitcointrader.Zaif;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.simple.JSONObject;
import org.apache.commons.codec.binary.Hex;
import com.github.naofum.zfbitcointrader.R;
import com.github.naofum.zfbitcointrader.SecurePreferences;


public class ObjAPI implements Parcelable {
    //Private Fields
    public String apikey;
    public String apisecret;
    public int refreshInterval;
    public int depthRange;
    public ObjBalanceCoin BTC;
    public ObjBalanceCoin JPY;
    public final static String BASEURL = "https://api.zaif.jp/";
    private boolean keyChanged;
    private SecurePreferences mSecurePrefs;
    public SecretKey key;
    private static long counter = 0;

    //Constructors
    public ObjAPI(Context con, SecretKey secKey) {
        JPY = new ObjBalanceCoin("Yen", "JPY");
        BTC = new ObjBalanceCoin("Bitcoin", "BTC");
        key = secKey;
        mSecurePrefs = new SecurePreferences(con, con.getString(R.string.preference_file_key), secKey.toString(), true);
    }

    //Methods
    public void update(Context con) {
        String primary = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        String secret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        keyChanged = (!primary.equals(apikey) || !secret.equals(apisecret));
        this.apikey = primary;
        this.apisecret = secret;
        if (keyChanged) {
            mSecurePrefs.put(con.getString(R.string.prefapikey), primary);
            mSecurePrefs.put(con.getString(R.string.prefapisecret), secret);
            mSecurePrefs.put(con.getString(R.string.prefbtcaddress), "");
        }
    }

    public static Boolean validateKeys(Context con, SecretKey secKey) {
        SecurePreferences mSecurePrefs = new SecurePreferences(con, con.getString(R.string.preference_file_key), secKey.toString(), true);
        String primary = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        String secret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        return primary.length() == 36 && secret.length() == 36;
    }

    public static void initPrefs(final Context con, SecretKey secKey) {
        SecurePreferences mSecurePrefs = new SecurePreferences(con, con.getString(R.string.preference_file_key), secKey.toString(), true);

        //API keys
        if (!mSecurePrefs.containsKey(con.getString(R.string.prefapikey))) {
            mSecurePrefs.put(con.getString(R.string.prefapikey), "");
        }
        if (!mSecurePrefs.containsKey(con.getString(R.string.prefapisecret))) {
            mSecurePrefs.put(con.getString(R.string.prefapisecret), "");
        }
        if (!mSecurePrefs.containsKey("refreshInterval")) {
            mSecurePrefs.put("refreshInterval", "10");
        }
        if (!mSecurePrefs.containsKey("depthRange")) {
            mSecurePrefs.put("depthRange", "10");
        }
        //TODO Update here with trade fee changes
        mSecurePrefs.put("tradeFeeOffset", "0.99999");

    }

    public void loadPrefs(final Context con, SecretKey secKey) {
        SecurePreferences mSecurePrefs = new SecurePreferences(con, con.getString(R.string.preference_file_key), secKey.toString(), true);
        this.apikey = mSecurePrefs.getString(con.getString(R.string.prefapikey));
        this.apisecret = mSecurePrefs.getString(con.getString(R.string.prefapisecret));
        this.refreshInterval = Integer.parseInt(mSecurePrefs.getString("refreshInterval"));
        this.depthRange = Integer.parseInt(mSecurePrefs.getString("depthRange"));
    }

    private String buildUrl(String method, String path, JSONObject body) {
        String url = this.BASEURL + path;
        if (method.equals("GET") && body != null) {
            url += URLEncoder.encode(body.toString());
        }
        return url;
    }

    public String getTimestampMillis() {
//        long timestamp = System.currentTimeMillis();
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
//        String c = "000" + counter++;
//        String time = "" + timestamp + c.substring(c.length() - 2, c.length());
//        String time = "" + timestamp + "000";

//        int timestamp = (int) (System.currentTimeMillis() - 1371854884) * 10;
//        if (timestamp <= oldtimestamp) oldtimestamp++;
//        long timestamp = (long) ((System.currentTimeMillis() / 1000000L) * 1000 + counter++ % 1000);
        String time = "" + timestamp;
        return time.trim();
    }

    public String getTimestamp() {
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        String time = "" + timestamp;
        return time.trim();
    }

    public String getSignature(String method, String post, String body) {
//        String data = "method=" + url + "&nonce=" + getTimestampMillis();
        String data = post;
        if (method.equals("POST") && body != null) {
            data += "&" + body;
        }
        try {
            SecretKeySpec secret_key = new SecretKeySpec(this.apisecret.getBytes(), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secret_key);
//            String b64data = new String(Base64.encodeBase64(data.getBytes()));
//            byte[] rawHmac = mac.doFinal(b64data.getBytes());
//            return new String(Base64.encodeBase64(rawHmac));

            byte[] digest = mac.doFinal(data.getBytes());
            return new String(Hex.encodeHex(digest));
//            BigInteger hash = new BigInteger(1, digest);
//            String result = hash.toString(16);
//            if ((result.length() % 2) != 0) {
//                result = "0" + result;
//            }
//            return result;
        } catch (InvalidKeyException e) {
            Log.e("ObjAPI.getSignature", "Invalid Key exception");
            return "";
        } catch (NoSuchAlgorithmException e1) {
            Log.e("ObjAPI.getSignature", "NoSuchAlgorithm exception");
            return "";
        }
    }

    public float getTradeFee() {
        return Float.parseFloat(mSecurePrefs.getString("tradeFeeOffset"));
    }

    private String getUrl(String path, JSONObject obj) {
        return buildUrl("GET", "api/1/" + path + "/btc_jpy", obj);
    }

    private String getPostUrl(String path, JSONObject obj) {
        return buildUrl("POST", "tapi", obj);
    }

    public String getTicker() {
        return getUrl("ticker", null);
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public int getDepthRange() {
        return depthRange;
    }

    public String getOrderBook() {
        return getUrl("depth", null);
    }

    public String getRecentTrades() {
        return getUrl("trades", null);
    }

    //   public String getKeyPermissions() {
//        return getUrl("key", null);
//    }

    public String getBalances() {
        return getPostUrl("get_info", null);
    }

    public String getDepositAddress() {
        return getPostUrl("get_info", null);
    }

    public String getOpenOrders(){
        return getPostUrl("active_orders", null);
    }

    public String getTradeHistory() {
        return getPostUrl("trade_history", null);
    }

    public String addOrder(String side, String price, String amount) {
        return getPostUrl("trade", null);
    }

    public String cancelOrder(String order_id) {
        return getPostUrl("cancel_order", null);
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<ObjAPI> CREATOR = new Creator<ObjAPI>() {
        @Override
        public ObjAPI createFromParcel(Parcel parcel) {
            return new ObjAPI(parcel);
        }

        @Override
        public ObjAPI[] newArray(int i) {
            return new ObjAPI[i];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(apikey);
        out.writeString(apisecret);
        out.writeInt(refreshInterval);

    }
    private ObjAPI(Parcel in){
        this.apikey = in.readString();
        this.apisecret = in.readString();
        this.refreshInterval = in.readInt();
    }
}
