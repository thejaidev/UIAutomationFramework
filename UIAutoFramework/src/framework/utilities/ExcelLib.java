/* ExcelLib Library contains the most commonly used methods to perform actions on Excel
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import framework.constants.ITestdataEnums;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class contains all the methods / actions that can be performed on an
 * Excel workbook
 */
public class ExcelLib implements ITestdataEnums {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private FileInputStream fis = null;
	private FileOutputStream fos = null;
	private File file = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;
	private int col_Num = -1, startRow = -1, endRow = -1;

	private ConfigurationLib configLib;
	private CommonUtilLib utilLib;

	public ExcelLib() {
		configLib = new ConfigurationLib();
		utilLib = new CommonUtilLib();
	}

	public void createExcel(String xlFilePath, String sheetName) {
		try {
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet(sheetName);
			File file = new File(xlFilePath);
			OutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();
			workbook.close();
			connectToExcel(xlFilePath);
			logger.info("Created file in " + xlFilePath);
		} catch (Exception e) {
			logger.error("Unable to create the excel file in " + xlFilePath);
		}
	}

	/**
	 * To connect to an excel sheet of format .xlsx based on the path provided in
	 * the config.properties
	 *
	 * @param xlFilePath path of the excel file to establish the connection
	 */
	public boolean connectToExcel(String xlFilePath) {
		String extension = null;
		boolean isConnected = false;
		int intPos = -1;
		try {
			for (int i = xlFilePath.length() - 1; i > 0; i--) {
				if (xlFilePath.charAt(i) == '.') {
					intPos = i;
					break;
				}
			}
			extension = xlFilePath.substring(intPos);
			file = new File(xlFilePath);
			fis = new FileInputStream(file);
			if (!(extension.equalsIgnoreCase(".xlsx") || extension.equalsIgnoreCase(".xls")))
				logger.error("Inavlid file format: " + extension + ". It should be either xlsx or xls");
			else
				workbook = new XSSFWorkbook(fis);
			fis.close();
			logger.info("Connected to excel: " + xlFilePath);
			isConnected = true;
		} catch (FileNotFoundException | NullPointerException e) {
			logger.error("Correct the file path / name: " + xlFilePath, e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Unable to read the contents of the file: " + xlFilePath, e);
			e.printStackTrace();
		}
		return isConnected;
	}

	/**
	 * To get the row number to read or write the data from excel
	 *
	 * @note This is a helper method which will be called by getEntireCellValue()
	 *       and setCellData(). Do not call this method directly
	 */
	private int getExcelRowNum(String script, int itr) {
		int rowNo = -1;
		boolean found = false;
		String scriptCell = null, iterationCell = null;
		try {
			@SuppressWarnings("rawtypes")
			Iterator iterator = sheet.rowIterator();
			while (iterator.hasNext()) {
				Row row = (Row) iterator.next();
				scriptCell = row.getCell(1).toString().trim();
				iterationCell = row.getCell(2).toString().trim();
				if (row.getCell(2).getCellTypeEnum() == CellType.NUMERIC)
					iterationCell = iterationCell.substring(0, iterationCell.indexOf(".")).trim();
				if ((scriptCell.equals(script) && iterationCell.equals(String.valueOf(itr).trim()))
						|| (scriptCell.equals(script) && Integer.parseInt(iterationCell) == itr)) {
					startRow = row.getRowNum();
					rowNo = row.getRowNum();
					found = true;
					break;
				}
			}
			if (startRow == -1 || found == false)
				throw new Exception(
						"Please check the test name: " + script + " or the iteration: " + itr + " in the excel sheet");

			row = sheet.getRow(0);
		} catch (Exception e) {
			logger.error("Unable to get the row number for script: " + script + " | " + itr, e);
			startRow = -1;
			rowNo = -1;
		}
		return rowNo;
	}

	/**
	 * To get the column number to read or write the data from excel
	 *
	 * @param colName Name of the column should be passed as a parameter
	 * @return
	 * @note This is a helper method which will be called by getEntireCellValue()
	 *       and setCellData(). Do not call this method directly
	 */
	private int getExcelColNum(String colName) throws Exception {
		Row firstRow = sheet.getRow(0);
		int colNo = -1;
		boolean found = false;
		for (int i = 0; i < firstRow.getLastCellNum(); i++) {
			if (firstRow.getCell(i).getStringCellValue().trim().equals(colName.trim())) {
				col_Num = i;
				colNo = i;
				found = true;
				break;
			}
		}
		if (col_Num == -1 || found == false) {
			logger.error("Column " + colName + " doesn't exist in the test data sheet");
			throw new Exception("Please check the column name: " + colName + " in the test data sheet");
		}
		return colNo;
	}

	/**
	 * To get the excel cell to read or write the data
	 *
	 * @note This is a helper method which will be called by getEntireCellValue()
	 *       and setCellData(). Do not call this method directly
	 */

	private XSSFCell getCell(int cellRow, int cellCol) {
		try {
			row = sheet.getRow(cellRow);
			cell = row.getCell(cellCol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cell;
	}

	/**
	 * To determine whether the cell is merged or not. If rowsMerged > 1 then the
	 * script contains merged rows
	 *
	 * @return returns the number of rows merged
	 */
	@SuppressWarnings("finally")
	private int getNumOfMergedRows(String colName, int startingRow, boolean fromScriptName) {
		int rowsMerged = 0, col = 0, endRw = 0;
		XSSFRow mergedRow = null;
		XSSFCell mergedCell = null;
		CellType cellType = CellType.BLANK;
		try {

			if (fromScriptName) {
				col = 1;
				endRw = sheet.getPhysicalNumberOfRows();
			} else {
				col = getExcelColNum(colName);
				endRw = endRow;
			}

			for (int i = startingRow + 1; i < endRw; i++) {
				mergedRow = sheet.getRow(i);
				mergedCell = mergedRow.getCell(col);
				try {
					cellType = mergedCell.getCellTypeEnum();
				} catch (NullPointerException e) {
					logger.debug("Cells are not merged");
					cellType = CellType.BLANK;
				}
				if (cellType == CellType.BLANK)
					rowsMerged++;
				else
					break;
			}
		} catch (Exception e) {
			logger.info("No merged rows found");
		} finally {
			rowsMerged++;
			return rowsMerged;
		}

	}

	/**
	 * To get values from multiple rows in case of merged cells
	 *
	 * @param sheetName  Name of the sheet
	 * @param colName    Name of the column
	 * @param rowsMerged This is to determine the upper limit while fetching data
	 *                   from multiple rows
	 * @return cellValues ArrayList of values
	 */
	private ArrayList<String> getValuesFromMultipleRows(String sheetName, String colName, int startingRow,
			int rowsMerged) {
		ArrayList<String> cellValues = new ArrayList<String>();
		String val;
		int col = 0;
		logger.info("Retrieving multiple values from column: " + colName);
		try {
			col = getExcelColNum(colName);
			for (int i = startingRow; i < startingRow + rowsMerged; i++) {
				cell = getCell(i, col);
				try {
					if (cell.getCellTypeEnum() != CellType.BLANK) {
						val = getEntireCellValue(sheetName, colName, i, col);
						logger.info("Fetching " + val);
						cellValues.add(val);
					}
				} catch (NullPointerException ne) {
					logger.info("Cell is empty");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellValues;
	}

	/**
	 * To get the entire cell data from excel sheet based on the sheet (tab) name
	 * and column name
	 *
	 * @param sheetName Name of the sheet to fetch the data from
	 * @param colName   Name of the column to fetch the data from
	 * @return cellValue Returns the entire cell value
	 * @note This method calls getCell() to fetch data from Excel. This is a helper
	 *       method which will be called by getCellData. Do not call this method
	 *       directly
	 */

	private String getEntireCellValue(String sheetName, String colName, int row, int col) {
		String val = null;
		try {
			getCell(row, col);
			if (cell.getCellTypeEnum() == CellType.STRING)
				val = cell.getStringCellValue().trim();
			else if (cell.getCellTypeEnum() == CellType.FORMULA)
				val = cell.getRichStringCellValue().toString().trim();
			else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
				String cellValue = String.valueOf(cell.getNumericCellValue()).replaceFirst("\\.0+$", "");
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					Date date = cell.getDateCellValue();
					cellValue = df.format(date);
				}
				val = cellValue.trim();
			} else if (cell.getCellTypeEnum() == CellType.BLANK)
				val = null;
			else
				val = String.valueOf(cell.getBooleanCellValue()).trim();
			val = validateTestDataValueToFetchExist(val);
		} catch (NullPointerException e) {
			logger.info(
					"Cell is empty. If this is not expected then check the sheet name or the formatting of the sheet: "
							+ sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * This method fetches single value from a cell, multiple ";" separated values
	 * from a single cell, or multiple values in multiple cell (one below the other
	 * i.e. in different rows)
	 *
	 * @param sheetName     Name of the sheet to fetch data from
	 * @param scriptName    Name of the script
	 * @param iteration     Iteration of the script
	 * @param colName       Name of the column to fetch data from
	 * @param fetchMultiple Pass false to fetch the entire contents of the cell.
	 *                      Pass true to fetch semicolon ";" separated value
	 * @return cellSepData Returns value of type Object. It should be either type
	 *         casted to String or String[]. Type cast the returned value as String
	 *         if fetchMultiple is false. Type cast the returned value as String []
	 *         if fetch multiple is set to true
	 * @note This method calls getExcelRowNum(), getNumOfMergedRows(),
	 *       getExcelColNum, getEntireCellValue(sheetName, colName) to fetch data
	 *       from Excel
	 */

	public Object getCellData(String sheetName, String scriptName, int iteration, String colName,
			boolean fetchMultiple) {
		String cellValue = null;
		Object[] cellSepData = null;
		int mergedRows = 0, rowNo = -1, colNo = -1;
		try {
			sheet = workbook.getSheet(sheetName);
			rowNo = getExcelRowNum(scriptName, iteration);
			if (rowNo == -1)
				throw new Exception("Record doesn't exist in the excel sheet");
			mergedRows = getNumOfMergedRows(Testdata.SCRIPTNAME.toString(), startRow, true);
			endRow = rowNo + mergedRows;
			colNo = getExcelColNum(colName);
			if (mergedRows <= 1) {
				cellValue = getEntireCellValue(sheetName, colName, startRow, colNo);
				if (fetchMultiple) {
					if (cellValue.contains(";")) {
						cellSepData = cellValue.split(";");
						for (int i = 0; i < cellSepData.length; i++) {
							cellSepData[i] = ((String) cellSepData[i]).trim();
							logger.info("Cell contains " + cellSepData[i]);
						}
					} else {
						cellSepData = new String[1];
						cellSepData[0] = cellValue;
					}
					return cellSepData;
				} else {
					logger.info("Retrieved " + cellValue + " from excel - Script: " + scriptName + " | Iteration: "
							+ iteration + " | Column: " + colName);
					return cellValue;
				}
			} else {
				ArrayList<String> cellValues = getValuesFromMultipleRows(sheetName, colName, startRow, mergedRows);
				cellSepData = cellValues.toArray(new String[cellValues.size()]);
				if (fetchMultiple)
					return cellSepData;
				else
					return cellSepData[0];

			}
		} catch (Exception e) {
			if (rowNo == -1)
				logger.error("Record doesn't exist in the sheet: " + sheetName + " in column: " + colName);
			return null;
		}
	}

	/**
	 * This method can be used to get all the data in multiple cells with respect to
	 * another column e.g. if you want to fetch all the packages with respect to a
	 * reference set i.e. a reference set R1 is mapped to package A, B, and C R2 is
	 * mapped to package C, D, and F. Then to fetch only the packages associated
	 * with R1 we can use this method
	 *
	 * @param sheetName           Name of the sheet to fetch data from
	 * @param relativeColumn      The relative column name (primary column) which is
	 *                            used with respect to the actual column to fetch
	 *                            the values e.g. reference set
	 * @param relativeColumnValue The relative column value which is used with
	 *                            respect to the actual column to fetch the values
	 *                            e.g. R1
	 * @param colName             Actual name of the column to fetch data from e.g.
	 *                            package name
	 * @return cellSepData Returns value of type Object. It should be either type
	 *         casted to String or String[]
	 * @note This method is called by getCellData(String, String, String). Do not
	 *       call this method individually
	 */

	private Object getCellData(String sheetName, String scriptName, int iteration, String relativeColumn,
			String relativeColumnValue, String colName, boolean allKeysAreSame) {
		ArrayList<String> cellValues = null;
		ArrayList<String> completeCellValues = new ArrayList<String>();
		int relativeColMergedRows = 0, actColMergedRows = 0, relativeColNum = 0, rowNo = 0;
		String val;
		Object[] cellSepData = null;
		try {
			sheet = workbook.getSheet(sheetName);
			rowNo = getExcelRowNum(scriptName, iteration);
			relativeColMergedRows = getNumOfMergedRows(Testdata.SCRIPTNAME.toString(), startRow, true);
			endRow = rowNo + relativeColMergedRows;
			getExcelColNum(colName);
			relativeColNum = getExcelColNum(relativeColumn);
			for (int i = startRow; i < startRow + relativeColMergedRows; i++) {
				cell = getCell(i, relativeColNum);
				try {
					if (cell.getCellTypeEnum() != CellType.BLANK) {
						val = getEntireCellValue(sheetName, relativeColumn, i, relativeColNum);
						if (val.equalsIgnoreCase(relativeColumnValue)) {
							actColMergedRows = getNumOfMergedRows(relativeColumn, i, false);
							cellValues = getValuesFromMultipleRows(sheetName, colName, i, actColMergedRows);
							if (allKeysAreSame) {
								completeCellValues.addAll(cellValues);
								cellSepData = completeCellValues.toArray(new String[completeCellValues.size()]);
							} else {
								cellSepData = cellValues.toArray(new String[cellValues.size()]);
								break;
							}
						}
					}
				} catch (NullPointerException ne) {
					logger.debug("Cell is empty");
				}
			}
			return cellSepData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method can be used to get all the data in multiple cells with respect to
	 * another column e.g. a reference set R1 is mapped to package A, B, and C; R2
	 * is mapped to package C, D, and F. Then to fetch ALL the packages associated
	 * with ALL reference sets we can use this method
	 *
	 * @param sheetName      Name of the sheet to fetch data from
	 * @param scriptName     Name of the script
	 * @param iteration      Iteration of the script
	 * @param relativeColumn The relative column name (primary column) which is used
	 *                       with respect to the actual column to fetch the values
	 *                       e.g. reference set
	 * @param colName        Name of the column to fetch data from e.g. packages
	 * @return testDataMap Returns a multi map key value pair where key stores the
	 *         relative column value and val store the actual column value
	 * @note This method calls getCellData to fetch data from Excel
	 */

	public Multimap<String, String> getCellData(String sheetName, String scriptName, int iteration,
			String relativeColumn, String colName) {
		String[] relativeEle, val;
		Multimap<String, String> testDataMap = ArrayListMultimap.create();
		try {
			relativeEle = (String[]) (getCellData(sheetName, scriptName, iteration, relativeColumn, true));
			if (utilLib.verifyIfAllElementsAreSameInArray(relativeEle)) {
				val = (String[]) (getCellData(sheetName, scriptName, iteration, relativeColumn, relativeEle[0], colName,
						true));
				for (int i = 0; i < val.length; i++)
					testDataMap.put(relativeEle[0], val[i]);
			} else {
				for (int i = 0; i < relativeEle.length; i++) {
					val = (String[]) (getCellData(sheetName, scriptName, iteration, relativeColumn, relativeEle[i],
							colName, false));
					if (val == null)
						logger.info("No data exists in " + colName + " with relative column " + relativeColumn);
					else {
						for (String v : val)
							testDataMap.put(relativeEle[i], v);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testDataMap;
	}

	/**
	 * To write to a cell
	 *
	 * @param cellRow   Row to write data to
	 * @param cellCol   Column to write data to
	 * @param textToSet Text to be written to above row and column
	 * @note This is a helper method which will be called by setCellData(), and
	 *       findAndReplace Do not call this method directly
	 */

	public void writeTo(int cellRow, int cellCol, String textToSet) {
		try {
			row = sheet.getRow(cellRow);
			if (row == null)
				row = sheet.createRow(cellRow);
			cell = row.createCell(cellCol);

			cell.setCellValue(textToSet);
			fis.close();
			fos = new FileOutputStream(file);
			workbook.write(fos);
			fos.close();
			logger.info("Wrote " + textToSet + " in row: " + cellRow + ", column " + cellCol + " in excel");
		} catch (FileNotFoundException e) {
			logger.error("Please close the test data sheet. Unable to write: " + textToSet + " to excel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To write data to excel sheet based on the sheet (tab) name - sheetName,
	 * column name - colName and text to save - textToSet
	 *
	 * @param sheetName  Name of the sheet to write data to
	 * @param scriptName Name of the script
	 * @param iteration  Iteration of the script
	 * @param colName    Name of the column to write data to
	 * @param textToSet  The actual value written to excel and to be saved
	 * @param append     Pass True to append with existing data with a ";" semicolon
	 *                   Pass False to write only the new text
	 * @note Excel sheet should be closed in order to write data to excel This
	 *       method calls getRowNum(), getColNum(colName), and getCell() to fetch
	 *       data from Excel
	 */

	public void setCellData(String sheetName, String scriptName, int iteration, String colName, String textToSet,
			boolean append) {
		String existingText = null;
		try {
			sheet = workbook.getSheet(sheetName);
			getExcelRowNum(scriptName, iteration);
			getExcelColNum(colName);
			logger.debug("Setting data in excel. Row - " + (startRow + 1) + " and column - " + (col_Num + 1));
			if (append) {
				existingText = (String) getCellData(sheetName, scriptName, iteration, colName, false);
				textToSet = existingText + "; " + textToSet;
			}
			writeTo(startRow, col_Num, textToSet);
		} catch (NullPointerException e) {
			logger.error("Please check the sheet name or the formatting of the sheet: " + sheetName, e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Unable to write to excel", e);
			e.printStackTrace();
		}
	}

	/**
	 * To write multiple rows to excel sheet based on the sheet (tab) name -
	 * sheetName, column name - colName and text to save - textToSet
	 *
	 * @param sheetName Name of the sheet
	 * @param colName   Name of the column
	 * @param textToSet String array to be written to excel in rows, one below the
	 *                  other
	 */
	public void setCellData(String sheetName, String scriptName, int iteration, String colName, String[] textToSet) {
		int startRow = -1, endRow = -1, rowsToBeCreated = -1, mergedRows = -1;
		try {
			sheet = workbook.getSheet(sheetName);
			startRow = getExcelRowNum(scriptName, iteration);
			getExcelColNum(colName);
			endRow = sheet.getPhysicalNumberOfRows();
			rowsToBeCreated = textToSet.length;

			mergedRows = getNumOfMergedRows(colName, startRow, true);

			if (startRow != endRow - 1 && rowsToBeCreated > mergedRows)
				sheet.shiftRows(startRow + 1, endRow + 1, rowsToBeCreated - mergedRows);

			for (int i = 0; i < rowsToBeCreated; i++) {
				writeTo(startRow + i, col_Num, textToSet[i]);
			}
		} catch (NullPointerException e) {
			logger.error("Please check the sheet name or the formatting of the sheet: " + sheetName, e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Unable to write to excel", e);
			e.printStackTrace();
		}
	}

	/**
	 * To find and replace values in the column - colName when a script's test data
	 * spans across multiple rows
	 *
	 * @param sheetName            Name of the sheet to write data to
	 * @param colName              Name of the column to write data to
	 * @param textToFind           Text which should be replaced with
	 * @param textToSet            The actual value written to excel and to be saved
	 * @param replaceAllOccurences Pass True to replace all occurrences of
	 *                             textToFind Pass false to replace only the first
	 *                             occurrence of textToFind
	 * @param append               Pass True to append with existing data with a ";"
	 *                             semicolon Pass False to replace the entire
	 *                             existing text with new text
	 * @pre-requisite
	 * @note This method calls getExcelRowNum(), getNumOfMergedRows(),
	 *       getExcelColNum, getEntireCellValue(sheetName, colName), writeTo(int,
	 *       int, String) to write data to Excel
	 */

	public void findAndReplace(String sheetName, String scriptName, int iteration, String colName, String textToFind,
			String textToSet, boolean replaceAllOccurences, boolean append) {
		int mergedRows = 0, col = -1, startingRow = -1, rowNo = -1;
		try {
			sheet = workbook.getSheet(sheetName);
			rowNo = getExcelRowNum(scriptName, iteration);
			mergedRows = getNumOfMergedRows(Testdata.SCRIPTNAME.toString(), startRow, true);
			endRow = rowNo + mergedRows;
			col = getExcelColNum(colName);
			startingRow = startRow;
			for (int i = startingRow; i < startingRow + mergedRows; i++) {
				cell = getCell(i, col);
				if (cell.getCellTypeEnum() != CellType.BLANK) {
					if (getEntireCellValue(sheetName, colName, i, col).equalsIgnoreCase(textToFind)) {
						if (append) {
							textToSet = textToFind + "; " + textToSet;
						}
						logger.info("Found " + textToFind + " in excel. Replacing it with " + textToSet + " at row - "
								+ (i + 1) + " and column - " + (col + 1));
						writeTo(i, col, textToSet);
						if (!replaceAllOccurences)
							break;
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("Please check the sheet name or the formatting of the sheet: " + sheetName, e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Unable to write to excel", e);
			e.printStackTrace();
		}
	}

	/**
	 * To find and replace values in the column - colName relative to relativeColumn
	 * and relativeColumnValue when a script's test data spans across multiple rows
	 *
	 * @param sheetName            Name of the sheet to fetch data from
	 * @param scriptName           Name of the script
	 * @param iteration            Iteration of the script
	 * @param relativeColumn       The relative column name (primary column) which
	 *                             is used with respect to the actual column to
	 *                             fetch the values e.g. reference set
	 * @param relativeColumnValue  The relative column value which is used with
	 *                             respect to the actual column to fetch the values
	 *                             e.g. R1
	 * @param colName              Actual name of the column to fetch data from e.g.
	 *                             package name
	 * @param textToFind           Text which should be replaced with
	 * @param textToSet            The actual value written to excel and to be saved
	 * @param replaceAllOccurences Pass True to replace all occurrences of
	 *                             textToFind Pass false to replace only the first
	 *                             occurrence of textToFind
	 * @param append               Pass True to append with existing data with a ";"
	 *                             semicolon Pass False to replace the entire
	 *                             existing text with new text
	 * @note This method calls getExcelRowNum(), getNumOfMergedRows(),
	 *       getExcelColNum, getEntireCellValue(sheetName, colName), writeTo(int,
	 *       int, String) to write data to Excel
	 */

	public void findAndReplace(String sheetName, String scriptName, int iteration, String relativeColumn,
			String relativeColumnValue, String colName, String textToFind, String textToSet,
			boolean replaceAllOccurences, boolean append) {
		int relativeColMergedRows = 0, actColMergedRows = 0, relativeColNum = 0, col = -1, startingRow = -1, rowNo = -1;
		String val;
		try {
			sheet = workbook.getSheet(sheetName);
			rowNo = getExcelRowNum(scriptName, iteration);
			relativeColMergedRows = getNumOfMergedRows(Testdata.SCRIPTNAME.toString(), startRow, true);
			endRow = rowNo + relativeColMergedRows;
			getExcelColNum(colName);
			relativeColNum = getExcelColNum(relativeColumn);
			for (int i = startRow; i < startRow + relativeColMergedRows; i++) {
				cell = getCell(i, relativeColNum);
				if (cell.getCellTypeEnum() != CellType.BLANK) {
					val = getEntireCellValue(sheetName, relativeColumnValue, i, relativeColNum);
					if (val.equalsIgnoreCase(relativeColumnValue)) {
						actColMergedRows = getNumOfMergedRows(relativeColumn, i, false);
						col = getExcelColNum(colName);
						startingRow = i;
						for (int j = startingRow; j < startingRow + actColMergedRows; j++) {
							cell = getCell(j, col);
							if (cell.getCellTypeEnum() != CellType.BLANK) {
								if (getEntireCellValue(sheetName, colName, j, col).equalsIgnoreCase(textToFind)) {
									if (append) {
										textToSet = textToFind + "; " + textToSet;
									}
									logger.info("Found " + textToFind + " in excel. Replacing it with " + textToSet
											+ " at row - " + (j + 1) + " and column - " + (col + 1));
									writeTo(j, col, textToSet);
									if (!replaceAllOccurences)
										break;
								}
							}
						}
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("Please check the sheet name or the formatting of the sheet: " + sheetName, e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Unable to write to excel", e);
			e.printStackTrace();
		}
	}

	/**
	 * To get number of occurrences of a script to determine the invocation count
	 *
	 * @param script Name of the script
	 * @return occurrences Number of occurrences
	 */
	public int getNumberOfOccurences(String script) {
		String scriptCell = null;
		int occurences = 0;
		try {
			sheet = workbook.getSheet(Sheetname.TEST_MAP.toString());
			@SuppressWarnings("rawtypes")
			Iterator iterator = sheet.rowIterator();
			while (iterator.hasNext()) {
				Row row = (Row) iterator.next();
				scriptCell = row.getCell(1).toString().trim();
				if (scriptCell.equals(script)) {
					occurences++;
				}
			}
		} catch (Exception e) {
			System.out.println("Unable to get number of occurences of script name " + script);
		}
		return occurences;
	}

	/**
	 * To read all the contents of an excel sheet
	 *
	 */

	public void readExcelSheet(String sheetName) {
		try {
			sheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = sheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					switch (cell.getCellTypeEnum()) {
					case STRING:
						System.out.print((cell.getRowIndex() + 1) + " : " + cell.getStringCellValue());
						break;
					case BOOLEAN:
						System.out.print((cell.getRowIndex() + 1) + " : " + cell.getBooleanCellValue());
						break;
					case NUMERIC:
						System.out.print((cell.getRowIndex() + 1) + " : " + cell.getNumericCellValue());
						break;
					default:
						System.out.println("<Empty Cell>");
					}
					System.out.print(" - ");
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error("Unable to read the contents of the excel sheet");
			e.printStackTrace();
		}
	}

	/**
	 * To create an excel report using a linked map of list data model
	 *
	 * @param sheetName             Name of the sheet where the report should be
	 *                              written
	 * @param completeReportDetails Linked Map of list which contains all the report
	 *                              details
	 * @param columnName            Number of columns required in the report
	 */
	public void createReport(String sheetName, List<LinkedHashMap<String, String>> completeReportDetails,
			String[] columnName) {
		try {
			String textToWrite = null;
			int col = -1;
			sheet = workbook.getSheet(sheetName);
			for (int i = 0; i < completeReportDetails.size(); i++) {
				writeTo(i + 1, 0, String.valueOf(i + 1));
				for (int j = 1; j < columnName.length; j++) {
					try {
						col = getExcelColNum(columnName[j]);
						textToWrite = completeReportDetails.get(i).get(columnName[j]);
						writeTo(i + 1, col, textToWrite);
					} catch (IndexOutOfBoundsException e) {
						logger.error("Unable to write " + textToWrite + " to row " + i + " and column " + col);
					}
				}
			}
			logger.info("Generated excel report");
		} catch (Exception e) {
			logger.error("Unable to generate the excel report", e);
		}
	}

	/**
	 * To generate column headers for the excel report
	 *
	 * @param columnNames Name of the columns to be generated
	 */
	public void genereateCoumnHeaders(String sheetName, String[] columnNames) {
		try {
			sheet = workbook.getSheet(sheetName);
			for (int i = 0; i < columnNames.length; i++)
				writeTo(0, i, columnNames[i]);
			setStyleAsBold(0);
		} catch (Exception e) {
			logger.error("Unable to write column headers for the excel report", e);
		}
	}

	/**
	 * To set a a row as bold
	 *
	 * @param rw Row number to be made as bold
	 * @note This is used to genereateCoumnHeaders and row number starts from 0
	 */
	private void setStyleAsBold(int rw) {
		try {
			Row row = sheet.getRow(rw);

			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);

			for (int i = 0; i < row.getLastCellNum(); i++)
				row.getCell(i).setCellStyle(style);
		} catch (Exception e) {
			logger.error("Unable to set row style as bold for row " + rw);
		}
	}

	/**
	 * Method validateTestDataValueToFetchExist is Overloaded Function to handle
	 * Single String
	 *
	 * @param cellSepData
	 * @return
	 */
	private String validateTestDataValueToFetchExist(String cellSepData) {
		try {
			if (cellSepData != null) {
				List<TestDataDefaultValues> enumTestDataValues = Arrays.asList(TestDataDefaultValues.values());
				for (TestDataDefaultValues defaultValue : enumTestDataValues) {
					if (cellSepData.contains(defaultValue.toString())) {
						cellSepData = replaceDefaultValuesFromTestData(cellSepData);
						logger.info("Returing Values for validateTestDataValueToFetchExist method : " + cellSepData);
						break;
					}

				}
			}
		} catch (Exception e) {
			logger.error("Error occurred while execution validateTestDataValueToFetchExist method", e);
		}
		return cellSepData;
	}

	/**
	 * @param inputValue
	 * @return Replaces Default Values of Test Data to a required Value
	 */
	private String replaceDefaultValuesFromTestData(String inputValue) {
		String returnValue = null;
		try {
			if (Pattern
					.compile(Pattern.quote(TestDataDefaultValues.LOGIN_USERNAME.toString()), Pattern.CASE_INSENSITIVE)
					.matcher(inputValue).find())
				returnValue = configLib.getLoginUsername();
			else if (Pattern
					.compile(Pattern.quote(TestDataDefaultValues.LOGIN_PASSWORD.toString()), Pattern.CASE_INSENSITIVE)
					.matcher(inputValue).find())
				returnValue = configLib.getLoginPassword();
			else if (Pattern.compile(Pattern.quote(TestDataDefaultValues.BROWSER.toString()), Pattern.CASE_INSENSITIVE)
					.matcher(inputValue).find())
				returnValue = configLib.getBrowser();
			else
				logger.error("Incorrect Value " + inputValue + " .We do not Support provided Test Data Value");
		} catch (Exception e) {
			logger.error("Execution of method replaceDefaultValuesFromTestData failed", e);
		}

		logger.info("Old Value : " + inputValue + " - New Value is : " + returnValue);
		return returnValue;
	}

	/**
	 * To search a text in a column of a sheet
	 *
	 * @param sheetName  Sheet name to fetch data from
	 * @param columnName Column name from which the data should be fetched
	 * @param textToFind Text to be searched
	 * @return count Total number of scripts to be executed
	 * @note This is used to get total number Of scripts to be executed
	 */
	public int searchText(String sheetName, String columnName, String textToFind) {
		int count = 0;
		int col = -1;
		try {
			sheet = workbook.getSheet(sheetName);
			col = getExcelColNum(columnName);
			for (int row = 1; row < sheet.getPhysicalNumberOfRows(); row++) {
				String text = getEntireCellValue(sheetName, columnName, row, col);
				if (text == null)
					logger.debug("Execute flag is not specified");
				else if (text.equalsIgnoreCase(textToFind))
					count++;
			}
			logger.info("Total number of scripts to be executed are " + count);
		} catch (Exception e) {
			logger.error("Unable to get total number of scripts to be executed", e);
		}
		return count;
	}
}
