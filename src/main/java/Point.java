/**
 * @Author: ivan
 * Date: 01.02.15
 * Time: 20:30
 */
public class Point {

    private int locationCode;
    private String locationName;
    private String roadName;
    private double latitute;
    private double longitute;

    private int negativeOffset;
    private int positiveOffset;

    public Point(int locationCode, String locationName, String roadName, double latitute, double longitute,
                 int negativeOffset, int positiveOffset) {
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.roadName = roadName;
        this.latitute = latitute;
        this.longitute = longitute;
        this.negativeOffset = negativeOffset;
        this.positiveOffset = positiveOffset;
    }

    public double getLatitute() {
        return latitute;
    }

    public double getLongitute() {
        return longitute;
    }
}
