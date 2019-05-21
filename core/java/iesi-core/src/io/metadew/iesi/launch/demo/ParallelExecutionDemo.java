package io.metadew.iesi.launch.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParallelExecutionDemo {


    public static void main(String[] args) {

        executeFromInputFile();

    }

    public static void executeFromInputFile() {
        String file = "input.txt";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));

            String line = null;
            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                ParallelExecutionLauncher T1 = new ParallelExecutionLauncher("Thread-" + i, line);
                T1.start();
                i++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}