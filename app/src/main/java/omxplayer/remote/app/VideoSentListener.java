package omxplayer.remote.app;

import omxplayer.remote.app.utils.Utils.SendStatus;

public interface VideoSentListener {

	public void finishedSending(SendStatus state, String fileName);
	
}
