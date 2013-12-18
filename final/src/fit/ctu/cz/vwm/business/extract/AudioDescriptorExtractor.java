package fit.ctu.cz.vwm.business.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class AudioDescriptorExtractor implements Extractor {

	@Override
	public void extract(File file, AudioDocumentMap aDoc) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = "";
		while (true) {
			String readLine = br.readLine();
			if (readLine == null)
				break;
			String[] split = readLine.split(":");
			if (split[1].toLowerCase().equals("nan")) {
				split[1] = "-1";
			}
			aDoc.put(split[0], split[1]);
		}
		br.close();
	}

}
