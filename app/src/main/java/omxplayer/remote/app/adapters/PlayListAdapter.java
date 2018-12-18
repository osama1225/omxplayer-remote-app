package omxplayer.remote.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import omxplayer.remote.app.R;

public class PlayListAdapter extends BaseAdapter {

    private Context c;
    private ArrayList<String> names;
    private String currentlyPlaying;

    public PlayListAdapter() {
        c = null;
        names = new ArrayList<>();
        currentlyPlaying = null;
    }

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
        if (names.size() > 0) {
            //the last element in the list is the current playing video
            currentlyPlaying = names.get(names.size() - 1);
        }
        notifyDataSetChanged();

    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(
                    R.layout.video_item_view_select, null);
            textView = (TextView) convertView.findViewById(R.id.video_name);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        String name = names.get(arg0).substring(
                names.get(arg0).lastIndexOf("/") + 1, names.get(arg0).length());
        textView.setText(name);
        if (!names.get(arg0).equals(currentlyPlaying)) {
            textView.setAlpha(.6f);
        } else {
            textView.setAlpha(1f);
        }
        return convertView;
    }

    public void setContext(Context c) {
        this.c = c;
    }

    public void setNamesAndRefresh(String[] names) {
        updateList(names);
    }
}
