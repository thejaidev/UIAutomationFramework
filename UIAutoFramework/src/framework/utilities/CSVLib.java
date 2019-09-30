/* CSVLib Library contains the most commonly used methods to perform actions on a CSV file
 *
 */
package framework.utilities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CSVLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * To read the contents of a CSV file
	 *
	 * @param completeFilePath
	 *            Path including extension where the file is located
	 * @return csvModel Returns a list of hash map which contains csv model data
	 */
	public List<LinkedHashMap<String, String>> readCSV(String completeFilePath) throws Exception {
		int i = 0;
		String[] keys = null;
		LinkedHashMap<String, String> celVals = null;
		List<LinkedHashMap<String, String>> csvModel = new ArrayList<LinkedHashMap<String, String>>();
		Reader csvReader = null;
		String keyval = null, val = null;
		CommonUtilLib utilLib = new CommonUtilLib();
		try {
			csvReader = new FileReader(completeFilePath);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(csvReader);
			System.out.println("****************************************************");
			for (CSVRecord record : records) {
				celVals = new LinkedHashMap<String, String>();
				if (i == 0) {
					keys = utilLib.trimStringsInArray(getColumnNames(record));
					for (int j = 0; j < keys.length; j++) {
						keyval = keys[j].trim();
						celVals.put(keyval, keyval);
					}
					csvModel.add(celVals);
				}
				else {
					for (int k = 0; k < keys.length; k++) {
						try {
							val = record.get(k).trim();
						}
						catch (ArrayIndexOutOfBoundsException e) {
							val = "";
						}
						celVals.put(keys[k], val);
						System.out.print(val + "\t");
					}
					csvModel.add(celVals);
					System.out.println("");
				}
				i++;
			}
			System.out.println("****************************************************");
		}
		catch (Exception e) {
			logger.error("Ünable to read the CSV file", e);
		}
		return csvModel;
	}

	/**
	 * To get the column header of a CSV file
	 *
	 * @param record
	 *            First record of CSV
	 * @return colHeaders String array of column header
	 */
	private String[] getColumnNames(CSVRecord record) {
		String[] colHeaders = null;
		try {
			colHeaders = new String[record.size()];
			for (int i = 0; i < record.size(); i++) {
				colHeaders[i] = record.get(i).trim();
				System.out.print(colHeaders[i] + "\t");
			}
			System.out.println("");
		}
		catch (Exception e) {
			logger.error("Unable to get the column headers");
		}
		return colHeaders;
	}

}
