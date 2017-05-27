package omxplayer.remote.app.dialogs;

import omxplayer.remote.app.R;
import omxplayer.remote.app.VideoItem;
import omxplayer.remote.app.VideoSentListener;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.adapters.StoredVideoListAdapter;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.tasks.FileSender;
import omxplayer.remote.app.utils.Sound;
import omxplayer.remote.app.utils.Utils;
import omxplayer.remote.app.utils.Utils.SendStatus;
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

	public StoredVideoListDialog(Context context, CommandSender commandSender, Sound sound) {
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
						StoredVideoListAdapter adapter = (StoredVideoListAdapter) gridView.getAdapter();
						VideoItem[] selectedVideoItems = adapter.getSelectedItems();
						if (selectedVideoItems != null && selectedVideoItems.length > 0) {
							dismiss();
							fileSender = new FileSender();
							fileSender.setFinishedListener(new VideoSentListener() {
									@Override
									public void finishedSending(
											SendStatus state, String fileName) {
										if (state.equals(SendStatus.SUCCESS)) {
											commandSender.send(Utils.SSHCommands.fileSentCmd, fileName);
											Toast.makeText(context, "Successfully Sent!", Toast.LENGTH_LONG).show();
											sound.play(context, R.raw.success);
										} else if (state.equals(SendStatus.FAILED)) {
											commandSender.send(Utils.SSHCommands.removeCmd, fileName);
											Toast.makeText(context, "Not Sent!", Toast.LENGTH_LONG).show();
											sound.play(context, R.raw.fail);
										} else {
											commandSender.send(Utils.SSHCommands.removeCmd, fileName);
										}
									}
								});
							
							String [] filePaths = new String[selectedVideoItems.length];
							int index = 0;
							for (VideoItem videoItem : selectedVideoItems) {
								filePaths[index++] = videoItem.getPath();
							}
							fileSender.exec(context, filePaths);
						}
					}
				});
	}

	private void prepareDialog(final CustomAdapter<VideoItem> storedVideoListAdapter) {
		fileSender = null;
		gridView.setAdapter(storedVideoListAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
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
