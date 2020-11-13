package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    private FileHandler fileHandler;
    ArrayList<String> inputList = new ArrayList<>();
    public static void main(String[] args) {
         new Main(args[0],args[1]).start();
         System.out.println("Input file: "+ args[0]);
         System.out.println("main hosts file: "+ args[1]);
    }
    public Main (String inputFileName, String hostsFileName){
         fileHandler = new FileHandler(inputFileName,hostsFileName);
    }
    public void start() {
        File inputFile = fileHandler.getInputFile();
        File hostsFile = fileHandler.getOutputFile();
        handle(inputFile,hostsFile);
    }

    private void handle(File inputFile, File hostsFile) {
        try (FileInputStream inputStream = new FileInputStream(inputFile); Scanner sc = new Scanner(inputStream, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String cleanedLine = getCleaned(line);
                String rightLine= getHostsTypeLine(cleanedLine);
                if (!isPresent(rightLine,hostsFile)){
                    addLine(rightLine);
                }
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException ignored) {
            System.err.println("IO exception while reading a file");
        }
        Collections.sort(inputList);
        addLines(hostsFile);
    }

    private void addLines(File hostsFile) {
        try {
            String line = fileHandler.getMainHostsFile().readLine();
            while (bothFilesHaveLines(line)){
                //input is lower
                if (isFirstBeforeSecond(inputList.get(0),line)){
                    fileHandler.writeToTemp((inputList.get(0)+"\n").getBytes());
                    inputList.remove(0);
                }else {
                    //line is lower
                    fileHandler.writeToTemp((line+"\n").getBytes());
                    line = fileHandler.getMainHostsFile().readLine();
                }
            }
            resolvePossibleRemainingLinesInOneOfTheFiles(fileHandler.getMainHostsFile(), line);
            fileHandler.closeAllFiles();
            fileHandler.copyTempToFileUsingChannel(hostsFile);
        } catch (Exception e) {
                fileHandler.closeAllFiles();
        }
    }

    private void resolvePossibleRemainingLinesInOneOfTheFiles
            (RandomAccessFile mainHostsFile, String line) throws IOException {
        if ((line) != null){
            while ((line) != null || !inputList.isEmpty()){
                fileHandler.writeToTemp((line+"\n").getBytes());
                line = mainHostsFile.readLine();
            }
        }else if (!inputList.isEmpty()){
            while ((line) != null || !inputList.isEmpty()){
                fileHandler.writeToTemp((inputList.get(0)+"\n").getBytes());
                inputList.remove(0);
            }
        }
    }

    private boolean bothFilesHaveLines(String line) {
        return (line) != null && !inputList.isEmpty();
    }

    public static boolean isFirstBeforeSecond(String first, String second) {
        return first.compareTo(second)<0;
    }

    private boolean isPresent(String rightLine, File hostsFile) {
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

    private void addLine(String rightLine) {
        inputList.add(rightLine);
    }

    private String getCleaned(String line){
        String newLine = line;
        if (newLine.contains("https://")){
            newLine = newLine.replace("https://","");
        }else if (newLine.contains("http://")){
            newLine = newLine.replace("http://","");
        }
        if (newLine.contains("/")){
            newLine = newLine.substring(0,newLine.indexOf("/"));
        }
        return newLine;
    }
    private String getHostsTypeLine(String cleanedLine) {
         return "0.0.0.0       "+cleanedLine;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
