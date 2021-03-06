package com.example.tiagosaldanha.wifiandhotspot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tiago Saldanha on 14/09/2016.
 */
public class SecondActivity_HotspotCreator extends Activity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private long rowID;
    private TextView ssidTextView;
    private TextView bssidTextView;
    private TextView smallTextView;
    private TextView capabilitiesTextView;
    private Button createHotpotButton;
    private WifiManager wifiManager;
    private String ssidString;
    private String bssidString;
    private String capabilitiesString;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_hotspot_creator);

        ssidTextView = (TextView) findViewById(R.id.ssid_textView);
        bssidTextView= (TextView) findViewById(R.id.bssid_textView);
        smallTextView= (TextView) findViewById(R.id.small_textView);
        capabilitiesTextView = (TextView) findViewById(R.id.capabilities_textView);
        createHotpotButton= (Button) findViewById(R.id.hotspot_button);

        Bundle extras=getIntent().getExtras();
        rowID= extras.getLong("row_id");
        ssidString=extras.getString("network_ssid");
        bssidString=extras.getString("network_bssid");
        capabilitiesString=extras.getString("network_capabilities");



        ssidTextView.setText("Network SSID: "+ssidString+"\n");
        bssidTextView.setText("Network BSSID: "+bssidString+"\n");
        capabilitiesTextView.setText("Network capabilities: "+capabilitiesString+"\n");
        Log.i("NetworkUtil","VALUE OF ROW_ID : "+ rowID);


        createHotpotButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "CRIANDO HOTSPOT...", Toast.LENGTH_SHORT).show();
                wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                createWifiAccessPoint();
                setMobileDataEnabled (true);
                Toast.makeText(getApplicationContext(), "Hotspot Criado", Toast.LENGTH_SHORT).show();
                smallTextView.setText("HOTSPOT CRIADO");
            }
        });




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

//
//


    private void createWifiAccessPoint()
    {
        if (wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();

//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//        startActivity(intent);

        boolean methodFound = false;
        for (Method method : wmMethods)
        {
            if (method.getName().equals("setWifiApEnabled"))
            {
                methodFound = true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = ssidString;
                netConfig.BSSID= bssidString;
                netConfig.hiddenSSID = false;


                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                try
                {
                    boolean apstatus = (Boolean) method.invoke(
                            wifiManager, netConfig, true);
                    for (Method isWifiApEnabledmethod : wmMethods)
                    {
                        if (isWifiApEnabledmethod.getName().equals(
                                "isWifiApEnabled")) {
                            while (!(Boolean) isWifiApEnabledmethod.invoke(
                                    wifiManager)) {
                            }
                            ;
                            for (Method method1 : wmMethods)
                            {
                                if (method1.getName().equals(
                                        "getWifiApState")) {
                                }
                            }
                        }
                    }
                    if (apstatus)
                    {
                        Log.d("Splash Activity",
                                "Access Point created");
                    }
                    else
                    {
                        Log.d("Splash Activity",
                                "Access Point creation failed");
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!methodFound) {
            Log.d("Splash Activity",
                    "cannot configure an access point");

        }

    }

    /**
     * Switching On data
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setMobileDataEnabled (boolean enabled){
        Log.i("NetworkUtil", "Mobile data enabling: " + enabled);
        final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Class conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());

            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }





}
