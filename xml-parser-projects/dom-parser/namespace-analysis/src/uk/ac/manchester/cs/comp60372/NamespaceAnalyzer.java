package uk.ac.manchester.cs.comp60372;

import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.util.ArrayList;
import java.util.List;

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
 * Date: 06-Oct-2011 modify by:<br/>
 * 
 * @author Guillermo Antonio Toro Bayona
 * 
 *         This class MUST HAVE A ZERO ARGUMENT CONSTRUCTOR!
 */
public class NamespaceAnalyzer {
	/**
	 * String with constants for parsing and DTD elements
	 */
	public static final String XPATH_EXPRESSION_POSITION = "/*";
	public static final String XPATH_EXPRESSION_OPEN_BRAKET = "[";
	public static final String XPATH_EXPRESSION_CLOSE_BRAKET = "]";
	public static final String XML_NAMESPACE_DECLARATION = "xmlns";
	public static final String XML_COLON = ":";
	/**
	 * String with DTD elements
	 */
	public static final String DTD_FILE = "namespaceAnalysisReport.dtd";
	public static final String DTD_ELEMENT_NAMESPACEREPORT = "namespaceReport";
	public static final String DTD_ELEMENT_DETAILS = "details";
	public static final String DTD_ELEMENT_PROBLEM = "problem";
	public static final String DTD_ELEMENT_DECLARATION = "declaration";
	public static final String DTD_ATTRIBUTE_VERDICT = "verdict";
	public static final String DTD_ATTRIBUTE_PREFIX = "prefix";
	public static final String DTD_ATTRIBUTE_NAMESPACE = "namespace";
	public static final String DTD_ATTRIBUTE_PATH = "path";
	public static final String DTD_VALUE_ATTRIBUTE_VERDICT_NORMAL = "normal";
	public static final String DTD_VALUE_ATTRIBUTE_VERDICT_DECEPTIVE = "deceptive";
	public static final String DTD_VALUE_ATTRIBUTE_VERDICT_SUPER_CONFUSING = "super-confusing";
	/**
	 * List with the problems identified
	 */
	private List<Problem> listProblems = new ArrayList<Problem>();
	/**
	 * Document with Name Space report
	 */
	private Document documentNameSpaceReport;
	/**
	 * StringBuffer
	 */
	private StringBuffer xPathExpressionBuffer;

	public NamespaceAnalyzer() {
		// Do not specify a different constructor to this empty constructor!
	}

