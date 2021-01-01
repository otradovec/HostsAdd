package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;

public class MainTest {

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
        deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz";
        String oldHostsContent = "0.0.0.0       ct24.ceskatelevize.cz\n" +
                "0.0.0.0       jenda.cz\n" +
                "0.0.0.0       zorka.eu\n";
        createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        File outputFile = main.getFileHandler().getOutputFile();
        File inputFile = main.getFileHandler().getInputFile();
        assertEquals(outputFilePath,outputFile.getPath());
        assertEquals(inputFilePath,inputFile.getPath());
        main.start();
        assertEquals("0.0.0.0       ct24.ceskatelevize.cz\n" +
                        "0.0.0.0       info.cz\n" +
                        "0.0.0.0       jenda.cz\n" +
                        "0.0.0.0       seznam.cz\n" +
                        "0.0.0.0       zorka.eu\n" +
                        "0.0.0.0       zpravy.magazinplus.cz",
                        getHostsContent(main.getFileHandler()));
    }

    @Test
    public void sameNewHosts(){
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newSameMultipleHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "https://ct24.ceskatelevize.cz/\n" +
                "http://seznam.cz\n" +
                "https://zpravy.magazinplus.cz/1961-predpoklady-se-potvrdily-puvod-koronaviru-je-stale-jasnejsi.html?utm_source=www.seznam.cz&utm_medium=sekce-z-internetu#dop_ab_variant=413311&dop_req_id=FkukFJgP7Se-202010190802&dop_source_zone_name=hpfeed.sznhp.box\n" +
                "info.cz\n" +
                "http://seznam.cz\n";
        String oldHostsContent = "0.0.0.0       ct24.ceskatelevize.cz\n" +
                "0.0.0.0       jenda.cz\n" +
                "0.0.0.0       zorka.eu\n";
        createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        File outputFile = main.getFileHandler().getOutputFile();
        File inputFile = main.getFileHandler().getInputFile();
        assertEquals(outputFilePath,outputFile.getPath());
        assertEquals(inputFilePath,inputFile.getPath());
        main.start();
        assertEquals("0.0.0.0       ct24.ceskatelevize.cz\n" +
                        "0.0.0.0       info.cz\n" +
                        "0.0.0.0       jenda.cz\n" +
                        "0.0.0.0       seznam.cz\n" +
                        "0.0.0.0       zorka.eu\n" +
                        "0.0.0.0       zpravy.magazinplus.cz",
                getHostsContent(main.getFileHandler()));
    }

    private String getHostsContent(FileHandler fileHandler) {
        return fileHandler.getContent(fileHandler.getOutputFile());
    }

    private void createSafelyNewHostsFiles(String inputFilePath, String outputFilePath,String inputFileContent,String oldHostsContent) {
        try {
            createNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    private void createNewHostsFiles(String inputFilePath, String outputFilePath, String inputFileContent,String oldHostsContent) throws IOException {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        inputFile.createNewFile();
        outputFile.createNewFile();
        writeTextToFile(inputFileContent, inputFile);
        writeTextToFile(oldHostsContent,outputFile);
    }

    private void deleteHostsFiles(String inputFilePath, String outputFilePath) {
        new File(inputFilePath).delete();
        new File(outputFilePath).delete();
    }

    private void writeTextToFile(String s, File inputFile) throws IOException {
        FileWriter myWriter = new FileWriter(inputFile);
        myWriter.write(s);
        myWriter.close();
    }
}