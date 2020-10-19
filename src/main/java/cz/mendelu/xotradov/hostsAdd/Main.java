package cz.mendelu.xotradov.hostsAdd;

import java.io.*;
import java.util.Scanner;

public class Main {
    // ne opakovani
    //podle abecedy
     public static void main(String[] args) {
         new Main().start(args[0],args[1]);
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
                addLine(line,hostsFile);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException ignored) {

        }
    }

    private void addLine(String line, File hostsFile) {
         String cleanedLine = getCleaned(line);
         String rightLine= getHostsTypeLine(cleanedLine);
        try {
            String loc = hostsFile.getCanonicalPath();
            FileWriter fstream = new FileWriter(loc, true);
            BufferedWriter out = new BufferedWriter(fstream);
            System.out.println("Adding line: "+rightLine);
            out.write(rightLine);
            out.newLine();

            //close buffer writer
            out.close();
            fstream.close();
        } catch (IOException e) {
            System.out.println("Failed to write new lines to the hosts file.");
        }
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
