package cz.mendelu.xotradov.hostsAdd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.fail;

public class TestHelper {
    public String getHostsContent(FileHandler fileHandler) {
        return fileHandler.getContent(fileHandler.getOutputFile());
    }

    public void createSafelyNewHostsFiles(String inputFilePath, String outputFilePath,String inputFileContent,String oldHostsContent) {
        try {
            createNewHostsFiles(inputFilePath,outputFilePath,inputFileContent,oldHostsContent);
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    public void createNewHostsFiles(String inputFilePath, String outputFilePath, String inputFileContent,String oldHostsContent) throws IOException {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        inputFile.createNewFile();
        outputFile.createNewFile();
        writeTextToFile(inputFileContent, inputFile);
        writeTextToFile(oldHostsContent,outputFile);
    }

    public void deleteHostsFiles(String inputFilePath, String outputFilePath) {
        new File(inputFilePath).delete();
        new File(outputFilePath).delete();
    }

    public void writeTextToFile(String s, File inputFile) throws IOException {
        FileWriter myWriter = new FileWriter(inputFile);
        myWriter.write(s);
        myWriter.close();
    }
}
