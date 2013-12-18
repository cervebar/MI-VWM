package fit.ctu.cz.vwm.business.extract;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLParser {

	Node doc;
	public ArrayList<String> vals;
	public ArrayList<String> descriptors;
	boolean enablevals;
	boolean enabledesc;
	boolean firstinner;

	public XMLParser(Document d, File f) {
		this.doc = d.getChildNodes().item(0);
		vals = new ArrayList<>();
		descriptors = new ArrayList<>();
		this.saveVals();/*
						 * try { this.printToFile(d, f); } catch (Exception e) {
						 * e.printStackTrace(); }
						 */

	}

	public void printToFile(Document d, File f) throws Exception {

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// write MPEG-7 description to file
		String outputPath = "results/txt/"
				+ f.getName().substring(0, f.getName().length() - 4) + ".txt";
		PrintStream fileStream = new PrintStream(new File(outputPath));

		transformer.transform(new DOMSource(d), new StreamResult(fileStream));
	}

	public void saveVals() {
		enablevals = false;
		enabledesc = false;
		firstinner = true;
		Node n = doc;
		saveVal(n);
	}

	public void saveVal(Node n) {

		if (n.getNodeName() == "Vector" || n.getNodeName() == "SeriesOfVector"
				|| n.getNodeName() == "Scalar"
				|| n.getNodeName() == "SeriesOfScalar"
				|| n.getNodeName() == "CovarianceInverse"
				|| n.getNodeName() == "Mean"
				|| n.getNodeName() == "Transitions") {
			enablevals = true;
		}
		if (enabledesc && !enablevals) {
			if (firstinner) {
				descriptors.remove(descriptors.size() - 1);
				firstinner = false;
			}
			descriptors.add(n.getNodeName());
		}

		if (n.getNodeName() == "AudioDescriptor") {
			enabledesc = true;
			NamedNodeMap nmp = n.getAttributes();
			descriptors.add(nmp.getNamedItem("xsi:type").getNodeValue());
		}
		if (!n.equals(null) && n.getNodeValue() != null && enablevals) {
			vals.add(n.getNodeValue());
			enablevals = false;
		}

		if (n.getChildNodes().getLength() > 0) {
			for (int i = 0; i < n.getChildNodes().getLength(); i++) {
				saveVal(n.getChildNodes().item(i));
			}
			if (enabledesc && n.getNodeName() == "AudioDescriptor") {
				firstinner = true;
				enabledesc = false;
			}
		}
	}

}
