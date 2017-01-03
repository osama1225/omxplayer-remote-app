package omxplayer.remote.app.network;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.utils.Utils;

import omxplayer.remote.app.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class WifiConnection {

	private Activity context;
	private ConnectionServiceHandler connectionServiceHandler;
	private WifiManager wifiManager;
	private wifiScanReceiver receiver;
	private IntentFilter filter;
	private List<ScanResult> networks;
	private ArrayAdapter<ScanResult> adapter;

	private Dialog dialog;
	private Button scanButton;
	private ListView networkList;
	private ProgressBar scanning;

	private NSD discovery;

	private Hashtable<String, String> savedNW;
	private boolean connectedFromApp;

	public WifiConnection(Activity context,
			ConnectionServiceHandler connectionServiceHandler) {
		connectedFromApp = false;
		this.context = context;
		this.connectionServiceHandler = connectionServiceHandler;
		networks = new ArrayList<ScanResult>();
		this.adapter = new NetworkScanAdapter(context,
				R.layout.peers_selection_layout, networks);
		enableWifi();
		filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		receiver = new wifiScanReceiver();
		registerNetworksReceiver();

	}

	private void enableWifi() {
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);

	}

	public void showScanDialog() {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.peers_selection_layout);
		final Window window = dialog.getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		scanButton = (Button) dialog.findViewById(R.id.scan_button_id);
		networkList = (ListView) dialog.findViewById(R.id.peersListId);
		scanning = (ProgressBar) dialog.findViewById(R.id.scanningIdd);

		scanning.setVisibility(View.INVISIBLE);
		adapter.clear();
		networkList.setAdapter(adapter);
		setListeners();

		dialog.show();

	}

	private void setListeners() {
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				scanning.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				scanning.setVisibility(View.VISIBLE);
				scan();

			}
		});

		networkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// assuming for now he will choose the right one.
				dialog.dismiss();
				MainActivity.progressBar.setVisibility(View.VISIBLE);
				Utils.SSID = networks.get(arg2).SSID;
				handleSelectedNetworkToConnect();

			}
		});
	}

	private void scan() {
		wifiManager.startScan();
	}

	/**
	 * Method that handles the action to do upon the selection of a network to
	 * connect.First of all, if am already connected to the network,just start
	 * ssh session. 1- check if this network is saved before, then don't promote
	 * for a password, and connect directly
	 */
	private void handleSelectedNetworkToConnect() {

		String curr = wifiManager.getConnectionInfo().getSSID();
		if (curr.equals("\"" + Utils.SSID + "\"")) {
			discovery = new NSD(context, connectionServiceHandler);
			return;
		}

		savedNW = readSavedNetworks();
		if (savedNW != null) {
			if (!savedNW.containsKey(Utils.SSID))
				showNetworkPasswordDialog();
			else {
				Utils.PSK = savedNW.get(Utils.SSID);
				joinNetwork(true);
			}
		} else
			showNetworkPasswordDialog();
	}

	/*
	 * join the network specified by the name id. assuming for now the password
	 * is fixed
	 */
	private void joinNetwork(boolean savedBefore) {
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
			showNetworkPasswordDialog();
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
				showNetworkPasswordDialog();
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
			showNetworkPasswordDialog();
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

	private void showNetworkPasswordDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.network_password_screen);
		final Window window = dialog.getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		((TextView) dialog.findViewById(R.id.SSID_id)).setText(Utils.SSID);
		final EditText network_pass = (EditText) dialog
				.findViewById(R.id.netowrk_passId);

		dialog.findViewById(R.id.connect_btn_id).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						MainActivity.progressBar.setVisibility(View.VISIBLE);
						String pass = network_pass.getText().toString().trim();
						if (pass == null || pass.equals("")) {
							Toast.makeText(context, "Invalid Password",
									Toast.LENGTH_SHORT).show();
							Utils.SSID = "";
							return;
						}
						// trying to connect
						Utils.PSK = pass;
						joinNetwork(false);

					}
				});
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				MainActivity.progressBar.setVisibility(View.INVISIBLE);
			}
		});
		dialog.show();

	}

	public String send(String cmd,String...optionalParams) {
		if (discovery != null && discovery.getSSH() != null)
			return discovery.getSSH().executeCmd(cmd,optionalParams);
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

	public void setSsh(SSHClient client) {
		if (discovery != null)
			discovery.setSSH(client);
	}

	public SSHClient getSsh() {
		if (discovery != null)
			return discovery.getSSH();
		return null;
	}

	public void close() {
		unregisterNetworksReceiver();
		Utils.connected = false;
		wifiManager = null;
		receiver = null;
		filter = null;
		networks = null;
		adapter = null;
		if (getSsh() != null)
			getSsh().closeConnection();
		if (discovery != null)
			discovery.unregister();
		discovery = null;
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
					String curr = wifiManager.getConnectionInfo().getSSID();
					if (connectedFromApp
							&& curr.equals("\"" + Utils.SSID + "\"")) {
						connectedFromApp = false;
						saveNw();
						discovery = new NSD(WifiConnection.this.context,
								connectionServiceHandler);
					}
				}
			} else if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				networks = wifiManager.getScanResults();
				if (scanning != null
						&& scanning.getVisibility() == ProgressBar.VISIBLE) {
					scanning.setVisibility(View.GONE);
					adapter.clear();
					adapter.addAll(networks);
					adapter.notifyDataSetChanged();
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
							saveNw();
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
				}
			}
		}
	}

	class NetworkScanAdapter extends ArrayAdapter<ScanResult> {
		private List<ScanResult> networks;
		private Context c;

		public NetworkScanAdapter(Context context, int textViewResourceId,
				List<ScanResult> objects) {
			super(context, textViewResourceId, objects);
			c = context;
			this.networks = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			v = ((LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.device_name, null);

			TextView view = (TextView) v.findViewById(R.id.nameId);
			ScanResult network = networks.get(position);
			view.setText(network.SSID + "\n" + network.BSSID);
			return view;
		}

	}

	public void joinDisplayToNetwork(final String name, final String pass) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				send("sudo update-rc.d hostapd disable; ");
				send("sudo update-rc.d isc-dhcp-server disable; ");
				send("sudo bash -c 'cat .scripts/interfaces_wlan.txt > /etc/network/interfaces'; ");
				send("sudo bash -c 'cat .scripts/wpa_supplicant_head.txt > /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send("sudo bash -c 'echo \"network={\" >> /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send("sudo bash -c 'echo -e \"ssid=\\\""
						+ name
						+ "\\\"\" >> /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send("sudo bash -c 'echo -e \"psk=\\\""
						+ pass
						+ "\\\"\" >> /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send("sudo bash -c 'echo \"key_mgmt=WPA-PSK\" >> /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send("sudo bash -c 'echo \"}\" >> /etc/wpa_supplicant/wpa_supplicant.conf'; ");
				send(Utils.restartHostCmd);
				Toast.makeText(context, "Restarting...", Toast.LENGTH_SHORT)
						.show();

			}
		});
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

	private void saveNw() {
		// get saved networks
		savedNW = readSavedNetworks();
		if (savedNW == null)
			savedNW = new Hashtable<String, String>();
		// check if this network is saved before
		// if (savedNW.containsKey(Utils.SSID))
		// return;
		savedNW.put(Utils.SSID, Utils.password);
		FileOutputStream fout = null;
		ObjectOutputStream out = null;
		try {
			fout = context.openFileOutput("networks", 0);
			out = new ObjectOutputStream(fout);
			out.writeObject(savedNW);
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

}
