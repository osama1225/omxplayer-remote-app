package omxplayer.remote.app.adapters.viewholders;

import android.widget.ImageView;
import android.widget.TextView;

public class RemoveMediaViewHolder {

    private TextView nameTextView;
    private ImageView thumbnailImageView;
    private ImageView selectedMarkImageView;

    public TextView getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(TextView nameTextView) {
        this.nameTextView = nameTextView;
    }

    public ImageView getThumbnailImageView() {
        return thumbnailImageView;
    }

    public void setThumbnailImageView(ImageView thumbnailImageView) {
        this.thumbnailImageView = thumbnailImageView;
    }

    public ImageView getSelectedMarkImageView() {
        return selectedMarkImageView;
    }

    public void setSelectedMarkImageView(ImageView selectedMarkImageView) {
        this.selectedMarkImageView = selectedMarkImageView;
    }

}
