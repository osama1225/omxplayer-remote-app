package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.R;
import omxplayer.remote.app.VideoItem;
import omxplayer.remote.app.VideoSentListener;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.tasks.FileSender;
import omxplayer.remote.app.utils.Sound;
import omxplayer.remote.app.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class StoredVideoListDialog extends CustomDialog<VideoItem> {

	private Context context;
	private CommandSender commandSender;
	private FileSender fileSender;
	private Sound sound;
	private GridView gridView;
	private VideoItem videoToSend;
	private String recentSentVideoName;

	public StoredVideoListDialog(Context context,
			CommandSender commandSender, Sound sound) {
		super(context);
		this.context = context;
		this.commandSender = commandSender;
		this.sound = sound;
		setupDialog();
	}

	private void setupDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.videos_list_send);
		final Window window = getWindow();
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		gridView = (GridView) findViewById(R.id.gridView1);
		findViewById(R.id.done_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// actual sending
						if (!Utils.connected) {
							Toast.makeText(context, "Not conected",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (videoToSend != null) {
							dismiss();
							fileSender = new FileSender(sound);
							fileSender
									.setFinishedListener(new VideoSentListener() {

										@Override
										public void finishedSending(
												boolean finished) {
											if (finished
													&& !fileSender.isCanceled()) {
												commandSender.send(
														Utils.fileSentCmd,
														recentSentVideoName);
											}
										}
									});

							fileSender.exec(context, videoToSend.getPath(),
									commandSender);
							videoToSend = null;
						}
					}
				});
	}

	private void prepareDialog(final CustomAdapter<VideoItem> storedVideoListAdapter) {
		fileSender = null;
		recentSentVideoName = "";
		videoToSend = null;
		gridView.setAdapter(storedVideoListAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				videoToSend = (VideoItem) storedVideoListAdapter.getItem(arg2);
				recentSentVideoName = new String(videoToSend.getName());
				storedVideoListAdapter.toggleFromSelectedIndecies(arg2);
			}

		});
	}

	@Override
	public void prepareAndShow(CustomAdapter<VideoItem> adapter) {
		prepareDialog(adapter);
		show();
	}
}
