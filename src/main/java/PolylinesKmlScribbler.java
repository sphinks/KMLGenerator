import java.io.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: ivan
 * Date: 01.02.15
 * Time: 20:39
 */
public class PolylinesKmlScribbler {

    private Writer kmlStreamWriter;
    private boolean isFolderOpened;
    private boolean isFooterOpen;
    private String folderName;
    private String defaultStyleName;
    private boolean ownStream;

    public static String tmcLocationStyleName = "tmc_location";

    public PolylinesKmlScribbler(Writer kmlStreamWriter) {

        this.kmlStreamWriter = kmlStreamWriter;
        this.isFolderOpened = false;
        this.isFooterOpen = false;
        this.folderName = "";
        this.defaultStyleName = "Default";
        this.ownStream = true;

        addHeader(null);
        addDefaultStyles();
    }

    public PolylinesKmlScribbler(String filename) {

        this.isFolderOpened = false;
        this.isFooterOpen = false;
        this.folderName = "";
        this.defaultStyleName = "Default";
        this.ownStream = true;

        open(filename);
        addHeader(null);
        addDefaultStyles();
    }

    public void addHeader(String name) {
        // header already added?
        if (isFolderOpened)
            return;

        StringBuilder envelop = new StringBuilder();
        envelop.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        envelop.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
        envelop.append("<Document>");

        if (name != null && !name.isEmpty())
        {
            envelop.append("<name>" + name + "</name>");
        }

        envelop.append("<open>1</open>");
        envelop.append("<Folder><name>" + name + "</name><open>1</open>");
        isFolderOpened = true;

        writeToStream(envelop.toString());
    }

    /**
     * Define a new named style for points
     * @param name of style
     * @param icon url to icon
     */
    public void defineIconStyle(String name, String icon) {
        StringBuilder style = new StringBuilder();

        style.append("<Style id=\"" + name + "\">");
        style.append("<IconStyle>");
        style.append("<scale>1</scale>");
        style.append("<Icon>");
        style.append("<href>" + icon + "</href>");
        style.append("</Icon>");
        style.append("</IconStyle>");
        style.append("</Style>");
        writeToStream(style.toString());
    }

    /**
     * Set default LineStyle
     * @param color
     * @param colorMode
     * @param width
     */
    public void defineDefaultLineStyle(String color, String colorMode, int width) {
        defineLineStyle(defaultStyleName, color, colorMode, width);
    }

    /**
     * Define a new named style for line strings
     * @param name of style
     * @param color KML color in format aabbggrr,where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, if you want to apply a blue color with 50 percent opacity to an overlay, you would specify the following: 7fff0000, where alpha=0x7f, blue=0xff, green=0x00, and red=0x00.</param>
     * @param colorMode as in KML spec: normal or random
     * @param width Width of the line, in pixels
     */
    public void defineLineStyle(String name, String color, String colorMode, int width) {

        StringBuilder style = new StringBuilder();

        style.append("<Style id=\"" + name + "\">");
        style.append("<LineStyle>");
        style.append("<color>" + color + "</color>");
        if (colorMode != null && !colorMode.isEmpty())
            style.append("<colorMode>" + colorMode + "</colorMode>");
        style.append("<width>" + width + "</width>");
        style.append("</LineStyle>");
        style.append("</Style>");
        writeToStream(style.toString());
    }

