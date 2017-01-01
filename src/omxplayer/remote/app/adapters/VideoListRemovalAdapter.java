package omxplayer.remote.app.adapters;

import java.util.ArrayList;

import omxplayer.remote.app.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListRemovalAdapter extends CustomAdapter {

	private ArrayList<String> names;
	private Context c;
	private int selectedIndex;

	public VideoListRemovalAdapter(String[] names, Context c) {
		this.names = new ArrayList<String>();
		for (int i=0;i<names.length-1; i++) {
			String name = names[i];
			if (name.startsWith("@#%", 0))
				continue;
			this.names.add(name);
		}
		this.c = c;
		selectedIndex = -1;
	}

	@Override
	public int getCount() {
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		notifyDataSetChanged();
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(c).inflate(
				R.layout.video_item_view_remove2, null);
		String name = names.get(position).substring(
				names.get(position).lastIndexOf("/") + 1, names.get(position).length());
		((TextView) view.findViewById(R.id.name_id3)).setText(name);

		ImageView cover = ((ImageView) view.findViewById(R.id.cover_img3));

		if (selectedIndex != -1 && selectedIndex == position) {
			((ImageView) view.findViewById(R.id.selected_img_id3))
					.setVisibility(ImageView.VISIBLE);
			cover.setAlpha(150);
		} else
			((ImageView) view.findViewById(R.id.selected_img_id3))
					.setVisibility(ImageView.INVISIBLE);

		return view;
	}

}
