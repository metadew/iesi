package io.metadew.iesi.util.sql;

import io.metadew.iesi.connection.tools.FileTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SQLInsertStatements {

	public static void main(String[] args) {

		String type = "orders"; // customers restaurants orders
		String inputFile = "C:/Data/" + type + "20190705.csv";
		boolean headers = true;
		StringBuilder insertStatement = new StringBuilder();
		try {
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			String readLine = "";
			int i = 0;
			StringBuilder insertStart = new StringBuilder();
			insertStart.append("INSERT INTO ");
			insertStart.append("#team#." + type);
			insertStart.append(" (");
			while ((readLine = bufferedReader.readLine()) != null) {
				if (headers) {
					if (i == 0) {
						String[] cols = readLine.split(";");
						for (int j = 0; j < cols.length; j++) {
							if (j > 0)
								insertStart.append(",");
							insertStart.append(cols[j]);
						}
						insertStart.append(")");

						i++;
						continue;
					}
				}

				StringBuilder insertValues = new StringBuilder();
				insertValues.append(" VALUES (");
				if (i > 0) {
					String[] values = readLine.split(";");
					for (int j = 0; j < values.length; j++) {
						if (j > 0)
							insertValues.append(",");

						if (type.equalsIgnoreCase("orders")) {
							if (j == 0)
								insertValues.append(values[j]);
							if (j == 1)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 2)
								insertValues.append(values[j]);
							if (j == 3)
								insertValues.append(values[j]);
							if (j == 4)
								insertValues.append(values[j]);
						} else {
							// customers and restaurants
							if (j == 0)
								insertValues.append(values[j]);
							if (j == 1)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 2)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 3)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 4)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 5)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 6)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 7)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 8)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 9)
								insertValues.append("'").append(values[j]).append("'");
							if (j == 10)
								insertValues.append("'").append(values[j]).append("'");
						}
						// insertStart.append(values[j]);
					}
				}

				insertValues.append(");");

				insertStatement.append(insertStart);
				insertStatement.append(insertValues);
				insertStatement.append("\n");
				// TODO mapping to data type
				i++;
				// break; // TODO temp
			}

			String filePath = "c:/Data/" + type + "20190705.dml";

			FileTools.delete(filePath);
			FileTools.appendToFile(filePath, "", insertStatement.toString());
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ok");
	}

}