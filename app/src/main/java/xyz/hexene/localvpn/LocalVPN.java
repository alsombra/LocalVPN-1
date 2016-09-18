/*
** Copyright 2015, Mohamed Naufal
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package xyz.hexene.localvpn;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tiagosaldanha.wifiandhotspot.MainActivity_WifiWatcher;
import com.example.tiagosaldanha.wifiandhotspot.SecondActivity_HotspotCreator;


public class LocalVPN extends ActionBarActivity
{
    private static final int VPN_REQUEST_CODE = 0x0F;
    public static final int PACKET_MESSAGE_COMING = 1;

    private boolean waitingForVPNStart;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO: colocar switch
            Log.d("LocalVPN", "MENSAGEM PARA HANDLER: "+  msg.obj);

        }
    };
    private LocalVPNService mService = null;

    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (LocalVPNService.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false))
                    waitingForVPNStart = false;
            }
        }
    };
    private boolean mIsBound;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            Log.d("Service Connection", "On Service Connected + setHanlder");

            mService = ((LocalVPNService.LocalBinder)iBinder).getInstance();
            mService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_vpn);
        final Button vpnButton = (Button)findViewById(R.id.vpn);
        final Button wifi_hotspotButton = (Button)findViewById(R.id.wifi_hotspot);

        wifi_hotspotButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent viewWiFi = new Intent(LocalVPN.this,MainActivity_WifiWatcher.class);
                startActivity(viewWiFi);
            }
        });

        vpnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startVPN();
            }
        });
        waitingForVPNStart = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(vpnStateReceiver,
                new IntentFilter(LocalVPNService.BROADCAST_VPN_STATE));
    }

    private void startVPN()
    {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            waitingForVPNStart = true;
            Log.d("LocalVPN", "Startin Service");
            startService(new Intent(this, LocalVPNService.class));
            Log.d("LocalVPN", "Making Bind Service");
            doBindService();
            enableButton(false);
        }
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                LocalVPNService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        enableButton(!waitingForVPNStart && !LocalVPNService.isRunning());
    }

    private void enableButton(boolean enable)
    {
        final Button vpnButton = (Button) findViewById(R.id.vpn);
        if (enable)
        {
            vpnButton.setEnabled(true);
            vpnButton.setText(R.string.start_vpn);
        }
        else
        {
            vpnButton.setEnabled(false);
            vpnButton.setText(R.string.stop_vpn);
        }
    }
}
