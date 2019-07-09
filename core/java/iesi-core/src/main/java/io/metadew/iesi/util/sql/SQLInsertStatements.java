package io.metadew.iesi.util.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import io.metadew.iesi.connection.tools.FileTools;

public class SQLInsertStatements {

	public static void main(String[] args) {

		String inputFile = "C:/Data/customers20190705.csv";
		boolean headers = true;
		try {
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			String readLine = "";
			int i = 0;
			StringBuilder insertStart = new StringBuilder();
			insertStart.append("INSERT INTO ");
			insertStart.append("#team#.customers");
			insertStart.append(" (");
			while ((readLine = bufferedReader.readLine()) != null) {
				if (headers) {
					if (i == 0) {
				        String[] cols = readLine.split(";");
				        for (int j = 0; j < cols.length; j++) {
				        	if (j > 0) insertStart.append(",");
				        	insertStart.append(cols[j]);				        	
				        }
				        insertStart.append(")");
				        insertStart.append(" VALUES (");

				        i++;
						continue;
					}
				}
				
				if (i > 0) {
			        String[] values = readLine.split(";");
			        for (int j = 0; j < values.length; j++) {
			        	if (j > 0) insertStart.append(",");
			        	
			        	// customers
			        	if (j == 0) insertStart.append(values[j]);
			        	if (j == 1) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 2) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 3) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 4) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 5) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 6) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 7) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 8) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 9) insertStart.append("'").append(values[j]).append("'");
			        	if (j == 10) insertStart.append("'").append(values[j]).append("'");
			        	
			        	//insertStart.append(values[j]);				        	
			        }
				}
				
				insertStart.append(");");
		        System.out.println(insertStart.toString());
				System.out.println(readLine);
				
				String filePath = "c:/Data/customers.dml";
				
				FileTools.delete(filePath);
				FileTools.appendToFile(filePath, "", insertStart.toString());
				
				// TODO mapping to data type
				i++;
				break; // TODO temp
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ok");
	}

}