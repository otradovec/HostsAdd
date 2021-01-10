package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;

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
        if (!raOutputFile.getChannel().isOpen()) {
            try {
                raOutputFile = new RandomAccessFile(outputFile, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return raOutputFile;
    }

    public void writeToTemp(byte[] bytes) throws IOException {
        if (!raTempOutFile.getChannel().isOpen()){
            tempFile.delete();
            raTempOutFile = new RandomAccessFile(tempFile, "rw");
        }
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

    public String getContent(File file){
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile= new RandomAccessFile(file,"r");
            String result = getUnsafeContent(randomAccessFile);
            randomAccessFile.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getUnsafeContent(RandomAccessFile randomAccessFile) throws IOException {
        String newLine = randomAccessFile.readLine();
        String result = "";
        if (newLine !=null){
            result = result.concat(newLine);
            newLine = randomAccessFile.readLine();
            while (newLine != null){
                result = result.concat("\n");
                result = result.concat(newLine);
                newLine = randomAccessFile.readLine();
            }
        }
        return result;
    }

    public void writeToTemp(String s) throws IOException {
        writeToTemp(s.getBytes());
    }

    boolean isPresent(String rightLine, File hostsFile) {
        try (FileInputStream inputStream = new FileInputStream(hostsFile); Scanner sc = new Scanner(inputStream, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.equals(rightLine)){
                    return true;
                }
            }
            return false;
        } catch (IOException ignored) {
            return false;
        }
    }

    public String getHostsLine() throws IOException {
        return raOutputFile.readLine();
    }
}
