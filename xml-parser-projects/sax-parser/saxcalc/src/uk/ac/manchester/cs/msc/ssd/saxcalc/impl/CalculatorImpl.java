package uk.ac.manchester.cs.msc.ssd.saxcalc.impl;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Sep-2011 <br>
 * Modify by: @author Guilermo Antonio Toro Bayona
 */
public class CalculatorImpl {
	/**
	 * int variable for result
	 */
	private int result;

	/**
	 * ZERO ARGUMENT CONSTRUCTOR - VERY IMPORTANT! DO NOT ALTER
	 */
	public CalculatorImpl() {
	}

	/**
	 * Computes the result to a calculation that is specified by an XML document
	 * that can be obtained from the given input stream.
	 * 
	 * @param is
	 *            The input stream from which the XML document can be obtained.
	 * @return The result of the calculation.
	 * @throws org.xml.sax.SAXException
	 *             If the document was invalid or there was some problem parsing
	 *             the calculation. <br>
	 *             Modified by: @author Guilermo Antonio Toro Bayona
	 */
	public int computeResult(InputStream is) throws SAXException,
			NumberFormatException {
		// Printing output for running calculator
		System.out.println("Running calculator...");
		// Create a SAX Parser factory
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		// Set TRUE for validation properties
		saxFactory.setValidating(true);
		// Creating a XML parser based on reader factory from SAX
		SAXParser saxParser = null;
		try {
			// Instance of the parser configuration
			saxParser = saxFactory.newSAXParser();
		} catch (ParserConfigurationException pce) {
			// Print a new description for the error
			System.out.println("Error in the Parser's Configuration");
			// If there are a problem creating the instance of the parser. Throw
			// a SAX Exception
			throw new SAXException(pce);
		}
		// Create a reader of XML based on the parser
		XMLReader xmlReader = saxParser.getXMLReader();
		// Create an instance of ContentHandler (Inner Class)
		ContentHandler contentHandler = new CalculatorHandler();
		// Assign the handler to the reader
		xmlReader.setContentHandler(contentHandler);
		// Create an instance of SaxErrorHanlder (Inner Class)
		SaxErrorHandler saxErrorHanlder = new SaxErrorHandler();
		// Assign the error handler to the reader
		xmlReader.setErrorHandler(saxErrorHanlder);
		// Create an instance for a source
		InputSource source = new InputSource(is);
		// Parsing the XML document
		try {
			// Call the method parse with the Source (file)
			xmlReader.parse(source);
			// Close the input stream as soon as the parse process has finished.
			is.close();
		} catch (IOException ioex) {
			// Print a new description for the error
			System.out.println("Error with the Input Stream");
			// If there are a problem closing the stream or parsing
			throw new SAXException(ioex);
		}
		// Printing output for parsing OK
		System.out.println("    .... parsed o.k.");
		System.out.println("    .... computed result");
		// Return a variable modified by the Inner Classes
		return this.result;
	}

	/**
	 * Inner Class for Parser Handler. This class extends from DefaultHandler
	 * and manage the logic for parsing the XML.
	 * 
	 * @author Guillermo Antonio Toro
	 */
	class CalculatorHandler extends DefaultHandler {
		/**
		 * String constant for 'expression' tag
		 */
		public static final String TAG_EXPRESSION = "expression";
		/**
		 * String constant for 'plus' tag
		 */
		public static final String TAG_PLUS = "plus";
		/**
		 * String constant for 'times' tag
		 */
		public static final String TAG_TIMES = "times";
		/**
		 * String constant for 'minus' tag
		 */
		public static final String TAG_MINUS = "minus";
		/**
		 * String constant for 'expression' tag
		 */
		public static final String TAG_INT = "int";
		/**
		 * List for Expression elements
		 */
		private List<Expression> expressions = new ArrayList<Expression>();

