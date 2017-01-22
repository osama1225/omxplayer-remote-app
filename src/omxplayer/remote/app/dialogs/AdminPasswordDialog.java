package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.utils.Utils;
import omxplayer.remote.app.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class AdminPasswordDialog extends Dialog {

	private Context context;
	private CommandSender commandSender;
	private EditText passwordEditText;

	public AdminPasswordDialog(Context context, CommandSender commandSender) {
		super(context);
		this.context = context;
		this.commandSender = commandSender;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.password_screen);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		passwordEditText = (EditText) findViewById(R.id.passId);

		passwordEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().equals(Utils.adminPassword)) {
					// perform close function
					dismiss();
					if (!Utils.connected) {
						Toast.makeText(context, "Not conected",
								Toast.LENGTH_SHORT).show();
						return;
					}
					commandSender.send(Utils.shutdownHostCmd);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void show() {
		if (passwordEditText != null) {
			passwordEditText.setText("");
		}
		super.show();
	}
}
