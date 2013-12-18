package fit.ctu.cz.vwm.business.search.mlt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GengreResponseParser implements ResponseConverter<List<GenreResultMLT>, InputStream> {

	@Override
	public List<GenreResultMLT> parseResponse(InputStream data) throws Exception {
		List<GenreResultMLT> result = new ArrayList<>();
		if (true) {
			HttpConnection.printinBuffer(data);
			return result;
		}

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(data);
		doc.getDocumentElement().normalize();

		NodeList docs = doc.getElementsByTagName("doc");

		// TODO udelat parsing na genre result mlt
		for (int i = 0; i < docs.getLength(); i++) {
			Element docum = (Element) docs.item(i);
			Node item = docum.getElementsByTagName("str").item(0);
			String id = item.getTextContent();
			// result.add(id);
		}
		return result;
	}
}
