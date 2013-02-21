package uk.ac.manchester.cs.comp60372;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 * Copyright (C) 2011, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Rafael Goncalves<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 12-Oct-2011
 *
 * This class MUST HAVE A ZERO ARGUMENT CONSTRUCTOR!
 */
public class Test {

	public static void main(String[] args) {

		// Input: 1 XML document
		if (args.length != 1) {
			System.err.println("Usage: java Test xmlfile.xml");
			System.exit(-1);
		}

		try {
			NamespaceAnalyzer na = new NamespaceAnalyzer();

			// Parse input file
			Document input = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(args[0]);

			// Get output report
			System.out.println("\tAnalysing namespaces...");
			Document outputDoc = na.check(input);

			if(outputDoc != null) {
				// Preparing output
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "namespaceAnalysisReport.dtd");

				// Setting (relative) output directory of reports
				File dir = new File("tests/Output/");

				// Setting output filename
				String filename = 
						args[0].substring(args[0].lastIndexOf("/")+1, args[0].indexOf("."))
						+ "-report.xml";
				File output = new File(dir, filename);

				// Serializing XML file to tests/Output/
				StreamResult result = new StreamResult(output);
				DOMSource source = new DOMSource(outputDoc);
				t.transform(source, result);  

				// Outputting XML file to stdout
				StreamResult resultStr = new StreamResult(new StringWriter());
				t.transform(source, resultStr);
				String xmlString = resultStr.getWriter().toString();
				System.out.println("Output Document:\n" + xmlString + "\n");
			}
			else {
				System.out.println("\tThe output document is NULL!\n\t\tRevise your NamespaceAnalyzer.check(...) method...\n");
			}
		}
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerException e) {
			e.printStackTrace();
		} 
		catch (DOMException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