    private void addDefaultStyles() {
        defineIconStyle(tmcLocationStyleName, "http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
        defineDefaultLineStyle("99ffffff", "random", 4);
    }

    /**
     * Draw line string under default style
     * @param name
     * @param desctiption
     * @param iGeometry
     */
    public void scribeGeoPolyline(String name, String desctiption, List<Point> iGeometry) {
        scribeGeoPolyline(name, desctiption, iGeometry, defaultStyleName);
    }

    public void scribeGeoPolyline(String name, String desctiption, List<Point> iGeometry, String styleName) {
        StringBuilder placemark = new StringBuilder();

        placemark.append("<Placemark>");
        placemark.append("<name>" + name + "</name>"); //TODO escape string in original
        if (desctiption != null && !desctiption.isEmpty())
            placemark.append("<description>" + desctiption + "</description>"); //in original new XCData(desctiption)
        placemark.append("<styleUrl>" + styleName + "</styleUrl>");
        placemark.append("<tessellate>1</tessellate>");

        placemark.append("<LineString>");
        placemark.append("<coordinates>");

        for (Point point:iGeometry) {
            double lon = new BigDecimal(String.valueOf(point.getLongitute())).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            double lat = new BigDecimal(String.valueOf(point.getLatitute())).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            placemark.append(lon + "," + lat);
        }

        placemark.append("</coordinates>");
        placemark.append("</LineString>");
        placemark.append("</Placemark>");

        writeToStream(placemark.toString());
    }

    public void pointPlacemark(String name, double lat, double lon) {
        pointPlacemark(name, lat, lon, null, null);
    }

    public void pointPlacemark(String name, double lat, double lon, String description, String styleName)
    {
        StringBuilder placemark = new StringBuilder();

        placemark.append("<Placemark>");
        if (name != null && !name.isEmpty()) {
            placemark.append("<name>" + name + "</name>"); // TODO escape as in original
        }

        if (description != null && !description.isEmpty()) {
            placemark.append("<description>" + description + "</description>"); // TODO escape as in original
        }

        if (styleName != null && !styleName.isEmpty()) {
            placemark.append("<styleUrl>" + styleName + "</styleUrl>");
        }

        placemark.append("<Point>");
        placemark.append("<coordinates>" + lon + "," + lat + "</coordinates>");
        placemark.append("</Point>");
        placemark.append("</Placemark>");

        writeToStream(placemark.toString());
    }

    public void tmcLocation(int code, String name, double lat, double lon) {
        pointPlacemark(String.valueOf(code),
                lat, lon,
                "Location code: <b>" + String.valueOf(code) + "</b><br/>Road: " + name,
                "#tmc_location");
    }

    public void addFooter() {
        if (isFolderOpened)
        {
            writeToStream("</Folder>");
            isFolderOpened = false;
        }

        writeToStream("</Document></kml>");
        isFooterOpen = false;
    }

    private void open(String fileName) {
        // already opened?
        if (kmlStreamWriter != null)
            return;

        if (fileName == null || fileName.isEmpty())
            throw new NullPointerException("FileName is empty");

        try
        {
            File inputFile = new File(fileName);
            if(!inputFile.exists()) {
                inputFile.createNewFile();
            }

            try {
                kmlStreamWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(inputFile), "utf-8"));
            } catch (IOException ex) {
                System.out.println("Error while creating writer: " + ex.getMessage());
                try {
                    kmlStreamWriter.close();
                } catch (IOException exInternal) {
                    System.out.println("Error while closing writer: " + exInternal.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("An error occurs in GeoKmlBuilder while starting file: " +
                    fileName + "; Error: " + ex.getMessage());
        }
    }

    private void writeToStream(String content) {

        try {
            if (kmlStreamWriter != null)
            {
                kmlStreamWriter.write(content);
            }
        }catch (IOException ex) {
            try {
                if (kmlStreamWriter != null) {
                    kmlStreamWriter.close();
                }
            }catch (IOException exInternal) {
                System.out.println("Error while closing output stream: " + exInternal.getMessage());
            }
        }
    }

    public void finishDocument() {
        if (isFolderOpened)
        {
            writeToStream("</Folder>");
            isFolderOpened = false;
        }

        if (!isFooterOpen)
            addFooter();
    }

    public void close() {

        finishDocument();
        try {
            if (kmlStreamWriter != null)
            {
                kmlStreamWriter.flush();
            }
        }catch (IOException ex) {
            System.out.println("Error while closing file: " + ex.getMessage());
        }finally {
            try {
                if (kmlStreamWriter != null) {
                    kmlStreamWriter.close();
                }
            }catch (IOException ex) {
                System.out.println("Error while closing output stream: " + ex.getMessage());
            }
        }
    }

}
