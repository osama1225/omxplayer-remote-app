package omxplayer.remote.app.adapters;

import java.util.ArrayList;
import java.util.List;

import omxplayer.remote.app.R;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NetworkScanAdapter extends CustomAdapter<ScanResult> {
	private List<ScanResult> networks;
	private Context context;

	public NetworkScanAdapter(Context context, List<ScanResult> scanResults) {
		super();
		this.context = context;
		this.networks = scanResults;
		if (networks == null) {
			networks = new ArrayList<ScanResult>();
		}
	}

	@Override
	public int getCount() {
		return networks.size();
	}

	@Override
	public Object getItem(int position) {
		return networks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void setSelectedIndex(int index) {
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		v = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.device_name, null);

		TextView view = (TextView) v.findViewById(R.id.nameId);
		ScanResult network = networks.get(position);
		view.setText(network.SSID + "\n" + network.BSSID);
		return view;
	}

	@Override
	public void setItems(List<ScanResult> items) {
		if (items != null) {
			networks.clear();
			networks.addAll(items);
		}

	}
}
