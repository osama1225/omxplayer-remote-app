package omxplayer.remote.app.dialogs;

import java.util.ArrayList;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.R;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class ScanDialog extends CustomDialog<ScanResult> {

	private WifiConnection wifiConnection;
	private ListView networkList;
	private ProgressBar scanningProgessBar;

	public ScanDialog(Context context, WifiConnection wifiConnection) {
		super(context);
		networkList = null;
		this.wifiConnection = wifiConnection;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.peers_selection_layout);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		Button scanButton = (Button) findViewById(R.id.scan_button_id);
		networkList = (ListView) findViewById(R.id.peersListId);
		scanningProgessBar = (ProgressBar) findViewById(R.id.scanningIdd);

		scanningProgessBar.setVisibility(View.INVISIBLE);

		scanButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View paramView) {
				scanningProgessBar.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				scanningProgessBar.setVisibility(View.VISIBLE);
				wifiConnection.scanNetworks();
			}
		});
	}

	@Override
	public void prepareAndShow(final CustomAdapter<ScanResult> adapter) {
		networkList.setAdapter(adapter);
		adapter.setItems(new ArrayList<ScanResult>());
		adapter.notifyDataSetChanged();
		networkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// assuming for now he will choose the right one.
				dismiss();
				MainActivity.progressBar.setVisibility(View.VISIBLE);
				Utils.SSID = ((ScanResult) adapter.getItem(arg2)).SSID;
				wifiConnection.connectToSelectedNetwork();
			}
		});
		show();
	}
	
	public ProgressBar getScanningProgessBar() {
		return scanningProgessBar;
	}
}
