package omxplayer.remote.app.utils;

public class Utils {

	// for ssh
	public static final String uName = "pi";
	public static String hostName = "Hologram.local";
	public static final String password = "IDLabs2015";
	public static final String videosDir = "/home/pi/Videos/";
	public static final String adminPassword = "435227";

	// for wifi connection
	public static String SSID = "";
	public static String PSK = "";
	public static boolean connected = false;

	// ssh commands
	public interface SSHCommands{
		public static final String playCmd = "/home/pi/.scripts/play";
		public static final String pauseCmd = "/home/pi/.scripts/pause";
		public static final String incVolCmd = "/home/pi/.scripts/volup";
		public static final String decVolCmd = "/home/pi/.scripts/voldown";
		public static final String nextCmd = "killall omxplayer.bin";
		public static final String prevCmd = "/home/pi/.scripts/prev";
		public static final String fastForwardCmd = "/home/pi/.scripts/fast_forward";
		public static final String rewindCmd = "/home/pi/.scripts/rewind";
		public static final String fileSentCmd = "/home/pi/.scripts/commit_file ";
		public static final String removeCmd = "/home/pi/.scripts/remove_video ";
		public static final String retrieveplaylistCmd = "/home/pi/.scripts/retrieve_playlist";
		public static final String selectVideoCmd = "/home/pi/.scripts/select_video ";
		public static final String pauseLogCmd = "cat /tmp/pauselog";
		public static final String fileLogCmd = "cat /tmp/filelog";
		public static final String restartHostCmd = "sudo reboot";
		public static final String shutdownHostCmd = "sudo shutdown -h now";
		public static final String resetNetwork = "sudo /home/pi/.scripts/reset_network";
	}	
	// for sending videos state
	public static enum SendStatus {SUCCESS, FAILED, CANCELED};

}
