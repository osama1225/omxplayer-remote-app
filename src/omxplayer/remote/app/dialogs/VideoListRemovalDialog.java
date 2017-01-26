package omxplayer.remote.app.dialogs;


import omxplayer.remote.app.R;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.adapters.VideoListRemovalAdapter;
import omxplayer.remote.app.network.CommandSender;
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

public class VideoListRemovalDialog extends CustomDialog<String> {

	private Context context;
	private CommandSender commandSender;
	private GridView gridView;

	public VideoListRemovalDialog(Context context, CommandSender commandSender) {
		super(context);
		this.context = context;
		this.commandSender = commandSender;
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
						CustomAdapter<String> adapter = (VideoListRemovalAdapter) gridView
								.getAdapter();
						String[] videoNamesToRemove = adapter.getSelectedItems();
						if (videoNamesToRemove != null && videoNamesToRemove.length > 0) {
							dismiss();
							commandSender.send(Utils.removeCmd,
									videoNamesToRemove);
							Toast.makeText(context, "Successfully Removed!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void prepareDialog(
			final CustomAdapter<String> videoListRemovalAdapter) {
		gridView.setAdapter(videoListRemovalAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				videoListRemovalAdapter.toggleFromSelectedIndecies(arg2);
			}
		});
	}

	@Override
	public void prepareAndShow(CustomAdapter<String> adapter) {
		prepareDialog(adapter);
		show();
	}
}
