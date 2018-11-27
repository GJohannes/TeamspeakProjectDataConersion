package simplePackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;

import javafx.print.JobSettings;

public class LogDataConversionMain {

	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.out.println("Please only enter a single argument which is the path to the folder that should be changed");
			return;
		}
		
		File allFilesInOneFolder = new File(args[0]);
		
		if(!allFilesInOneFolder.exists()) {
			System.out.println("Could not execute. First argument has to be the path to the folder");
			return;
		} else if(allFilesInOneFolder.isFile()) {
			System.out.println("Could not execute. irst argument has to be the path to the folder with files and NOT A FILE");
			return;
		}
		
		
		for (int i = 0; i < allFilesInOneFolder.list().length; i++) {
			File singleFile = new File(allFilesInOneFolder.getPath() + "/" + allFilesInOneFolder.list()[i]);
			Path path = Paths.get(singleFile.getAbsolutePath());
			ArrayList<String> allJSONSofOneFile = (ArrayList<String>) Files.readAllLines(path);

			try {
				for (int j = 0; j < allJSONSofOneFile.size(); j++) {
					try {
						JSONParser parser = new JSONParser();
						parser.parse(allJSONSofOneFile.get(j));
						System.out.println("file: " + allFilesInOneFolder.list()[i] + " -- line: " + j + " is already a valid json file - Line was not changed");
						continue;
					} catch (Exception e) {

						String firstFooter = "{\"LocalDateTime \":\"" + allJSONSofOneFile.get(j).split("\"LocalDateTime \":", 2)[1];
						String[] tempArray = firstFooter.split(",", 2);
						String secondString = tempArray[0] + "\"," + tempArray[1];
						;

						String thirdSplitter = "\"Event\":";
						String[] thirdFooter = secondString.split(thirdSplitter, 2);
						String thirdString = thirdFooter[0] + thirdSplitter + "\"" + thirdFooter[1];

						String fourthSplitter = "LOG";
						String[] fourthFooter = thirdString.split(fourthSplitter, 2);
						String finalString = "";
						if (fourthFooter.length == 2) {
							finalString = fourthFooter[0] + fourthSplitter + "\"" + fourthFooter[1];
						} else {
							String fourthSplitterAlternative = "SERVER";
							String[] fourthFooterAlternative = thirdString.split(fourthSplitterAlternative, 2);
							finalString = fourthFooterAlternative[0] + fourthSplitterAlternative + "\"" + fourthFooterAlternative[1];
						}
						allJSONSofOneFile.set(j, finalString);
					}

				}
				FileWriter fileWriter = new FileWriter(singleFile);
				BufferedWriter writer = new BufferedWriter(fileWriter);

				for (int x = 0; x < allJSONSofOneFile.size(); x++) {
					writer.write(allJSONSofOneFile.get(x));
					writer.newLine();
				}
				writer.flush();
				writer.close();
				System.out.println("Successful rewritten file " + allFilesInOneFolder.list()[i]);

			} catch (Exception e) {
				System.out.println("Error in file " + allFilesInOneFolder.list()[i] + " Changed nothing");
				// e.printStackTrace();

			}
		}
	}
}
