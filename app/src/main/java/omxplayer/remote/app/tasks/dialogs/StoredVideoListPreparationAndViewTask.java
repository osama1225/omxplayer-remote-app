package omxplayer.remote.app.tasks.dialogs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.View;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.VideoItem;
import omxplayer.remote.app.adapters.CustomAdapter;
import omxplayer.remote.app.adapters.StoredVideoListAdapter;
import omxplayer.remote.app.dialogs.CustomDialog;

public class StoredVideoListPreparationAndViewTask extends
		AsyncTask<Void, Void, Void> {
	private CustomAdapter<VideoItem> storedVideoListAdapter;

	private Context context;
	private CustomDialog<VideoItem> storedVideoListDialog;

	public StoredVideoListPreparationAndViewTask(Context context, CustomDialog<VideoItem> storedVideoListDialog) {
		this.context = context;
		this.storedVideoListDialog = storedVideoListDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MainActivity.progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		storedVideoListAdapter = new StoredVideoListAdapter(context);
		if (Looper.myLooper() != null)
			Looper.myLooper().quit();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		MainActivity.progressBar.setVisibility(View.INVISIBLE);
		storedVideoListDialog.prepareAndShow(storedVideoListAdapter);
	}

	public void execute() {
		this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] {});
	}
	
	public CustomAdapter<VideoItem> getStoredVideoListAdapter() {
		return storedVideoListAdapter;
	}
}
