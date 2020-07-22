package omxplayer.remote.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import omxplayer.remote.app.R;
import omxplayer.remote.app.utils.Sound;
import omxplayer.remote.app.utils.Utils;

public class OptionsDialog extends Dialog {

    private Context context;
    private DialogsManager dialogsManager;

    public OptionsDialog(Context context,
                         Sound sound, DialogsManager dialogsManager) {
        super(context);
        this.context = context;
        this.dialogsManager = dialogsManager;
        setupDialog();
    }

    private void setupDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.options_dialog);
        final Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.send_media_id).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        dialogsManager.prepareAndShowSendVideoListDialog();
                    }
                });

        findViewById(R.id.remove_media_id).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (!Utils.connected) {
                            Toast.makeText(context, "Not connected",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialogsManager.prepareAndShowRemoalVideoListDialog();
                    }
                });

        findViewById(R.id.close_media_id).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        dialogsManager.prepareAndShowPasswordDialog();
                    }
                });
    }

}
