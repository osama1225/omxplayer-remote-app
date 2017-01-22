package omxplayer.remote.app.adapters;

import java.util.ArrayList;
import java.util.List;

import omxplayer.remote.app.R;
import omxplayer.remote.app.adapters.viewholders.RemoveMediaViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListRemovalAdapter extends CustomAdapter<String> {

	private ArrayList<String> names;
	private Context c;
	private List<Integer> selectedIndecies;

	public VideoListRemovalAdapter(String[] names, Context c) {
		this.names = new ArrayList<String>();
		for (int i = 0; i < names.length - 1; i++) {
			String name = names[i];
			if (name.startsWith("@#%", 0))
				continue;
			this.names.add(name);
		}
		this.c = c;
		selectedIndecies = new ArrayList<Integer>(this.names.size());
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
	public void toggleFromSelectedIndecies(int selectedIndex) {
		if (selectedIndecies.contains(selectedIndex)) {
			selectedIndecies.remove(Integer.valueOf(selectedIndex));
		} else {
			selectedIndecies.add(selectedIndex);
		}
		notifyDataSetChanged();
	}

	@Override
	public List<Integer> getSelectedIndecies() {
		return selectedIndecies;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RemoveMediaViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(c).inflate(
					R.layout.video_item_view_remove, null);
			viewHolder = new RemoveMediaViewHolder();
			viewHolder.setNameTextView((TextView) convertView
					.findViewById(R.id.name_id3));
			viewHolder.setThumbnailImageView((ImageView) convertView
					.findViewById(R.id.cover_img3));
			viewHolder.setSelectedMarkImageView((ImageView) convertView
					.findViewById(R.id.selected_img_id3));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (RemoveMediaViewHolder) convertView.getTag();
		}
		String name = names.get(position).substring(
				names.get(position).lastIndexOf("/") + 1,
				names.get(position).length());
		viewHolder.getNameTextView().setText(name);

		ImageView thumbnailImageView = viewHolder.getThumbnailImageView();

		if (selectedIndecies != null && selectedIndecies.contains(position)) {
			viewHolder.getSelectedMarkImageView().setVisibility(
					ImageView.VISIBLE);
			thumbnailImageView.setAlpha(.5f);
		} else {
			viewHolder.getSelectedMarkImageView().setVisibility(
					ImageView.INVISIBLE);
			thumbnailImageView.setAlpha(1.0f);
		}

		return convertView;
	}

	@Override
	public void setItems(List<String> items) {
		if (items != null) {
			names.clear();
			names.addAll(items);
		}
	}

}
