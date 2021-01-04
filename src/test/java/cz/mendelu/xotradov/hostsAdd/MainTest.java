package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    public static String oldHostsContent =
            "0.0.0.0       ct24.ceskatelevize.cz\n" +
                    "0.0.0.0       jenda.cz\n" +
                    "0.0.0.0       zorka.eu\n";
    public static String resultHosts =
            "0.0.0.0       ct24.ceskatelevize.cz\n" +
                    "0.0.0.0       info.cz\n" +
                    "0.0.0.0       jenda.cz\n" +
                    "0.0.0.0       seznam.cz\n" +
                    "0.0.0.0       zorka.eu\n" +
                    "0.0.0.0       zpravy.magazinplus.cz";

    public TestHelper testHelper = new TestHelper();

    @Test
    public void isFirstBeforeSecond() {
        assertTrue(Main.isFirstBeforeSecond("0.0.0.0 a.cz","0.0.0.0 b.cz"));
        assertTrue(Main.isFirstBeforeSecond("0.0.0.0 a.eu","0.0.0.0 b.cz"));
        assertTrue(Main.isFirstBeforeSecond("0.0.0.0 b.cz","0.0.0.0 b.eu"));
        assertTrue(Main.isFirstBeforeSecond("0.0.0.0 azzzzzz.cz","0.0.0.0 b.cz"));
        assertFalse(Main.isFirstBeforeSecond("0.0.0.0 b.cz","0.0.0.0 b.cz"));
        assertFalse(Main.isFirstBeforeSecond("0.0.0.0 c.cz","0.0.0.0 b.cz"));
        assertFalse(Main.isFirstBeforeSecond("0.0.0.0 bbbb.bbb.bb","0.0.0.0 a.cz"));
    }

    @Test
    public void start() {
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    @Test
    public void sameNewHosts(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newSameMultipleHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz\n" +
                "http://seznam.cz\n";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    @Test
    public void blankLineInInput(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "\n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    @Test
    public void blankLines(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "\n" +
                "http://seznam.cz\n" +
                "\n" +
                "\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    @Test
    public void lineWithSpaceOnly(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                " \n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    @Test
    public void linesWithSpacesOnly(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "     \n" +
                "http://seznam.cz\n" +
                " \n" +
                "        \n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
        assertEquals(resultHosts, testHelper.getHostsContent(main.getFileHandler()));
    }

    /*@Test
    public void alternativeMain(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/urlToAdd";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/HOSTS.txt";
        Main main = new Main(inputFilePath,outputFilePath);
        main.start();
    }*/
}