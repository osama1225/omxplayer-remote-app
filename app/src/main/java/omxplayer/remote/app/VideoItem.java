package omxplayer.remote.app;

import android.graphics.Bitmap;

import java.io.Serializable;

public class VideoItem implements Comparable<VideoItem>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String path;
    private Bitmap videoImage;
    private Long size;

    public VideoItem(String name, String path, Bitmap videoImage, Long size) {
        this.name = name;
        this.videoImage = videoImage;
        this.size = size;
        this.path = path;
    }

//	private synchronized void writeObject(final ObjectOutputStream out)
//			throws Exception {
//		// Serialize everything but the image
//		out.defaultWriteObject();
//
//		// Now serialize the image
//		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//		videoImage.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
//		byte bitmapBytes[] = byteStream.toByteArray();
//		out.write(bitmapBytes, 0, bitmapBytes.length);
//	}
//
//	private void readObject(ObjectInputStream in) throws Exception,
//			ClassNotFoundException {
//		// Read everything but the image
//		in.defaultReadObject();
//
//		// Now read the image
//		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//		int b;
//		while ((b = in.read()) != -1)
//			byteStream.write(b);
//		byte bitmapBytes[] = byteStream.toByteArray();
//		videoImage = BitmapFactory.decodeByteArray(bitmapBytes, 0,
//				bitmapBytes.length);
//	}

    public VideoItem() {
        // TODO Auto-generated constructor stub
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Bitmap getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(Bitmap videoImage) {
        this.videoImage = videoImage;
    }

    @Override
    public int compareTo(VideoItem o) {
        return this.name.compareTo(o.getName());
    }
}
