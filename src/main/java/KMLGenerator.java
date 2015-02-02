import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ivan
 * Date: 02.02.15
 * Time: 0:23
 */
public class KMLGenerator {

    public static void main(String[] args) {

        PolylinesKmlScribbler scribbler = new PolylinesKmlScribbler("testFile.xml");
        scribbler.pointPlacemark("15",6.846,51.9291,"Name=Vreden&lt;br/&gt;" +
        "Road=&lt;br/&gt;" +
        "NegOffset=22&lt;br/&gt;" +
        "PosOffset=16", "tmc_location");
        scribbler.pointPlacemark("15",6.8412,52.0345,"Name=Ahaus-Alstätte&lt;br/&gt;" +
                "Road=&lt;br/&gt;" +
                "NegOffset=22&lt;br/&gt;" +
                "PosOffset=16", "tmc_location");
        List<Point> pointList = new ArrayList<Point>();
        pointList.add(new Point(15, "Name", "road", 6.846, 51.9291,0,0));
        pointList.add(new Point(16, "Name", "road", 7.846, 52.9291,0,0));
        pointList.add(new Point(17, "Name", "road", 8.846, 53.9291,0,0));
        scribbler.scribeGeoPolyline("name", "description", pointList);
        scribbler.close();
    }

    private static void printHelp() {
        System.out.println("Usage:\njava KMLGenerator <tmcPath>");
    }
}
