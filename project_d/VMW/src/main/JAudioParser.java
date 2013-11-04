package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JAudioParser {
	public static HashMap<String, ArrayList<String>> parseFV(File fv) {
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fv);
			NodeList root = doc.getDocumentElement().getChildNodes();
			for (int i = 2; i < root.getLength(); i++) { // iterate over
															// features
				Node n = root.item(i);
				for (int j = 2; j < n.getChildNodes().getLength(); j++) {
					Node n2 = n.getChildNodes().item(j);
					if (n2.getNodeName().equals("feature")) {
						String key = "";
						ArrayList<String> vals = new ArrayList<>();
						for (int k = 0; k < n2.getChildNodes().getLength(); k++) {
							Node n3 = n2.getChildNodes().item(k);
							if (n3.getNodeName().equals("name")) {
								key = n3.getChildNodes().item(0).getNodeValue();
							}
							if (n3.getNodeName().equals("v")) {
								vals.add(n3.getChildNodes().item(0)
										.getNodeValue());
							}
						}
						map.put(key, vals);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;

	}
}
