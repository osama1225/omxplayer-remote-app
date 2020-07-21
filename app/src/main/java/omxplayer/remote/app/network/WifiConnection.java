package omxplayer.remote.app.network;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.List;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.adapters.NetworkScanAdapter;
import omxplayer.remote.app.dialogs.DialogsManager;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.utils.Utils;

/**
 * To be re-designed/written from scratch!!
 *
 * @author Mohamed
 */
public class WifiConnection implements CommandSender {

    private Activity context;
    private ConnectionServiceHandler connectionServiceHandler;
    private WifiManager wifiManager;
    private wifiScanReceiver receiver;
    private IntentFilter filter;
    private CustomAdapter<ScanResult> networksAdapter;

    private NSD discovery;

    private Hashtable<String, String> savedNetworks;
    private boolean connectedFromApp;

    private DialogsManager dialogsManager;

    public WifiConnection(Activity context,
                          ConnectionServiceHandler connectionServiceHandler) {
        connectedFromApp = false;
        this.context = context;
        this.connectionServiceHandler = connectionServiceHandler;
        this.networksAdapter = new NetworkScanAdapter(context, null);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        receiver = new wifiScanReceiver();
        registerNetworksReceiver();

    }

    private void enableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void scanNetworks() {
        enableWifi();
        boolean scanStarted = wifiManager.startScan();
        Log.d("debug", "scanNetworks: start scan result is " + scanStarted);
    }

