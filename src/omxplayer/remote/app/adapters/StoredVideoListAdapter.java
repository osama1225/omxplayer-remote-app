package omxplayer.remote.app.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import omxplayer.remote.app.VideoItem;
import omxplayer.remote.app.R;
import android.content.Context;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StoredVideoListAdapter extends CustomAdapter<VideoItem> {

	private ArrayList<VideoItem> videos;
	private Context c;
	private int selectedIndex;

	public StoredVideoListAdapter(Context c) {
		this.c = c;
		videos = new ArrayList<VideoItem>();
		selectedIndex = -1;
		retrieveVideos();
	}

	private void retrieveVideos() {
		String[] projection = { MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE };
		Cursor cursor = new CursorLoader(c,
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, null).loadInBackground();
		while (cursor.moveToNext()) {
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Video.Media.DATA));
			String name = cursor.getString(cursor
					.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
			Long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Video.Media.SIZE));
			videos.add(new VideoItem(name, path, ThumbnailUtils
					.createVideoThumbnail(path, Thumbnails.MICRO_KIND), size));
		}
		cursor.close();

		// scan internal storage.
		cursor = new CursorLoader(c,
				MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection, null,
				null, null).loadInBackground();

		while (cursor.moveToNext()) {
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Video.Media.DATA));
			String name = cursor.getString(cursor
					.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
			Long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Video.Media.SIZE));
			videos.add(new VideoItem(name, path, ThumbnailUtils
					.createVideoThumbnail(path, Thumbnails.MICRO_KIND), size));

		}
		cursor.close();

		for (int i = 0; i < videos.size(); i++) {
			if (!videos.get(i).getPath().endsWith(".mp4")) {
				videos.remove(i);
				i--;
			}
		}

		// sort them by name
		Collections.sort(videos);

	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		notifyDataSetChanged();
	}
	
	public ArrayList<VideoItem> getVideos() {
		return videos;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return videos.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(c).inflate(R.layout.video_item_view_send,
				null);
		((TextView) view.findViewById(R.id.name_id)).setText(videos.get(
				position).getName());
		Long actualSize = videos.get(position).getSize();
		double size = actualSize / 1024.0f;
		String unit = "Bytes";
		if (size <= 0)
			size = actualSize;
		else {
			unit = "KByte";
			size /= 1024;
			if (size <= 0)
				size *= 1024;
			else
				unit = "MB";

		}
		String sizeString = String.format("%.2f", size);

		((TextView) view.findViewById(R.id.size_id)).setText(sizeString + " "
				+ unit);
		ImageView cover = ((ImageView) view.findViewById(R.id.cover_img));
		cover.setImageBitmap(videos.get(position).getVideoImage());

		if (selectedIndex != -1 && selectedIndex == position) {
			((ImageView) view.findViewById(R.id.selected_img_id))
					.setVisibility(ImageView.VISIBLE);
			cover.setAlpha(150);
		} else
			((ImageView) view.findViewById(R.id.selected_img_id))
					.setVisibility(ImageView.INVISIBLE);

		return view;
	}

	@Override
	public void setItems(List<VideoItem> items) {
		if(items != null){
			videos.clear();
			videos.addAll(items);
		}
		
	}
}