		/**
		 * Method that read the opening tags with SAX from the XML
		 * 
		 * @param namespaceURI
		 *            String with the URI for the NameSpace
		 * @param localName
		 *            String with the local name of the element read
		 * @param qualifiedName
		 *            String with the Qualified Name of the element read
		 * @param atts
		 *            Attributes related to the element read
		 * @author Guilermo Antonio Toro Bayona
		 */
		public void startElement(String namespaceURI, String localName,
				String qualifiedName, Attributes atts) throws SAXException {
			// Validation for tag EXPRESSION. If the tag is any of
			// them, then create an expression.
			if (!qualifiedName.equals(TAG_EXPRESSION)) {
				// Validate if the element read is an INT to extract the value
				if (qualifiedName.equals(TAG_INT)) {
					// Create an integer
					Integer operandValue = null;
					try {
						// Create a integer with the value read from the
						// attribute.
						operandValue = new Integer(atts.getValue("value"));
					} catch (NumberFormatException nfe) {
						// Print a new description for the error
						System.out
								.println("    .... My Parser Output: Error found while create a number with the value.");
						// Throw new SAXException due to any warning while
						// parsing
						throw nfe;
					}
					// Get the size of the expression list and calculate the
					// last
					// index
					Integer lastIndex = this.expressions.size() - 1;
					// Validate index
					if (lastIndex < 0) {
						// Instance an expression for the first element int
						Expression exp = new Expression(TAG_PLUS);
						// Add a new operand value for that expression class.
						exp.addOperand(operandValue);
						// Add the expression to the list
						this.expressions.add(exp);
					} else {
						// Get the last element in the list
						Expression exp = expressions.get(lastIndex);
						// Add a new operand value for that expression class.
						exp.addOperand(operandValue);
					}
				} else {
					// Instance of Expression class with the operation name
					Expression exp = new Expression(qualifiedName);
					// Add the expression to the list
					this.expressions.add(exp);
				}
			}

		}

		/**
		 * Method that read the closing tags with SAX from the XML
		 * 
		 * @param namespaceURI
		 *            String with the URI for the NameSpace
		 * @param localName
		 *            String with the local name of the element read
		 * @param qualifiedName
		 *            String with the Qualified Name of the element read
		 * @author Guilermo Antonio Toro Bayona
		 */
		public void endElement(String namespaceURI, String localName,
				String qualifiedName) throws SAXException {
			// Validate if the tags is not an INT.
			if (!qualifiedName.equals(TAG_INT)) {
				// Validate the size of the list.
				// If it is grater than 1. Calculate and modify the previous
				// expression.
				if (this.expressions.size() > 1) {
					// Get the last index.
					Integer lastIndex = this.expressions.size() - 1;
					// Get the last expression of the list.
					Expression exp = this.expressions.get(lastIndex);
					// Invoke the method for calculate the expression
					Integer expressionValue = exp.calculateExpression();
					// Remove the last element from the list.
					this.expressions.remove(lastIndex.intValue());
					// Get the new last index of the list
					lastIndex = this.expressions.size() - 1;
					// Get the previous element in the list
					exp = expressions.get(lastIndex);
					// Add a new operand value with the result of the previous
					// expression.
					exp.addOperand(expressionValue);
				} else if (this.expressions.size() == 1) {
					// Get the last index of the list
					Integer lastIndex = this.expressions.size() - 1;
					// Get the last expression
					Expression exp = this.expressions.get(lastIndex);
					// Invoke the method for calculate the expression
					Integer expressionValue = exp.calculateExpression();
					// Remove the last element from the list.
					this.expressions.remove(lastIndex.intValue());
					// Assign the value to the external variable for result.
					result = expressionValue;
				}
			}
		}
	}

