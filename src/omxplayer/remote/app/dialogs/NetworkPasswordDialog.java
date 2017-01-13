package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.R;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.utils.Utils;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkPasswordDialog extends Dialog {

	private Context context;
	private WifiConnection wifiConnection;
	private EditText networkPasswordEditText;

	public NetworkPasswordDialog(Context context, WifiConnection wifiConnection) {
		super(context);
		this.context = context;
		this.wifiConnection = wifiConnection;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.network_password_screen);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		((TextView) findViewById(R.id.SSID_id)).setText(Utils.SSID);
		networkPasswordEditText = (EditText) findViewById(R.id.netowrk_passId);

		findViewById(R.id.connect_btn_id).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
						MainActivity.progressBar.setVisibility(View.VISIBLE);
						String enteredPassword = networkPasswordEditText.getText()
								.toString().trim();
						if (enteredPassword == null || enteredPassword.equals("")) {
							Toast.makeText(context, "Invalid Password",
									Toast.LENGTH_SHORT).show();
							Utils.SSID = "";
							return;
						}
						Utils.PSK = enteredPassword;
						wifiConnection.joinWifiNetwork(false);

					}
				});
		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				MainActivity.progressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void show() {
		if (networkPasswordEditText != null) {
			networkPasswordEditText.setText("");
		}
		super.show();
	}

}
