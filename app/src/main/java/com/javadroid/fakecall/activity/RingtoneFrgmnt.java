package com.javadroid.fakecall.activity;

import static com.javadroid.fakecall.activity.Main.dirTarget;
import static com.javadroid.fakecall.config.SettingAll.BASEURL;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.adapter.ringtone.RingtoneAdapter;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.isConfig.SharedPreference;
import com.javadroid.fakecall.model.ringtone.RingtoneModel;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RingtoneFrgmnt extends Fragment {
    View view;
    public static KProgressHUD loading;
    public static ArrayList<RingtoneModel> itemList;
    public static RecyclerView rcRintone;
    public static Context context;
    private RingtoneAdapter ringtoneAdapter;
    public static Space spaceDown;
    SharedPreference sharedPreference;
    public static Boolean online = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_ringtone, container, false);
        spaceDown = view.findViewById(R.id.spaceDown);
        spaceDown.setVisibility(View.GONE);
        sharedPreference = new SharedPreference();
        Inter.LOAD(getActivity());
        context = getContext();
        loading = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setLabel("Ringtone loading...")
                .setDetailsLabel("Please wait")
                .setCancellable(false)
                .setDimAmount(0.5f);
        itemList = new ArrayList<>();
        rcRintone = view.findViewById(R.id.rcRingtone);
        rcRintone.setLayoutManager(new LinearLayoutManager(context));


        if (checkConnectivity()) {
            online = true;
            soundOnline();
        } else {
            online = false;
            soundOffline();
        }

        return view;

    }


    Map<String, Uri> mRingtonesMap;
    private Uri mLastSelectedRingtone = null;
    private String mLastSelectedRingtoneName = "Default";

    public void takeRingtonesOffline(int pos) {
        mRingtonesMap = getRingtones((Activity) context);
        final List<String> ringtoneKeys = new ArrayList<>(mRingtonesMap.keySet());
        Map<String, Uri> listOffline = new LinkedHashMap<>();

        int which = 0;
        String ring = null;
        for (int i = 0; i < ringtoneKeys.size(); i++) {
            ring = ringtoneKeys.get(i);

            if (mRingtonesMap.get(ring).toString().contains("10000")) {
                listOffline.put(ring, mRingtonesMap.get(ring));
                which = i;
            }
        }

        String name = ringtoneKeys.get(which);
        mLastSelectedRingtoneName = name;
        mLastSelectedRingtone = mRingtonesMap.get(name);

        itemList.get(pos).setSourceOff33(mRingtonesMap.get(ring).toString());


    }

    public static Map<String, Uri> getRingtones(Activity activity) {
        RingtoneManager manager = new RingtoneManager(activity);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, Uri> list = new LinkedHashMap<>();
        String title = "Set to Default";
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE);
        list.put(title, defaultRingtoneUri); // first add the default, to get back if select another

        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri notificationUri = manager.getRingtoneUri(cursor.getPosition());
            list.put(notificationTitle, notificationUri);
        }

        return list;
    }


    private void soundOffline() {
        itemList.addAll(sharedPreference.getFavorites(context));
        ringtoneAdapter = new RingtoneAdapter(itemList, context, new RingtoneAdapter.AdapterListener() {

            @Override
            public void beresDownload(int pos) {
                RingtoneModel entriOffline = itemList.get(pos);
                takeRingtonesOffline(pos);
                sharedPreference.addFavorite(context, entriOffline);
                ringtoneAdapter.notifyDataSetChanged();
                Log.v("adslogri", "beresDownload: dtaOffline " + sharedPreference.getFavorites(context).size());

            }
        });
        rcRintone.setAdapter(ringtoneAdapter);
    }

    private void soundOnline() {
        loading.show();
        StringRequest catLoad = new StringRequest(Request.Method.GET,
                BASEURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("Sound");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        RingtoneModel developers = new RingtoneModel(jo.getInt("id"), jo.getString("nama"),
                                jo.getString("link"), "default");
                        itemList.add(developers);
                    }

                    ringtoneAdapter = new RingtoneAdapter(itemList, context, new RingtoneAdapter.AdapterListener() {

                        @Override
                        public void beresDownload(int pos) {
                            RingtoneModel entriOffline = itemList.get(pos);
                            takeRingtonesOffline(pos);
                            sharedPreference.addFavorite(context, entriOffline);
                            ringtoneAdapter.notifyDataSetChanged();
                            Log.v("adslogri", "beresDownload: dtaOffline " + sharedPreference.getFavorites(context).size());

                        }
                    });
                    rcRintone.setAdapter(ringtoneAdapter);
                    Log.i("adslog", "onResponse: itemlist size " + itemList.size());
                } catch (JSONException e) {
                    Log.i("adslog", "onResponse: error json " + e.getMessage());
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("adslog", "onErrorResponse: volley error " + error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(catLoad);
    }

    public static boolean checkIfAlreadyhavePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_AUDIO") == 0) {
                if (!dirTarget.exists()) {
                    dirTarget.mkdirs();
                }
                return true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0) {
                if (!dirTarget.exists()) {
                    dirTarget.mkdirs();
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void requestForSpecificPermission(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.e("adslog", "requestForSpecificPermission: ");
            Toast.makeText(context, "Permission needed to manage songs", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2704);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 103);
        }
    }


    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.isAvailable();
    }
}
