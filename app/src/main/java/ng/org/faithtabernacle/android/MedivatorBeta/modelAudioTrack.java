package ng.org.faithtabernacle.android.MedivatorBeta;

public class modelAudioTrack {
    private String trackTitle;
    private String trackUrl;

    public modelAudioTrack(String trackTitle, String trackUrl) {
        this.trackTitle = trackTitle;
        this.trackUrl = trackUrl;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }
}