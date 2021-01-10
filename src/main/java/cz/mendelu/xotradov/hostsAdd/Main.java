package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        addGoodInputLinesToList(inputFile, hostsFile);
        Collections.sort(inputList);
        addLines(hostsFile);
        handleWWW(hostsFile);
    }

    private void addGoodInputLinesToList(File inputFile, File hostsFile) {
        try (FileInputStream inputStream = new FileInputStream(inputFile); Scanner sc = new Scanner(inputStream, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (isURL(line)){
                    String cleanedLine = getCleaned(line);
                    String rightLine= getHostsTypeLine(cleanedLine);
                    if (isGoodToAdd(rightLine,hostsFile)){
                        addLine(rightLine);
                    }
                }
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException ignored) {
            System.err.println("IO exception while reading a file");
        }
    }

    private void handleWWW(File hostsFile) {
        ArrayList<String> list = getWWWLines(hostsFile);
        addLines(list);
    }

    private ArrayList<String> getWWWLines(File file) {
        ArrayList<String> resultList = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(file); Scanner sc = new Scanner(inputStream, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!startsWithwwwSubdomain(line)){
                    resultList.add(getLineWithwwwSubdomain(line));
                }
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException ignored) {
            System.err.println("IO exception while reading a file");
        }
        return  resultList;
    }

    private String getLineWithwwwSubdomain(String line) {
        return getHostsPrefix()+"www."+line.substring(getHostsPrefix().length());
    }

    private boolean startsWithwwwSubdomain(String line) {
        return line.startsWith(getHostsPrefix()+"www.");
    }

    private String getHostsPrefix() {
        return "0.0.0.0       ";
    }

    private void addLines(ArrayList<String> list) {
        try {
            String line = fileHandler.getMainHostsFile().readLine();
            while (bothFilesHaveLines(line,list)){
                if (list.get(0).equals(line)){
                    list.remove(0);
                    fileHandler.writeToTemp((line+"\n"));
                    line = fileHandler.getMainHostsFile().readLine();
                }
                else if (isFirstBeforeSecond(list.get(0),line)){
                        fileHandler.writeToTemp((list.get(0)+"\n"));
                        list.remove(0);
                    }else {
                        //line is lower
                        fileHandler.writeToTemp((line+"\n"));
                        line = fileHandler.getMainHostsFile().readLine();
                }
            }
            resolvePossibleRemainingLinesInOneOfTheFiles(fileHandler.getMainHostsFile(), line);
            fileHandler.closeAllFiles();
            fileHandler.copyTempToFileUsingChannel(fileHandler.getOutputFile());
        } catch (Exception e) {
            fileHandler.closeAllFiles();
        }
    }

    private boolean isGoodToAdd(String rightLine,File hostsFile) {
        return !isPresent(rightLine,hostsFile) && !isPresent(rightLine,inputList);
    }

    private boolean isURL(String possibleURL) {
        String trimmed = possibleURL.trim();
        return !trimmed.equals("");
    }

    private void addLines(File hostsFile) {
        addLines(this.inputList);
    }

    private void resolvePossibleRemainingLinesInOneOfTheFiles
            (RandomAccessFile mainHostsFile, String line) throws IOException {
        if ((line) != null){
            while ((line) != null || !inputList.isEmpty()){
                fileHandler.writeToTemp((line+"\n"));
                line = mainHostsFile.readLine();
            }
        }else
            while (!inputList.isEmpty()){
                fileHandler.writeToTemp((inputList.get(0)+"\n"));
                inputList.remove(0);
            }
    }

    private boolean bothFilesHaveLines(String line, List<String> list) {
        return (line) != null && !list.isEmpty();
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

    private boolean isPresent(String line, ArrayList<String> list){
        return list.contains(line);
    }

    private void addLine(String rightLine) {
        inputList.add(rightLine);
    }

    private String getCleaned(String line){
        String newLine = line;
        newLine = newLine.replace("https://","");
        newLine = newLine.replace("http://","");
        if (newLine.contains("/")){
            newLine = newLine.substring(0,newLine.indexOf("/"));
        }
        return newLine;
    }
    private String getHostsTypeLine(String cleanedLine) {
         return getHostsPrefix()+cleanedLine;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
