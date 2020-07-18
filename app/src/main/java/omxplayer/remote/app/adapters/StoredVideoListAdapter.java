package omxplayer.remote.app.adapters;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import omxplayer.remote.app.R;
import omxplayer.remote.app.VideoItem;
import omxplayer.remote.app.adapters.viewholders.SendMediaViewHolder;

public class StoredVideoListAdapter extends CustomAdapter<VideoItem> {

    private ArrayList<VideoItem> videos;
    private Context c;

    public StoredVideoListAdapter(Context c) {
        this.c = c;
        videos = new ArrayList<VideoItem>();
        retrieveVideos();
    }

    private void retrieveVideos() {
        String[] projection = {MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE};
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SendMediaViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.video_item_view_send,
                    null);
            viewHolder = new SendMediaViewHolder();
            viewHolder.setNameTextView((TextView) convertView.findViewById(R.id.name_id));
            viewHolder.setSizeTextView((TextView) convertView.findViewById(R.id.size_id));
            viewHolder.setThumbnailImageView((ImageView) convertView.findViewById(R.id.cover_img));
            viewHolder.setSelectedMarkImageView((ImageView) convertView.findViewById(R.id.selected_img_id));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SendMediaViewHolder) convertView.getTag();
        }

        viewHolder.getNameTextView().setText(videos.get(
                position).getName());
        Long actualSize = videos.get(position).getSize();
        double size = actualSize / 1024.0f;
        String unit = "Bytes";
        if (size <= 0) {
            size = actualSize;
        } else {
            unit = "KByte";
            size /= 1024;
            if (size <= 0) {
                size *= 1024;
            } else {
                unit = "MB";
            }

        }
        String sizeString = String.format("%.2f", size);
        viewHolder.getSizeTextView().setText(sizeString + " "
                + unit);
        ImageView thumbnailImageView = viewHolder.getThumbnailImageView();
        thumbnailImageView.setImageBitmap(videos.get(position).getVideoImage());

        if (selectedIndecies != null && selectedIndecies.contains(position)) {
            viewHolder.getSelectedMarkImageView().setVisibility(ImageView.VISIBLE);
            thumbnailImageView.setAlpha(0.5f);
        } else {
            viewHolder.getSelectedMarkImageView().setVisibility(ImageView.INVISIBLE);
            thumbnailImageView.setAlpha(1.0f);
        }

        return convertView;
    }

    @Override
    public void setItems(List<VideoItem> items) {
        if (items != null) {
            videos.clear();
            videos.addAll(items);
        }
    }

    @Override
    public VideoItem[] getSelectedItems() {
        VideoItem[] selectedItems = null;
        if (!selectedIndecies.isEmpty()) {
            selectedItems = new VideoItem[selectedIndecies.size()];
            int resultIndex = 0;
            for (int index : selectedIndecies) {
                selectedItems[resultIndex++] = (VideoItem) getItem(index);
            }
        }
        return selectedItems;
    }
}
