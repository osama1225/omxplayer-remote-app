package omxplayer.remote.app.fragments;

import omxplayer.remote.app.MainActivity;
import omxplayer.remote.app.adapters.PlayListAdapter;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.network.WifiConnection;
import omxplayer.remote.app.utils.Utils;

import omxplayer.remote.app.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PlayListFragment extends Fragment {

	private WifiConnection wifiConn;
	private ConnectionServiceHandler connectionServiceHandler;
	private ViewGroup rootView;
	private ListView lv;
	private PlayListAdapter adapter;

	private Handler timerHandler;
	private Runnable checkFileLog;
	private final long checkInterval = 2000;

	public PlayListFragment(WifiConnection wifiConn, ConnectionServiceHandler connectionServiceHandler) {
		this.wifiConn = wifiConn;
		this.connectionServiceHandler = connectionServiceHandler;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ini the timer
		checkFileLog = new Runnable() {

			@Override
			public void run() {
				if (!Utils.connected)
					return;
				String response = wifiConn.send(Utils.retrieveplaylistCmd);
				String[] videoNames = response.split("\n");
				adapter.updateList(videoNames);
				timerHandler.postDelayed(checkFileLog, checkInterval);
			}
		};

		timerHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(
				R.layout.videos_playlist_screen, container, false);
		rootView.bringToFront();
		lv = (ListView) rootView.findViewById(R.id.listView2);
		lv.bringToFront();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String selectedName = adapter.getNames().get(arg2);
				adapter.setCurrentlyPlaying(selectedName);
				wifiConn.send(Utils.selectVideoCmd,selectedName);
				connectionServiceHandler.changePlayState("p");
			}
		});
		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {

			if (!Utils.connected) {
				Toast.makeText(getActivity(), "Not conected",
						Toast.LENGTH_SHORT).show();
			} else {
				MainActivity.progressBar.setVisibility(ProgressBar.VISIBLE);
				String response = wifiConn.send(Utils.retrieveplaylistCmd);
				String[] videoNames = null;
				if (!response.equals("")) {
					videoNames = response.split("\n");
				}
				adapter = new PlayListAdapter(getActivity(), videoNames);
				MainActivity.progressBar.setVisibility(ProgressBar.INVISIBLE);
				lv.setAdapter(adapter);
				timerHandler.postDelayed(checkFileLog, checkInterval);
			}
		} else {
			if (timerHandler != null)
				timerHandler.removeCallbacks(checkFileLog);
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

}
