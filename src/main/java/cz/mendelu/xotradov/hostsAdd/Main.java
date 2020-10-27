package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    ArrayList<String> inputList = new ArrayList<>();
    //podle abecedy
    //nacist nove radky usporadane
    //chanelem doplnit
     public static void main(String[] args) {
         new Main().start(args[0],args[1]);
         System.out.println("Input file: "+ args[0]);
         System.out.println("main hosts file: "+ args[1]);
    }

    public void start(String inputFileName, String hostsFileName) {
        File inputFile = new File(inputFileName);
        File hostsFile = new File(hostsFileName);
        start(inputFile,hostsFile);
    }

    public void start(File inputFile, File hostsFile){
        if (inputFile.exists()) {
            if (inputFile.canRead()){
                if (hostsFile.exists()) {
                    if (hostsFile.canWrite()){
                        handle(inputFile,hostsFile);
                    }else {
                        System.out.println("The second file cannot be written to.");
                    }
                } else {
                    System.out.println("The second file does not exist. Second argument is file to be read from.");
                }
            }else {
                System.out.println("The first file cannot be read.");
            }
        } else {
            System.out.println("The first file does not exist. First argument is file to be read from.");
        }
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
            // note that Scanner suppresses exceptions
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
        RandomAccessFile r = null;
        RandomAccessFile rtemp = null;
        try {
            //ToDo just read
             r = new RandomAccessFile(hostsFile, "rw");
             File tempFile = new File(hostsFile.getName() + "~");
             tempFile.delete();
             rtemp = new RandomAccessFile(tempFile, "rw");
            r.seek(0);
            String line = r.readLine();
            while ((line) != null && !inputList.isEmpty()){
                //input is lower
                if (isFirstBeforeSecond(inputList.get(0),line)){
                    rtemp.write((inputList.get(0)+"\n").getBytes());
                    inputList.remove(0);
                }else {
                    //line is lower
                    rtemp.write((line+"\n").getBytes());
                    line = r.readLine();
                }
            }
            //one of the files might have unresolved hosts
            if ((line) != null){
                while ((line) != null || !inputList.isEmpty()){
                    rtemp.write((line+"\n").getBytes());
                    line = r.readLine();
                }
            }else if (!inputList.isEmpty()){
                while ((line) != null || !inputList.isEmpty()){
                    rtemp.write((inputList.get(0)+"\n").getBytes());
                    inputList.remove(0);
                }
            }
            System.out.println("Just");
            r.close();
            rtemp.close();
            //copy to
            System.out.println("Just2");
            copyFileUsingChannel(tempFile,hostsFile);
        } catch (Exception e) {
            try {
                r.close();
                rtemp.close();
            } catch (Exception e1){
                e.printStackTrace();
            }
        }

    }

    public boolean isFirstBeforeSecond(String first, String second) {
        return first.compareTo(second)<0;
    }

    private void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
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
         return "0.0.0.0 "+cleanedLine;
    }
}
