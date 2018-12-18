package omxplayer.remote.app.adapters.viewholders;

import android.widget.ImageView;
import android.widget.TextView;

public class SendMediaViewHolder {

	private TextView nameTextView;
	private TextView sizeTextView;
	private ImageView thumbnailImageView;
	private ImageView selectedMarkImageView;

	public void setNameTextView(TextView nameTextView) {
		this.nameTextView = nameTextView;
	}

	public void setSizeTextView(TextView sizeTextView) {
		this.sizeTextView = sizeTextView;
	}

	public void setThumbnailImageView(ImageView thumbnailImageView) {
		this.thumbnailImageView = thumbnailImageView;
	}

	public void setSelectedMarkImageView(ImageView selectedMarkImageView) {
		this.selectedMarkImageView = selectedMarkImageView;
	}

	public TextView getNameTextView() {
		return nameTextView;
	}

	public TextView getSizeTextView() {
		return sizeTextView;
	}

	public ImageView getThumbnailImageView() {
		return thumbnailImageView;
	}

	public ImageView getSelectedMarkImageView() {
		return selectedMarkImageView;
	}
}
