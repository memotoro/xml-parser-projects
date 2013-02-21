package uk.ac.manchester.cs.msc.ssd.saxcalc;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Sep-2011
 */
public class TestCalculator {

	public static final int FILE_NAME_ARG = 0;

	public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		String answer = "";

		try {
			if(args.length != 1) {
				System.out.println("Invalid arguments");
				System.out.println("Expected: FileName");
				System.exit(1);
			}

			File testFile = new File(args[FILE_NAME_ARG]);

			answer = testFile.toString().substring(testFile.toString().lastIndexOf("_")+1,testFile.toString().lastIndexOf("."));
			
			Runner runner = new Runner();
			int result = runner.run(testFile);
			
			if(!answer.startsWith("err")) {
				int intAnswer = Integer.parseInt(answer);

				if(result == intAnswer) {
					System.out.println("RESULT: Correct! Expected: " + answer + ", and got: " + result);
				}
				else {
					System.out.println("RESULT: WRONG! Expected: " + answer + ", but got: " + result);
				}
			}
			else {
				if(answer.equals("errnan")) {
					System.out.println("RESULT: WRONG! Expected: " + answer + " (NumberFormatException), but got: " + result);
				}
				else if(answer.equals("errinvalid")) {
					System.out.println("RESULT: WRONG! Expected: " + answer + " (SAXException), but got: " + result);
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Input file not found: " + e.getMessage());
		}
		catch (SAXException e) {
			if(answer.equals("errinvalid")) {
				System.out.println("RESULT: Correct Error!");
			}
			else {
				System.out.println("RESULT: WRONG! Expected: " + answer + ", but got errinvalid (SAXException)");
			}
		}
		catch (NumberFormatException e) {
			if(answer.equals("errnan")) {
				System.out.println("RESULT: Correct Error!");
			}
			else {
				System.out.println("RESULT: WRONG! Expected: " + answer + ", but got errnan (NumberFormatException)");
			}
		}
	}
}