	/**
	 * Method that check the name-spaces in the document
	 * 
	 * @param srcfile
	 *            Document XML from DOM model
	 * @return Document report
	 * @author Guillermo Antonio Toro Bayona
	 */
	public Document check(Document srcfile) {
		try {
			// Create the factory
			DocumentBuilderFactory myDocumentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			// Validate with respect to the XSD
			myDocumentBuilderFactory.setValidating(true);
			// Create a new document builder
			DocumentBuilder myDocumentBuilder = myDocumentBuilderFactory
					.newDocumentBuilder();
			// Create document to Report
			this.documentNameSpaceReport = myDocumentBuilder.newDocument();
			// Assign the document input
			Document documentToAnalyze = srcfile;
			// Take the root element
			Element documentElement = documentToAnalyze.getDocumentElement();
			// Create Node Description
			this.xPathExpressionBuffer = new StringBuffer();
			this.xPathExpressionBuffer.append(XPATH_EXPRESSION_POSITION);
			this.xPathExpressionBuffer.append(XPATH_EXPRESSION_OPEN_BRAKET);
			this.xPathExpressionBuffer.append(1);
			this.xPathExpressionBuffer.append(XPATH_EXPRESSION_CLOSE_BRAKET);
			// Call method to analyze nodes
			nodesAnalysis(documentElement, xPathExpressionBuffer.toString());
			// Create a document
			Element rootElement = this.documentNameSpaceReport
					.createElement(DTD_ELEMENT_NAMESPACEREPORT);
			// Create Verdict
			Attr verdictAttribute = this.documentNameSpaceReport
					.createAttribute(DTD_ATTRIBUTE_VERDICT);
			// Assign to root element
			rootElement.setAttributeNode(verdictAttribute);
			// Assign to document
			this.documentNameSpaceReport.appendChild(rootElement);
			// short for verdict
			short veredict = 0;
			// Loop
			for (Problem problem : this.listProblems) {
				// Validate
				if (problem != null && problem.getListDeclaration() != null
						&& problem.getListDeclaration().size() > 1) {
					// Validate
					if (!problem.isDeceptive()) {
						// set value
						veredict = 1;
					} else {
						// set value
						veredict = 2;
					}
					break;
				}
			}
			// Validation nomarlity
			if (veredict == 0) {
				// Validate
				verdictAttribute
						.setNodeValue(DTD_VALUE_ATTRIBUTE_VERDICT_NORMAL);
			}
			// It's super-confusing
			else if (veredict == 1) {
				// Validate
				verdictAttribute
						.setNodeValue(DTD_VALUE_ATTRIBUTE_VERDICT_SUPER_CONFUSING);
			}
			// Deceptive
			else if (veredict == 2) {
				// Validate
				verdictAttribute
						.setNodeValue(DTD_VALUE_ATTRIBUTE_VERDICT_DECEPTIVE);
				// Create details
				Element detailsElement = this.documentNameSpaceReport
						.createElement(DTD_ELEMENT_DETAILS);
				// Append
				rootElement.appendChild(detailsElement);
				// Loop
				for (Problem problem : this.listProblems) {
					// Validate
					if (problem != null && problem.getListDeclaration() != null
							&& problem.getListDeclaration().size() > 1) {
						// Element by prefix
						Element problemElement = this.documentNameSpaceReport
								.createElement(DTD_ELEMENT_PROBLEM);
						detailsElement.appendChild(problemElement);
						// Loop for declaration
						for (Declaration declaration : problem
								.getListDeclaration()) {
							// Create declaration
							Element declarationElement = this.documentNameSpaceReport
									.createElement(DTD_ELEMENT_DECLARATION);
							// Append
							problemElement.appendChild(declarationElement);
							// Create attribute
							Attr prefixAttribute = this.documentNameSpaceReport
									.createAttribute(DTD_ATTRIBUTE_PREFIX);
							// Set attribute value
							prefixAttribute.setNodeValue(declaration
									.getPrefix());
							// Set attribute
							declarationElement
									.setAttributeNode(prefixAttribute);
							// Create attribute
							Attr nameSpacetAttribute = this.documentNameSpaceReport
									.createAttribute(DTD_ATTRIBUTE_NAMESPACE);
							// Set attribute value
							nameSpacetAttribute.setNodeValue(declaration
									.getNameSpace());
							// Set attribute
							declarationElement
									.setAttributeNode(nameSpacetAttribute);
							// Create attribute
							Attr xpathExpressionAttribute = this.documentNameSpaceReport
									.createAttribute(DTD_ATTRIBUTE_PATH);
							// Set attribute value
							xpathExpressionAttribute.setNodeValue(declaration
									.getxPathExpression());
							// Set attribute
							declarationElement
									.setAttributeNode(xpathExpressionAttribute);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return the documents
		return this.documentNameSpaceReport;
	}

	/**
	 * Recursion method to go through the DOM tree and identify attributes for
	 * each node
	 * 
	 * @param node
	 *            Node
	 * @author Guillermo Antonio Toro Bayona
	 */
	private void nodesAnalysis(Node node, String position) {
		// Validate null
		if (node != null) {
			// Get attributes
			NamedNodeMap namedNodeMap = node.getAttributes();
			// Validate
			if (namedNodeMap != null) {
				// Loop for attributes
				for (int attributeIndex = 0; attributeIndex < namedNodeMap
						.getLength(); attributeIndex++) {
					// Validate the name of the attribute
					Node nodeAttribute = namedNodeMap.item(attributeIndex);
					// Validate the name
					if (nodeAttribute.getNodeName() != null
							&& nodeAttribute.getNodeName().contains(
									XML_NAMESPACE_DECLARATION + XML_COLON)) {
						// Take prefix and value
						String nameSpacePrefix = nodeAttribute.getNodeName()
								.substring(
										nodeAttribute.getNodeName()
												.lastIndexOf(XML_COLON) + 1);
						String nameSpaceValue = nodeAttribute.getNodeValue();
						/*
						 * Start Validation code for deceptive
						 */
						// Get from the map a problem for Deceptive (name-space
						// as key)
						Problem problemByPrefix = new Problem(nameSpacePrefix,
								true);
						// Get index
						int problemPrefixIndex = this.listProblems
								.indexOf(problemByPrefix);
						// Create new declaration
						Declaration declaration = new Declaration();
						// Set attributes
						declaration.setPrefix(nameSpacePrefix);
						declaration.setNameSpace(nameSpaceValue);
						// Set the expression
						declaration.setxPathExpression(declaration
								.getxPathExpression() == null ? position
								: declaration.getxPathExpression() + position);
						// Validate
						if (problemPrefixIndex < 0) {
							// Add declaration to problem
							problemByPrefix.getListDeclaration().add(
									declaration);
							// Put in the map
							this.listProblems.add(problemByPrefix);
						} else {
							// Get the problem
							problemByPrefix = this.listProblems
									.get(problemPrefixIndex);
							// Compare values
							for (Declaration storeDeclaration : problemByPrefix
									.getListDeclaration()) {
								// Validate different values for name-space
								// (Validation for Deceptive)
								if (!storeDeclaration.getNameSpace().equals(
										nameSpaceValue)) {
									// Add declaration to problem
									problemByPrefix.getListDeclaration().add(
											declaration);
									break;
								}
							}
						}
						/*
						 * End Validation code for deceptive
						 */
						/*
						 * Start Validation code for super-confusing (part of
						 * normality)
						 */
						// Get from the map a problem for Deceptive (name-space
						// as key)
						Problem problemByUri = new Problem(nameSpaceValue,
								false);
						// Get index
						int problemUriIndex = this.listProblems
								.indexOf(problemByUri);
						// Create new declaration
						declaration = new Declaration();
						// Set attributes
						declaration.setPrefix(nameSpacePrefix);
						declaration.setNameSpace(nameSpaceValue);
						// Set the expression
						declaration.setxPathExpression(declaration
								.getxPathExpression() == null ? position
								: declaration.getxPathExpression() + position);
						// Validate
						if (problemUriIndex < 0) {
							// Add declaration to problem
							problemByUri.getListDeclaration().add(declaration);
							// Put in the map
							this.listProblems.add(problemByUri);
						} else {
							// Get the problem
							problemByUri = this.listProblems
									.get(problemUriIndex);
							// Compare values
							for (Declaration storeDeclaration : problemByUri
									.getListDeclaration()) {
								// Validate different values for name-space
								// prefix (Validation for Super-Confusing and
								// Normality)
								if (!storeDeclaration.getPrefix().equals(
										nameSpacePrefix)) {
									// Add declaration to problem
									problemByUri.getListDeclaration().add(
											declaration);
									break;
								}
							}
						}
						/*
						 * End Validation code for super-confusing (part of
						 * normality)
						 */
					}
				}
			}
			// Get children
			NodeList nodeListChildren = node.getChildNodes();
			// Validate
			if (nodeListChildren != null) {
				// Children count
				int elementChildren = 0;
				// Loop for every child
				for (int nodeIndex = 0; nodeIndex < nodeListChildren
						.getLength(); nodeIndex++) {
					// Get the node at index
					Node nodeChildren = nodeListChildren.item(nodeIndex);
					// Validation
					if (nodeChildren.getNodeType() == Node.ELEMENT_NODE) {
						// Increase the control
						elementChildren++;
						// Node Description
						this.xPathExpressionBuffer.delete(0,
								this.xPathExpressionBuffer.length());
						this.xPathExpressionBuffer.append(position);
						this.xPathExpressionBuffer
								.append(XPATH_EXPRESSION_POSITION);
						this.xPathExpressionBuffer
								.append(XPATH_EXPRESSION_OPEN_BRAKET);
						this.xPathExpressionBuffer.append(elementChildren);
						this.xPathExpressionBuffer
								.append(XPATH_EXPRESSION_CLOSE_BRAKET);
						// Recursion
						nodesAnalysis(nodeChildren,
								this.xPathExpressionBuffer.toString());
					}
				}
			}
		}
	}

	/**
	 * Inner Class that represents a Problem with name-spaces in XML
	 * 
	 * @author Guillermo Antonio Toro Bayona
	 * 
	 */
	class Problem {
		/**
		 * String key
		 */
		private String key;
		/**
		 * boolean for deceptive problems. Otherwise is super-confusing problems
		 */
		private boolean deceptive;
		/**
		 * List of declaration problems
		 */
		private List<Declaration> listDeclaration;

		/**
		 * Constructor
		 * 
		 * @param key
		 */
		public Problem(String key, boolean deceptive) {
			this.key = key;
			this.deceptive = deceptive;
			this.listDeclaration = new ArrayList<Declaration>();
		}

		// Getter and setter
		public List<Declaration> getListDeclaration() {
			return listDeclaration;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setListDeclaration(List<Declaration> listDeclaration) {
			this.listDeclaration = listDeclaration;
		}

		public boolean isDeceptive() {
			return deceptive;
		}

		public void setDeceptive(boolean deceptive) {
			this.deceptive = deceptive;
		}

		@Override
		public boolean equals(Object obj) {
			// Validate instance of
			if (!(obj instanceof Problem)) {
				return false;
			} else {
				Problem problem = (Problem) obj;
				return problem.key.equals(this.key);
			}
		}
	}

	/**
	 * Inner Class that represents a Declaration of problem with name-spaces in
	 * XML
	 * 
	 * @author Guillermo Antonio Toro Bayona
	 * 
	 */
	class Declaration {
		/**
		 * String prefix z
		 */
		private String prefix;
		/**
		 * String with name-space URI
		 */
		private String nameSpace;
		/**
		 * String with xPathExpression of the declaration error.
		 */
		private String xPathExpression;

		/**
		 * Constructor
		 */
		public Declaration() {
		}

		// Getter and setter
		public String getNameSpace() {
			return nameSpace;
		}

		public void setNameSpace(String nameSpace) {
			this.nameSpace = nameSpace;
		}

		public String getxPathExpression() {
			return xPathExpression;
		}

		public void setxPathExpression(String xPathExpression) {
			this.xPathExpression = xPathExpression;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
	}
}
