package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.R;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VideoListRemovalDialog extends CustomDialog {

	private Context context;
	private WifiConnection wifiConnection;
	private String videoNameToRemove;
	private GridView gridView;

	public VideoListRemovalDialog(Context context, WifiConnection wifiConnection) {
		super(context);
		this.context = context;
		this.wifiConnection = wifiConnection;
		videoNameToRemove = "";
		gridView = null;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.videos_list_remove);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		gridView = (GridView) findViewById(R.id.gridView2);
		findViewById(R.id.delete_btn_id).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!videoNameToRemove.equals("")) {
							dismiss();
							wifiConnection.send(Utils.removeCmd,
									videoNameToRemove);
							Toast.makeText(context, "Successfully Removed!",
									Toast.LENGTH_SHORT).show();
							videoNameToRemove = "";
						}
					}
				});
	}

	private void prepareDialog(final CustomAdapter videoListRemovalAdapter) {
		videoNameToRemove = "";
		gridView.setAdapter(videoListRemovalAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				videoNameToRemove = (String) videoListRemovalAdapter
						.getItem(arg2);
				videoListRemovalAdapter.setSelectedIndex(arg2);
			}
		});
	}

	@Override
	public void prepareAndShow(CustomAdapter adapter) {
		prepareDialog(adapter);
		show();
	}
}
