package omxplayer.remote.app.adapters;

import java.util.List;

import omxplayer.remote.app.R;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NetworkScanAdapter extends ArrayAdapter<ScanResult> {
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
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.device_name, null);

		TextView view = (TextView) v.findViewById(R.id.nameId);
		ScanResult network = networks.get(position);
		view.setText(network.SSID + "\n" + network.BSSID);
		return view;
	}

}
