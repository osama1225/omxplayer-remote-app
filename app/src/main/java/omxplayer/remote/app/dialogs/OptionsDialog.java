package omxplayer.remote.app.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import omxplayer.remote.app.R;
import omxplayer.remote.app.utils.Utils;

import static omxplayer.remote.app.utils.Utils.PermissionCodes.READ_EXTERNAL_STORAGE;

public class OptionsDialog extends Dialog {

    private Activity activity;
    private DialogsManager dialogsManager;

    public OptionsDialog(Activity activity, DialogsManager dialogsManager) {
        super(activity);
        this.activity = activity;
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
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    activity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE
                            );
                        } else {
                            dismiss();
                            dialogsManager.prepareAndShowSendVideoListDialog();
                        }
                    }
                });

        findViewById(R.id.remove_media_id).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (!Utils.connected) {
                            Toast.makeText(activity, "Not connected",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialogsManager.prepareAndShowRemovalVideoListDialog();
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
