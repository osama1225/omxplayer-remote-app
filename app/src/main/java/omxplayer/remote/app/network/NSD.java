package omxplayer.remote.app.network;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.DiscoveryListener;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.utils.Utils;

public class NSD {

    private DiscoveryListener discoveryListener;
    private NsdManager manager;

    private SSHClient ssh;
    private ConnectionServiceHandler connectionServiceHandler;

    public NSD(Context context, ConnectionServiceHandler connectionServiceHandler) {
        this.connectionServiceHandler = connectionServiceHandler;
        manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initializeDiscoveryListener();
        manager.discoverServices("_workstation._tcp.",
                NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private void initializeDiscoveryListener() {

        discoveryListener = new DiscoveryListener() {

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                manager.stopServiceDiscovery(this);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                manager.stopServiceDiscovery(this);
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceName().contains("Hologram")) {
                    // connect to that service.
                    manager.resolveService(serviceInfo, new MyResolveListener());
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("koko", "discovery stopepd : ");

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                // TODO Auto-generated method stub
                Log.d("koko", "discovery started : " + serviceType);

            }
        };
    }

    public SSHClient getSSH() {
        return ssh;
    }

    public void setSSH(SSHClient client) {
        this.ssh = client;
    }

    public void unregister() {
        if (manager != null)
            manager.stopServiceDiscovery(discoveryListener);
    }

    private class MyResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            if (serviceInfo.getHost() == null)
                return;
            if (ssh != null)
                return;
            Utils.hostName = serviceInfo.getHost().getHostAddress();
            ssh = new SSHClient(connectionServiceHandler);
        }


        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d("koko", "resolver faield: " + errorCode);
        }
    }
}
