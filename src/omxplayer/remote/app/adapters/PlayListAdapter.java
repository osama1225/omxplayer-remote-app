package omxplayer.remote.app.adapters;

import java.util.ArrayList;

import omxplayer.remote.app.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayListAdapter extends BaseAdapter {

	private Context c;
	private ArrayList<String> names;
	private String currentlyPlaying;

	public PlayListAdapter(Context c, String[] paths) {
		this.c = c;
		names = new ArrayList<String>();
		updateList(paths);
	}

	@Override
	public int getCount() {
		return names.size() - 1;
	}

	@Override
	public Object getItem(int arg0) {
		return names.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setCurrentlyPlaying(String currentlyPlaying) {
		this.currentlyPlaying = currentlyPlaying;
		notifyDataSetChanged();
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public void updateList(String[] paths) {
		if (paths == null || paths.length == 0)
			return;
		names = new ArrayList<String>();
		for (String name : paths) {
			if (name.startsWith("@#%", 0))
				continue;
			if (name.endsWith(".mp4") || name.endsWith(".3gp")
					|| name.endsWith(".mkv") || name.endsWith(".avi")
					|| name.endsWith(".mov") || name.endsWith(".mpeg")
					|| name.endsWith(".m4v") || name.endsWith(".rmvb")) {
				names.add(name);
			}
		}
		if (names.size() > 0)
			currentlyPlaying = names.get(names.size() - 1);
		notifyDataSetChanged();

	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		View view = LayoutInflater.from(c).inflate(
				R.layout.video_item_view_select, null);
		TextView tv = (TextView) view.findViewById(R.id.video_name);
		String name = names.get(arg0).substring(
				names.get(arg0).lastIndexOf("/") + 1, names.get(arg0).length());
		tv.setText(name);
		if (!names.get(arg0).equals(currentlyPlaying))
			tv.setAlpha(.6f);
		return view;

	}
}
