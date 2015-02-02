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
    private String defaultStyleName;

    public static String tmcLocationStyleName = "tmc_location";

    public PolylinesKmlScribbler(Writer kmlStreamWriter) {

        this.kmlStreamWriter = kmlStreamWriter;
        this.isFolderOpened = false;
        this.isFooterOpen = false;
        this.defaultStyleName = "Default";

        addHeader(null);
        addDefaultStyles();
    }

    public PolylinesKmlScribbler(String filename) {

        this.isFolderOpened = false;
        this.isFooterOpen = false;
        this.defaultStyleName = "Default";

        open(filename);
        addHeader(filename);
        addDefaultStyles();
    }

    public void addHeader(String name) {
        // header already added?
        if (isFolderOpened)
            return;

        isFolderOpened = true;
        writeToStream(KMLTemplates.getHeader(name));
    }

    /**
     * Define a new named style for points
     * @param name of style
     * @param icon url to icon
     */
    public void defineIconStyle(String name, String icon) {
        writeToStream(KMLTemplates.getIconStyle(name, icon));
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
        writeToStream(KMLTemplates.getLineStyle(name, color, colorMode, width));
    }

    private void addDefaultStyles() {
        defineIconStyle(tmcLocationStyleName, "http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
        defineDefaultLineStyle("99ffffff", "random", 4);
    }

    /**
     * Draw line string under default style
     * @param name
     * @param desctiption
     * @param points
     */
    public void scribeGeoPolyline(String name, String desctiption, List<Point> points) {
        scribeGeoPolyline(name, desctiption, points, defaultStyleName);
    }

    /**
     * Draw line with defined style
     * @param name
     * @param desctiption
     * @param points
     * @param styleName
     */
    public void scribeGeoPolyline(String name, String desctiption, List<Point> points, String styleName) {
        writeToStream(KMLTemplates.getScribeGeoPolyline(name, desctiption, points, styleName));
    }

    /**
     * Draw point with empty style
     * @param name
     * @param lat
     * @param lon
     */
    public void pointPlacemark(String name, double lat, double lon) {
        pointPlacemark(name, lat, lon, null, null);
    }

    /**
     * Draw point with defined style
     * @param name
     * @param lat
     * @param lon
     * @param description
     * @param styleName
     */
    public void pointPlacemark(String name, double lat, double lon, String description, String styleName)
    {
        writeToStream(KMLTemplates.getPointPlacemark(name, lat, lon, description, styleName));
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

    /**
     * Open file to writer stream
     * @param fileName
     */
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

    /**
     * Write char content to writer stream
     * @param content
     */
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

    /**
     * Add footer of document
     */
    public void finishDocument() {
        if (isFolderOpened)
        {
            writeToStream("</Folder>");
            isFolderOpened = false;
        }

        if (!isFooterOpen)
            addFooter();
    }

    /**
     * Close Stream
     */
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
