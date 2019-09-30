/* XMLLib Library contains the most commonly used methods to perform actions on XML file
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;

/**
 * This class contains all the methods / actions that can be performed on XML
 * File
 */
public class XMLLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Document doc;
	private Element root;

	/**
	 * To Initialized an XML document
	 *
	 * @return doc Returns the document object
	 */
	public Document initializeXml() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
			logger.info("Initialized XML document");
		}
		catch (Exception e) {
			logger.error("Unable to Initializ XML");
		}
		return doc;
	}

	/**
	 * To add a root element
	 *
	 * @param rootName
	 *            Name of the root element
	 * @return root Root element object
	 */
	public Element addRootElement(String rootName) {
		try {
			root = doc.createElement(rootName);
			doc.appendChild(root);
			logger.info("Added root element " + rootName);
		}
		catch (Exception e) {
			logger.error("Unable to add root element");
		}
		return root;
	}

	/**
	 * To add a child element to a parent element
	 *
	 * @param parentElement
	 *            Parent element to which the child element should be added
	 * @param childName
	 *            Name of the child element
	 * @return childEle Child element object
	 */
	public Element addChildElement(Element parentElement, String childName) {
		Element childEle = null;
		try {
			childEle = doc.createElement(childName);
			parentElement.appendChild(childEle);
			logger.info("Added child " + childName);
		}
		catch (Exception e) {
			logger.error("Unable to add child element " + childName);
		}
		return childEle;
	}

	/**
	 * To add attribute to a node
	 *
	 * @param node
	 *            Node to which the attribute should be added
	 * @param attrbuteName
	 *            Name of the attribute
	 * @param attributeVal
	 *            Value of attribute to be udpated
	 */
	public void addAttribute(Element node, String attrbuteName, String attributeVal) {
		try {
			Attr attr = doc.createAttribute(attrbuteName);
			attr.setValue(attributeVal);
			node.setAttributeNode(attr);
		}
		catch (Exception e) {
			logger.error("Unable to add attribute " + attrbuteName + " with value " + attributeVal + " to " + node);
		}
	}

	/**
	 * To save the initialized xml document to the disk
	 *
	 * @param path
	 *            The complete path with the file name where the XML document should
	 *            be saved
	 */
	public void saveXml(String path) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);

			StringWriter writer = new StringWriter();
			StreamResult consoleResult = new StreamResult(writer);
			transformer.transform(source, consoleResult);
			String xmlString = writer.getBuffer().toString();
			logger.info("Generated the import XML: " + System.getProperty("line.separator") + xmlString);
		}
		catch (Exception e) {
			logger.error("Unable to save the XML to " + path);
		}
	}
}