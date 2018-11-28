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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.print.JobSettings;

public class LogDataConversionMain {
	private static boolean previousLogWasSTARTEDLog = false;
	private static int numberOfPeopleOnTheServer = 0;
	private static JSONParser parser = new JSONParser();

	public static void main(String[] args) throws IOException {
//		String[] debugArray = new String[1];
//		debugArray[0] = "C:\\GitRepository\\CurrentTeamspeak3Project\\log";
//		args = debugArray;

		if (args.length != 1) {
			System.out.println("Please only enter a single argument which is the path to the folder that should be changed");
			return;
		}

		File allFilesInOneFolder = new File(args[0]);

		if (!allFilesInOneFolder.exists()) {
			System.out.println("Could not execute. First argument has to be the path to the folder");
			return;
		} else if (allFilesInOneFolder.isFile()) {
			System.out.println("Could not execute. irst argument has to be the path to the folder with files and NOT A FILE");
			return;
		}

		for (int i = 0; i < allFilesInOneFolder.list().length; i++) {
			File singleFile = new File(allFilesInOneFolder.getPath() + "/" + allFilesInOneFolder.list()[i]);
			Path path = Paths.get(singleFile.getAbsolutePath());
			ArrayList<String> allJSONSofOneFile = (ArrayList<String>) Files.readAllLines(path);

			try {
				for (int j = 0; j < allJSONSofOneFile.size(); j++) {

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
						if (previousLogWasSTARTEDLog) {
							numberOfPeopleOnTheServer++;
						} else {
							numberOfPeopleOnTheServer = 1;
						}
						previousLogWasSTARTEDLog = true;
					} else {
						String fourthSplitterAlternative = "SERVER";
						String[] fourthFooterAlternative = thirdString.split(fourthSplitterAlternative, 2);
						finalString = fourthFooterAlternative[0] + fourthSplitterAlternative + "\"" + fourthFooterAlternative[1];
						if (finalString.contains("JOIN")) {
							numberOfPeopleOnTheServer++;
							previousLogWasSTARTEDLog = false;
						} else {
							numberOfPeopleOnTheServer--;
							previousLogWasSTARTEDLog = false;
						}
					}
					//in case that json was already valid
					finalString = finalString.replace("\"\"", "\"");

					// now the line should be a valid JSON
					JSONObject oneLine = new JSONObject();
					oneLine = (JSONObject) parser.parse(finalString);

					oneLine.put("numberOfPeopleOnServer", numberOfPeopleOnTheServer);

					allJSONSofOneFile.set(j, oneLine.toString());
				}
				
				//in case of a started log at end of a file and started log at beginning of a file
				previousLogWasSTARTEDLog = false;
				
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
				e.printStackTrace();

			}
		}
	}
}
