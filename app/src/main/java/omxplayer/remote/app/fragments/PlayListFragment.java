package omxplayer.remote.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import omxplayer.remote.app.R;
import omxplayer.remote.app.adapters.PlayListAdapter;
import omxplayer.remote.app.handlers.ConnectionServiceHandler;
import omxplayer.remote.app.network.CommandSender;
import omxplayer.remote.app.tasks.PlaylistRetrievalTask;
import omxplayer.remote.app.utils.Utils;

public class PlayListFragment extends Fragment {

    private final long checkInterval = 2000;
    private CommandSender commandSender;
    private ConnectionServiceHandler connectionServiceHandler;
    private ViewGroup rootView;
    private ListView lv;
    private PlayListAdapter playListAdapter;
    private PlaylistRetrievalTask playlistRetrievalTask;
    private Handler timerHandler;
    private Runnable checkFileLog;

    public PlayListFragment(CommandSender commandSender, ConnectionServiceHandler connectionServiceHandler) {
        this.commandSender = commandSender;
        this.connectionServiceHandler = connectionServiceHandler;
        playListAdapter = new PlayListAdapter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ini the timer
        checkFileLog = new Runnable() {

            @Override
            public void run() {
                if (!Utils.connected) {
                    return;
                }
                //ignore showing progressbar..cause this in an interval update while the view is shown
                playlistRetrievalTask = new PlaylistRetrievalTask(commandSender, playListAdapter, lv, getActivity(), false);
                playlistRetrievalTask.execute();
                timerHandler.postDelayed(checkFileLog, checkInterval);
            }
        };

        timerHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.videos_playlist_screen, container, false);
            lv = rootView.findViewById(R.id.listView2);
            rootView.setTag(lv);
        } else {
            lv = (ListView) rootView.getTag();
        }
        rootView.bringToFront();
        lv.bringToFront();
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final String selectedName = playListAdapter.getNames().get(arg2);
                playListAdapter.setCurrentlyPlaying(selectedName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        commandSender.send(Utils.SSHCommands.selectVideoCmd, selectedName);
                        connectionServiceHandler.changePlayState("p");
                    }
                }).start();
            }
        });
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (!Utils.connected) {
                Toast.makeText(getActivity(), "Not connected",
                        Toast.LENGTH_SHORT).show();
            } else {
                playlistRetrievalTask = new PlaylistRetrievalTask(commandSender, playListAdapter, lv, getActivity(), true);
                playlistRetrievalTask.execute();
                timerHandler.postDelayed(checkFileLog, checkInterval);
            }
        } else {
            if (timerHandler != null)
                timerHandler.removeCallbacks(checkFileLog);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

}
