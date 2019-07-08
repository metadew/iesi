package io.metadew.iesi.util.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SQLInsertStatements {

	public static void main(String[] args) {

		String inputFile = "C:/Data/file.csv";
		boolean headers = true;
		try {
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			String readLine = "";
			int i = 0;
			StringBuilder insertStart = new StringBuilder();
			insertStart.append("INSERT INTO ");
			insertStart.append("customers");
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
			        	insertStart.append(values[j]);				        	
			        }
				}
				
				insertStart.append(");");
		        System.out.println(insertStart.toString());
				System.out.println(readLine);
				
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