import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: ivan
 * Date: 02.02.15
 * Time: 22:57
 */
public class KMLTemplates {

    /**
     * Define a new named style for points
     * @param name of style
     * @param icon url to icon
     */
    public static String getIconStyle(String name, String icon) {
        StringBuilder style = new StringBuilder();

        style.append(String.format("<Style id=\"%s\">", name));
        style.append("<IconStyle>");
        style.append("<scale>1</scale>");
        style.append("<Icon>");
        style.append("<href>" + icon + "</href>");
        style.append("</Icon>");
        style.append("</IconStyle>");
        style.append("</Style>");
        return style.toString();
    }

    /**
     * Define a new named style for line strings
     * @param name of style
     * @param color KML color in format aabbggrr,where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: 7fff0000, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.</param>
     * @param colorMode as in KML spec: normal or random
     * @param width Width of the line, in pixels
     */
    public static String getLineStyle(String name, String color, String colorMode, int width) {

        StringBuilder style = new StringBuilder();

        style.append(String.format("<Style id=\"%s\">", name));
        style.append("<LineStyle>");
        style.append(String.format("<color>%s</color>", color));
        if (colorMode != null && !colorMode.isEmpty())
            style.append(String.format("<colorMode>%s</colorMode>", colorMode));
        style.append("<width>" + width + "</width>");
        style.append("</LineStyle>");
        style.append("</Style>");
        return style.toString();
    }

    /**
     * Generate header of kml file
     * @param name
     * @return
     */
    public static String getHeader(String name) {

        StringBuilder envelop = new StringBuilder();
        envelop.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        envelop.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
        envelop.append("<Document>");

        if (name != null && !name.isEmpty())
        {
            envelop.append(String.format("<name>%s</name>", name));
        }

        envelop.append("<open>1</open>");
        envelop.append(String.format("<Folder><name>%s</name><open>1</open>", name));

        return envelop.toString();
    }

    /**
     * Draw line with defined style
     * @param name
     * @param desctiption
     * @param points
     * @param styleName
     */
    public static String getScribeGeoPolyline(String name, String desctiption, List<Point> points, String styleName) {

        StringBuilder placemark = new StringBuilder();

        placemark.append("<Placemark>");
        placemark.append(String.format("<name>%s</name>", name)); //TODO escape string in original
        if (desctiption != null && !desctiption.isEmpty())
            placemark.append(String.format("<description>%s</description>", desctiption)); //in original new XCData(desctiption)
        placemark.append(String.format("<styleUrl>%s</styleUrl>", styleName));
        placemark.append("<tessellate>1</tessellate>");

        placemark.append("<LineString>");
        placemark.append("<coordinates>");

        for (Point point:points) {
            double lon = new BigDecimal(String.valueOf(point.getLongitute())).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            double lat = new BigDecimal(String.valueOf(point.getLatitute())).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            placemark.append(lon + "," + lat);
            placemark.append(" ");
        }
        placemark.deleteCharAt(placemark.length()-1);
        placemark.append("</coordinates>");
        placemark.append("</LineString>");
        placemark.append("</Placemark>");

        return placemark.toString();
    }

    /**
     * Draw point with defined style
     * @param name
     * @param lat
     * @param lon
     * @param description
     * @param styleName
     */
    public static String getPointPlacemark(String name, double lat, double lon, String description, String styleName)
    {
        StringBuilder placemark = new StringBuilder();

        placemark.append("<Placemark>");
        if (name != null && !name.isEmpty()) {
            placemark.append(String.format("<name>%s</name>", name)); // TODO escape as in original
        }

        if (description != null && !description.isEmpty()) {
            placemark.append(String.format("<description>%s</description>", description)); // TODO escape as in original
        }

        if (styleName != null && !styleName.isEmpty()) {
            placemark.append(String.format("<styleUrl>%s</styleUrl>", styleName));
        }

        placemark.append("<Point>");
        placemark.append("<coordinates>" + lon + "," + lat + "</coordinates>");
        placemark.append("</Point>");
        placemark.append("</Placemark>");

        return placemark.toString();
    }
}
