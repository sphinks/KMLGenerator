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
        scribbler.close();
    }

    private static void printHelp() {
        System.out.println("Usage:\njava KMLGenerator <tmcPath>");
    }
}
