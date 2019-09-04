package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.framework.execution.FrameworkControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLookup implements LookupInstruction {

    public FileLookup() {
    }
    
    @Override
    public String getKeyword() {
        return "file";
    }

    @Override
    public String generateOutput(String parameters) {
        String output = "";
        File file = new File(parameters.trim());
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                output += FrameworkControl.getInstance().resolveConfiguration(readLine);
                output += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO harmonize for first line input
        //String output = input.trim();
        //output = SQLTools.getFirstSQLStmt(input);
        //return SQLTools.getFirstSQLStmt(parameters.trim());
        
        return output;
    }
}