	/**
	 * Inner Class for Error Handler. This class implements the interface
	 * ErrorHandler and manage the warnings, error and fatal error that are
	 * issued while parsing the XML.
	 * 
	 * @author Guillermo Antonio Toro
	 */
	public class SaxErrorHandler implements ErrorHandler {
		/**
		 * Method that manage the warning while the XML is parsed.
		 * 
		 * @param SAXParseException
		 *            exception with warning information
		 */
		public void warning(SAXParseException spe) throws SAXException {
			// Print a new description for the error
			System.out
					.println("    .... My Parser Output: Warning found while parsing. Parsing process stoped.");
			// Throw new SAXException due to any warning while parsing
			throw new SAXException(spe);
		}

		/**
		 * Method that manage the error while the XML is parsed.
		 * 
		 * @param SAXParseException
		 *            exception with warning information
		 */
		public void error(SAXParseException spe) throws SAXException {
			// Print a new description for the error
			System.out
					.println("    .... My Parser Output: Error found while parsing. Parsing process stoped.");
			// Throw new SAXException due to any warning while parsing
			throw new SAXException(spe);
		}

		/**
		 * Method that manage the fatalError while the XML is parsed.
		 * 
		 * @param SAXParseException
		 *            exception with warning information
		 */
		public void fatalError(SAXParseException spe) throws SAXException {
			// Print a new description for the error
			System.out
					.println("    .... My Parser Output: Fatal error found while parsing. Parsing process stoped.");
			// Throw new SAXException due to any warning while parsing
			throw new SAXException(spe);
		}
	}

	/**
	 * Inner Class that represents an Expression. An Expression is a
	 * mathematical operation, plus a list of different operands. The result of
	 * the expression is based on the application of the operation to all the
	 * operands.
	 * 
	 * @author Guilermo Antonio Toro Bayona
	 */
	class Expression {
		/**
		 * String for operation name
		 */
		private String operation;
		/**
		 * List of integer for operands
		 */
		private List<Integer> operands;
		/**
		 * Integer to store the result of the expression
		 */
		private Integer expressionResult;

		/**
		 * Constructor Overloaded for the class Expression
		 * 
		 * @param operation
		 *            String with the name of the operation
		 * @author Guilermo Antonio Toro Bayona
		 */
		public Expression(String operation) {
			// Initialize the list
			this.operands = new ArrayList<Integer>();
			// Assign the operation value
			this.operation = operation;
		}

		/**
		 * Method that calculate the expression.
		 * 
		 * @return Integer with the value of the expression execution.
		 * @author Guilermo Antonio Toro Bayona
		 */
		public Integer calculateExpression() {
			// Create a loop to go through the list of operands
			for (int operandIndex = 0; operandIndex < this.operands.size(); operandIndex++) {
				// Validate if the operation is PLUS
				if (this.operation.equals(CalculatorHandler.TAG_PLUS)) {
					// Validate the first index
					if (operandIndex == 0) {
						// Assign 0 as a first value to the variable.
						this.expressionResult = 0;
					}
					// Add the operands
					this.expressionResult += this.operands.get(operandIndex);
				}
				// Validate if the operation is TIMES
				else if (this.operation.equals(CalculatorHandler.TAG_TIMES)) {
					// Validate the first index
					if (operandIndex == 0) {
						// Assign 1 as a first value to the variable.
						this.expressionResult = 1;
					}
					// Multiply the operands
					this.expressionResult *= this.operands.get(operandIndex);
				}
				// Validate if the operation is MINUS
				else if (this.operation.equals(CalculatorHandler.TAG_MINUS)) {
					// Validate the first index
					if (operandIndex == 0) {
						// Assign the first value as the first value for the
						// subtraction.
						this.expressionResult = this.operands.get(operandIndex);
					} else {
						// Subtract the operands.
						this.expressionResult -= this.operands
								.get(operandIndex);
					}
				}
			}
			// Return the result calculated for the expression.
			return this.expressionResult;
		}

		/**
		 * Method to add and operand to the internal list of operands.
		 * 
		 * @param operand
		 *            Integer operand to be added.
		 */
		public void addOperand(Integer operand) {
			// Add the operand to the list
			this.operands.add(operand);
		}
	}
}

