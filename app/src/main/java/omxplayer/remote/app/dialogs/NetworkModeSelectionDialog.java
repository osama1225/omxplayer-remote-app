package omxplayer.remote.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import omxplayer.remote.app.R;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.utils.Utils;

public class NetworkModeSelectionDialog extends Dialog {

	private RadioGroup networkRadioGroup;
	private WifiConnection wifiConnection;

	public NetworkModeSelectionDialog(Context context,
			WifiConnection wifiConnection) {
		super(context);
		this.wifiConnection = wifiConnection;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.netowrk_mode_screen);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		networkRadioGroup = (RadioGroup) findViewById(R.id.network_mode_radio_group_id);
		findViewById(R.id.network_mode_done_btn_id).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (networkRadioGroup != null) {
							if (wifiConnection != null && Utils.connected) {
								switch (networkRadioGroup
										.getCheckedRadioButtonId()) {
								case R.id.network_adhoc_mode:
									wifiConnection
											.send(Utils.SSHCommands.resetNetwork);
									break;
								case R.id.network_wlan_mode:
									// TODO values to be taken from ui
									Toast.makeText(getContext(), "Not Implemented yet", Toast.LENGTH_SHORT).show();
									break;
								}
							} else {
								Toast.makeText(getContext(), "Not Connected!",
										Toast.LENGTH_SHORT).show();
							}
						}
						dismiss();
					}
				});

	}

}