    /**
     * Check for required permissions needed for enabling network scan.
     *
     * @return true, if all required permissions are granted.
     */
    public boolean askPermissionForNetworkScanning() {
        boolean allGranted = true;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            allGranted = false;
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );
        }
        // for recent versions, we need to make sure location is enabled
        final LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            allGranted = false;
            tryEnablingNetworkProvider();
        }
        return allGranted;
    }

    private void tryEnablingNetworkProvider() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Network provider is disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method that handles the action to do upon the selection of a network to
     * connect.First of all, if am already connected to the network,just start
     * ssh session. 1- check if this network is saved before, then don't promote
     * for a password, and connect directly
     */
    public void connectToSelectedNetwork() {

        String curr = wifiManager.getConnectionInfo().getSSID();
        if (curr.equals("\"" + Utils.SSID + "\"")) {
            discovery = new NSD(context, connectionServiceHandler);
            return;
        }

        savedNetworks = readSavedNetworks();
        if (savedNetworks != null) {
            if (!savedNetworks.containsKey(Utils.SSID)) {
                dialogsManager.showNetworkPasswordDialog();
            } else {
                Utils.PSK = savedNetworks.get(Utils.SSID);
                joinWifiNetwork(true);
            }
        } else {
            dialogsManager.showNetworkPasswordDialog();
        }
    }

    /*
     * join the network specified by the name id. assuming for now the password
     * is fixed
     */
    public void joinWifiNetwork(boolean savedBefore) {
        // check if am already connect to the netowrk
        String curr = wifiManager.getConnectionInfo().getSSID();
        if (!curr.equals("\"" + Utils.SSID + "\"")) {
            connect(savedBefore);
        } else {
            discovery = new NSD(context, connectionServiceHandler);
        }
    }

    /**
     * Method that perform the actual connection to the network
     */
    private void connect(boolean savedBefore) {
        // check if the network is configured before
        List<WifiConfiguration> NWS = wifiManager.getConfiguredNetworks();
        int id = -1;
        int i = 0;
        for (; i < NWS.size(); i++) {
            if (NWS.get(i).SSID.equals("\"" + Utils.SSID + "\"")) {
                id = NWS.get(i).networkId;
                if (!savedBefore) {
                    wifiManager.removeNetwork(id);
                    // check if it saved with another id and delete it
                    for (int j = i + 1; j < NWS.size(); j++) {
                        if (NWS.get(j).SSID.equals("\"" + Utils.SSID + "\"")) {
                            id = NWS.get(j).networkId;
                            wifiManager.removeNetwork(id);
                        }
                    }
                    // wifiManager.saveConfiguration();
                    id = -1;
                    i = NWS.size() + 1;
                    Utils.PSK = "";
                }
                break;
            }
        }
        if (i < NWS.size() && savedBefore) {
            NWS.get(i).preSharedKey = "\"" + Utils.PSK + "\"";
        } else if (i == NWS.size() && savedBefore)
            Utils.PSK = "";
        if (id != -1 && Utils.PSK.equals("")) {
            wifiManager.removeNetwork(id);
            dialogsManager.showNetworkPasswordDialog();
            return;
        }
        if (id == -1) {
            if (i == NWS.size() + 1) {
                Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT)
                        .show();
                MainActivity.progressBar.setVisibility(ProgressBar.INVISIBLE);
                Utils.PSK = "";
            }
            if (Utils.PSK == "") {
                dialogsManager.showNetworkPasswordDialog();
                return;
            }
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + Utils.SSID + "\"";
            conf.preSharedKey = "\"" + Utils.PSK + "\"";
            conf.priority = Integer.MAX_VALUE;

            // add it to wifi manager settings
            id = wifiManager.addNetwork(conf);
            if (id != -1) {
                wifiManager.saveConfiguration();
            }
        }
        if (id == -1) {
            Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT)
                    .show();
            MainActivity.progressBar.setVisibility(ProgressBar.INVISIBLE);
            Utils.PSK = "";
            dialogsManager.showNetworkPasswordDialog();
            return;
        }
        Log.d("id : ", "" + id);
        // enable it to connect
        wifiManager.disconnect();
        while (!wifiManager.enableNetwork(id, true)) {

        }
        boolean state = wifiManager.reconnect();
        if (!state) {
            connectionServiceHandler.connectionFailed();
            Utils.connected = false;
        } else
            connectedFromApp = true;
    }

    @Override
    public String send(String cmd, String... optionalParams) {
        if (discovery != null && discovery.getSSH() != null) {
            return discovery.getSSH().executeCmd(cmd, optionalParams);
        }
        return "";
    }

    /**
     * Check if am connected to the network name given as param
     *
     * @param SSID
     * @return
     */
    public boolean isConnected(String SSID) {
        String curr = wifiManager.getConnectionInfo().getSSID();
        if (curr.equals("\"" + SSID + "\""))
            return true;

        return false;
    }

    public void registerNetworksReceiver() {
        context.registerReceiver(receiver, filter);
    }

    public void unregisterNetworksReceiver() {
        if (receiver != null && context != null)
            context.unregisterReceiver(receiver);
    }

    public SSHClient getSsh() {
        if (discovery != null)
            return discovery.getSSH();
        return null;
    }

    public void setSsh(SSHClient client) {
        if (discovery != null)
            discovery.setSSH(client);
    }

    public void close() {
        unregisterNetworksReceiver();
        Utils.connected = false;
        wifiManager = null;
        receiver = null;
        filter = null;
        networksAdapter = null;
        if (getSsh() != null)
            getSsh().closeConnection();
        if (discovery != null)
            discovery.unregister();
        discovery = null;
    }

    @SuppressWarnings("unchecked")
    private Hashtable<String, String> readSavedNetworks() {
        FileInputStream fin = null;
        ObjectInputStream oin = null;
        Hashtable<String, String> networks = null;
        try {
            fin = context.openFileInput("networks");
            oin = new ObjectInputStream(fin);
            networks = (Hashtable<String, String>) oin.readObject();
        } catch (Exception e) {
            Log.d("error", "Error in reading saved networks: " + e.getMessage());
        } finally {
            try {
                if (oin != null)
                    oin.close();
                if (fin != null)
                    fin.close();
            } catch (Exception ee) {
            }
        }
        return networks;
    }

    private void saveNetwork() {
        // get saved networks
        savedNetworks = readSavedNetworks();
        if (savedNetworks == null)
            savedNetworks = new Hashtable<String, String>();
        // check if this network is saved before
        // if (savedNW.containsKey(Utils.SSID))
        // return;
        savedNetworks.put(Utils.SSID, Utils.password);
        FileOutputStream fout = null;
        ObjectOutputStream out = null;
        try {
            fout = context.openFileOutput("networks", 0);
            out = new ObjectOutputStream(fout);
            out.writeObject(savedNetworks);
        } catch (Exception e) {
            Log.d("error", "Error in saving new network : " + e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
                if (fout != null)
                    fout.close();
            } catch (Exception ee) {
            }
        }
    }

    public CustomAdapter<ScanResult> getNetworksAdapter() {
        return networksAdapter;
    }

    public void setDialogsManager(DialogsManager dialogsManager) {
        this.dialogsManager = dialogsManager;
    }

    private class wifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                // check the current state.
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (!networkInfo.isConnected())
                    Utils.connected = false;
                else {
                    String currentNetworkSSID = wifiManager.getConnectionInfo()
                            .getSSID();
                    if (connectedFromApp
                            && currentNetworkSSID.equals("\"" + Utils.SSID
                            + "\"")) {
                        connectedFromApp = false;
                        saveNetwork();
                        discovery = new NSD(WifiConnection.this.context,
                                connectionServiceHandler);
                    }
                }
            } else if (intent.getAction().equals(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                ProgressBar scanningBar = dialogsManager.getScanDialog()
                        .getScanningProgessBar();
                if (scanningBar != null
                        && scanningBar.getVisibility() == ProgressBar.VISIBLE) {
                    scanningBar.setVisibility(View.GONE);
                    networksAdapter.setItems(wifiManager.getScanResults());
                    networksAdapter.notifyDataSetChanged();
                }

            } else if (intent.getAction().equals(
                    WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                SupplicantState state = ((SupplicantState) intent
                        .getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                switch (state) {
                    case COMPLETED:
                        // successfully connected, check whether network that am
                        // connected to
                        if (Utils.SSID != "") {
                            String currNetwork = wifiManager.getConnectionInfo()
                                    .getSSID();
                            if (!currNetwork.equals("\"" + Utils.SSID + "\"")) {
                                connectionServiceHandler.connectionFailed();
                            } else {
                                saveNetwork();
                                // discovery = new NSD(WifiConnection.this.context);
                            }
                        }
                        break;

                    default:
                        break;
                }
                int error = intent.getIntExtra(
                        WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if (error == WifiManager.ERROR_AUTHENTICATING) {
                    Toast.makeText(context, "Authentication Failed!",
                            Toast.LENGTH_SHORT).show();
                    MainActivity.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
