package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;

import java.io.File;

import static cz.mendelu.xotradov.hostsAdd.MainTest.oldHostsContent;
import static org.junit.Assert.*;

public class FileHandlerTest {
    public TestHelper testHelper = new TestHelper();

    @Test
    public void getInputFile() {
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        File inputFile = main.getFileHandler().getInputFile();
        assertEquals(inputFilePath,inputFile.getPath());
    }

    @Test
    public void getOutputFile() {
        String inputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt";
        String outputFilePath = "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt";
        testHelper.deleteHostsFiles(inputFilePath,outputFilePath);
        String inputFileContent = "info.cz";
        testHelper.createSafelyNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        Main main = new Main(inputFilePath,outputFilePath);
        File outputFile = main.getFileHandler().getOutputFile();
        assertEquals(outputFilePath,outputFile.getPath());
    }
}