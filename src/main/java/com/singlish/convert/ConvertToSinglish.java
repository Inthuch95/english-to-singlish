package com.singlish.convert;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ConvertToSinglish {

	private static JSONObject jsonRules = new JSONObject();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parse arguments
		ArgumentParser parser = ArgumentParsers.newFor("ConvertToSinglish").build()
                .defaultHelp(true)
                .description("Convert normal English to Singlish.");
		parser.addArgument("json")
				.required(true)
				.help("JSON rules");
        
        Namespace namespace = null;
        
        try {
        	namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        // Read arguments
        String jsonFile = namespace.getString("json");
		JSONParser jsonParser = new JSONParser();
		try {
			jsonRules = (JSONObject) jsonParser.parse(new FileReader(jsonFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		process();
	}
	
	private static void process() {
		
		String line = "";
		String singlish = "";
		// Progress tracking variables
        int iDotNLCount = 1000;
        int iDotCount = 10;
        int iFileCount = 0;
		try {
			// Read input from stdin
	        Scanner scanner = new Scanner(System.in);
	        while (scanner.hasNext()) {
	        	// Print progress
        		if (iFileCount % iDotNLCount == 0) {
                    if (iFileCount > 0) {
                        System.err.print("\n");
                    }
                    System.err.print(iFileCount + " ");
                }
                if (iFileCount % iDotCount == 0) {
                    System.err.print(".");
                }
                iFileCount++;
                // Read line
				line = scanner.nextLine();
				singlish = searchReplace(line);
				// Clean
				singlish = singlish.substring(0, 1).toUpperCase() + singlish.substring(1);
				singlish = singlish.trim().replaceAll(" +", " ");
				System.out.println(singlish);
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String searchReplace(String english) {
		JSONArray rulesArray = (JSONArray) jsonRules.get("replace");
		JSONObject currentRule = new JSONObject();
		
		String regex = "";
		String replacement = "";
		String singlish = english;
		for (int i = 0; i < rulesArray.size(); i++) {
			currentRule = (JSONObject) rulesArray.get(i);
			regex = (String) currentRule.get("regex");
			replacement = (String) currentRule.get("replace");
			singlish = singlish.replaceAll(regex, replacement);
		}
		return singlish;
	}

}
