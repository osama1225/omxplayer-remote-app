# omxplayer-remote-app
An Android app that connects to customized raspberry pi device via SSH and controls custom video player based on OMXPlayer by executing some [bash scripts](https://github.com/osama1225/omxplayer-bash-scripts).</br>
Its main features:
- Connect to the wifi network that has the Pi device.
- Play/Pause current video, next/previous video, and increase/decrease volume.
- Fastforward/rewind for 30secs by long press
- Show the Playlist and select a video to play.
- Send videos from the app to the player.
- Remove videos from the player (remove the actual video files from the pi).
- Show on screen notification for quick controls when the screen is off.
- Close the Pi device.

[screenshots](https://drive.google.com/drive/folders/0B17oSX7YuAWOfmhqQVNiOWxpWnVuQmVrNUtqSFhlQzhfRkRYVlpxaTMtXzAtcnVVQndKRXc?resourcekey=0-czQ3_dScPy8MSJAf2Rtapw)


#### About the Customized Raspberry Pi image:
A jessie lite image is installed on raspberry pi 3 then the following customizations are added:
- Install openjdk 7
- Hiding the console log that appears on startup.
- Auto login
- Wifi configuration to make the pi acts as access point (make the pi create its own network)
- Auto start of bash scripts on boot that resposnible for opening the video player.
- Auto mount for attached usbs

### NOTE: All images are provided by [IDLabs](http://id-labs.org/)
