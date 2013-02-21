package uk.ac.manchester.cs.msc.ssd.saxcalc;

import org.xml.sax.SAXException;

import uk.ac.manchester.cs.msc.ssd.saxcalc.impl.CalculatorImpl;

import java.io.*;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Sep-2011
 */
public class Runner {
	
	public int run(File file) throws FileNotFoundException, SAXException, NumberFormatException {
		CalculatorImpl calculator = new CalculatorImpl();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		return calculator.computeResult(bis);
	}
}
