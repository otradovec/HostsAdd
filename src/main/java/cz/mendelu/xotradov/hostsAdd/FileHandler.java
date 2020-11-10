package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileHandler {
    private File inputFile;
    private File outputFile;
    private File tempFile;
    private RandomAccessFile raInputFile;
    private RandomAccessFile raTempOutFile;
    private RandomAccessFile raOutputFile;
    FileHandler(String inputFilePath,String outputFilePath){
        try {
            if(areGoodFiles(inputFilePath,outputFilePath)){
                inputFile = new File(inputFilePath);
                raInputFile = new RandomAccessFile(inputFile,"r");
                outputFile = new File(outputFilePath);
                raOutputFile = new RandomAccessFile(outputFile, "rw");
                tempFile = new File(outputFilePath + "~");
                tempFile.delete();
                raTempOutFile = new RandomAccessFile(tempFile, "rw");
                raOutputFile.seek(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean areGoodFiles(String inputFilePath, String outputFilePath) {
        File inputFile = new File(inputFilePath);
        File hostsFile = new File(outputFilePath);
        if (inputFile.exists()) {
            if (inputFile.canRead()){
                if (hostsFile.exists()) {
                    if (hostsFile.canWrite()){
                         return true;
                    }else {
                        System.err.println("The second file cannot be written to.");
                    }
                } else {
                    System.err.println("The second file does not exist. Second argument is file to be read from.");
                }
            }else {
                System.err.println("The first file cannot be read.");
            }
        } else {
            System.err.println("The first file does not exist. First argument is file to be read from.");
        }
        return false;
    }

    public RandomAccessFile getRAInputFile() {
        return raInputFile;
    }
    public File getInputFile() {
        return inputFile;
    }

    public RandomAccessFile getMainHostsFile() {
        return raOutputFile;
    }

    public void writeToTemp(byte[] bytes) throws IOException {
        raTempOutFile.write(bytes);
    }

    public void closeAllFiles() {
        try {
            raTempOutFile.close();
            raOutputFile.close();
            raInputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void copyTempToFileUsingChannel(File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(tempFile).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            long sourceSize = sourceChannel.size();
            destChannel.transferFrom(sourceChannel, 0, sourceSize);
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    public RandomAccessFile getRAOutputFile() {
        return raOutputFile;
    }
    public File getOutputFile() {
        return outputFile;
    }
}
