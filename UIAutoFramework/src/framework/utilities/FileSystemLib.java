/* FileSystemLib Library contains the most commonly used methods to perform actions in OS
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class contains all the methods / actions that can be performed on
 * windows file system
 */
public class FileSystemLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * To create a text file
	 *
	 * @param completeFilePath
	 *            Path of the file + file name to be created
	 */

	public File createTextFileIfNotExist(String completeFilePath) {
		logger.info("Creating the text file " + completeFilePath + " if it doesn't exist");
		File file = null;
		try {
			file = new File(completeFilePath);
			file.createNewFile();
		}
		catch (Exception e) {
			System.out.println("Unable to create the text file: " + completeFilePath);
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * To write in a text file
	 *
	 * @param file
	 *            File to be write to
	 * @param text
	 *            Text to write
	 * @param append
	 *            Pass true to append, pass false to remove the existing content and
	 *            write
	 */

	public void writeInTextFile(File file, String text, boolean append) {
		FileWriter writer = null;
		logger.info("Writing in a text file");
		try {
			writer = new FileWriter(file, append);
			writer.write(text + "\r\n");
			writer.flush();
			logger.info("Wrote text: " + text + " in the text file");
		}
		catch (Exception e) {
			logger.error("Unable to write in the text file", e);
			e.printStackTrace();
		}

	}

	/**
	 * To get all files in a folder
	 *
	 * @param folderPath
	 *            Path of the folder to get the list of file names
	 * @return results Name of all the files in the folder
	 */

	public List<String> getFilesInFolder(String folderPath) {
		List<String> results = new ArrayList<String>();
		logger.info("Getting all files from folder path: " + folderPath);
		try {
			File[] files = new File(folderPath).listFiles();
			for (File file : files) {
				if (file.isFile()) {
					if (file.length() > 0) {
						results.add(file.getName());
						logger.info("File: " + file.getName() + " is present");
					}
					else
						logger.warn("File: " + file.getName()
								+ " is present but file size is 0. Hence it won't be returned");
				}
			}
		}
		catch (Exception e) {
			logger.error("Unable to get the files from the folder", e);
		}
		return results;
	}

	/**
	 * @param location
	 *            Delete Files or SubFolders for specified path
	 */
	public void deleteFilesFolders(String location) {
		File fileToDelete = new File(location);
		if (fileToDelete.isDirectory()) {
			for (File c : fileToDelete.listFiles())
				deleteFilesFolders(c.toString());
		}
		if (!fileToDelete.delete()) {
			logger.info(fileToDelete.toString() + " : does not exist");
		}
		else {
			logger.info(fileToDelete.toString() + " # Deleted");
		}
	}

	/**
	 * To clear the contents of the log file. Since log4j.appender.FA.append cannot
	 * be set as false due to messaging server integration
	 *
	 * @param path
	 *            Path of the file that needs to be cleared
	 */

	public void clearFile(String path) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write("");
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			logger.error("Please check the file path: " + path, e);
			e.printStackTrace();
		}
	}

	/**
	 * To create the screenshot folder if it doesn't exist, if it exists then delete
	 * its content.
	 */

	public void createCleanFolder(String dir, boolean clear) {
		try {
			File directory = new File(dir);
			if (!directory.exists()) {
				logger.info("Directory " + dir + " doesn't exist. Hence creating the folder");
				directory.mkdir();
			}
			else {
				if (clear) {
					logger.info("Directory " + dir + " arleady exists. Hence cleaning the contents of the folder");
					FileUtils.cleanDirectory(directory);
				}
			}
		}
		catch (Exception e) {
			logger.error("Unable to create / clean the folder " + dir);
		}
	}

	/**
	 * Zip's any file or Folders to Zip format
	 *
	 * @param sourceFileFolder
	 * @param tempfolderPath
	 * @param zipFileDestination
	 * @throws Exception
	 */
	public void compressFileFolders(String sourceFileFolder, String tempfolderPath, String zipFileDestination)
			throws Exception {
		zipFileDestination = tempfolderPath + "\\" + zipFileDestination;
		try {
			byte[] buffer = new byte[1024];
			File[] files = null;
			FileOutputStream fileOS = new FileOutputStream(zipFileDestination);
			ZipOutputStream zipOS = new ZipOutputStream(fileOS);
			File fileDirOrFile = new File(sourceFileFolder);
			if (fileDirOrFile.listFiles() != null)
				files = fileDirOrFile.listFiles();
			else {
				files = new File[1];
				files[0] = fileDirOrFile;
			}
			for (int count = 0; count < files.length; count++) {
				logger.info("Compressing File : " + files[count].getName());
				FileInputStream fileIS = new FileInputStream(files[count]);
				zipOS.putNextEntry(new ZipEntry(files[count].getName()));
				int length;
				while ((length = fileIS.read(buffer)) > 0) {
					zipOS.write(buffer, 0, length);
				}
				zipOS.closeEntry();
				fileIS.close();
			}

			zipOS.close();
		}
		catch (IOException e) {
			logger.info("Zip File Creation failed and exception is " + e);
		}
	}

	/**
	 * To wait until the file - fileName is downloaded in the path
	 *
	 * @param path
	 *            Path where the file should be present
	 * @param fileName
	 *            Name of the file which will be downloaded
	 */
	public boolean waitUntilFileIsDownloaded(String path, String fileName) {
		boolean found = false;
		ConfigurationLib configLib = new ConfigurationLib();
		try {
			ArrayList<String> files = null;
			for (int i = 0; i < Integer.parseInt(configLib.getRetryAttempts()); i++) {
				files = (ArrayList<String>) getFilesInFolder(path);
				if (files.contains(fileName)) {
					found = true;
					logger.info(fileName + " is present in " + path);
					Thread.sleep(2500);
					break;
				}
				else {
					logger.warn(fileName + " not found in " + path + ". Trying " + (i + 1) + " more time...");
					Thread.sleep(1000 * Integer.parseInt(configLib.getRetryDelay()));
				}
			}
		}
		catch (Exception e) {
			logger.error("Unable to wait until the file is downloaded");
		}
		return found;
	}
}