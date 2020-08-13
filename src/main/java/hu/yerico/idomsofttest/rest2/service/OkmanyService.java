package hu.yerico.idomsofttest.rest2.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import hu.yerico.idomsofttest.rest2.domain.OkmanyDTO;


@Service
public class OkmanyService {
	
	private static Logger logger = Logger.getLogger(OkmanyService.class);
	
	private static final String CLASSNAME = "OkmanyDTO";
	private static final String OKMANYTIPUS = "okmanytípus";
	private static final String OKMANYSZAM = "okmanyszám";
	private static final String OKMANYKEP = "okmanykep";
	private static final String LEJARATIIDO = "lejárati idő";
	private static final int IMAGEWIDTH = 827;
	private static final int IMAGEHEIGHT= 1063;
	
	private static final String OKMANYTIPUS_JSON = "kodszotar46_okmanytipus.json";

	
	HashMap<String, String> okmanytipusok;
	
	List<String> errors = new ArrayList<String>();
	
	public OkmanyService() {
		super();
		fillOkmanytipusok();
	}
	
	public List<String> checkOkmanyDTO(OkmanyDTO dto) {
		logger.debug("checkOkmanyDTO called");
		errors.clear();
		
		//okmanytipus
		String tipus = dto.getOkmTipus(); 
		if(StringUtils.isEmpty(tipus)) {
			errors.add(CLASSNAME + " "  + OKMANYTIPUS + " üres.");
		} else if(tipus.length() > 1) {
			errors.add(CLASSNAME + " " + OKMANYTIPUS + " túl hosszú. Kapott: " + tipus + ", elvárt: 1.");
		} else {
			if(!okmanytipusok.containsKey(tipus)) {
				errors.add(CLASSNAME + " " + OKMANYTIPUS + " érvénytelen érték. Kapott: " + tipus + ", elvárt: 1 - 6.");
			} else {
				//Okmanyszam
				String okmanyszam = dto.getOkmanySzam();
				if (StringUtils.isEmpty(okmanyszam)) {
					errors.add(CLASSNAME + " " + OKMANYSZAM + " üres");
				} else {
					checkOkmanySzam(tipus, okmanyszam);
				}
			}
		}
		
		//okmanykep
		if(dto.getOkmanyKep() == null || dto.getOkmanyKep().length == 0) {
			errors.add(CLASSNAME + " " + OKMANYKEP + " hiányzik");
		} else {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(dto.getOkmanyKep());
				BufferedImage image = ImageIO.read(bais); 
				if(image == null) {
					errors.add(CLASSNAME + " " + OKMANYKEP + " hibás képformátum");
				} else {
					if(image.getWidth() != IMAGEWIDTH || image.getHeight() != IMAGEHEIGHT) {
						errors.add(CLASSNAME + " " + OKMANYKEP + " nem megfelelő képméret. Kapott: " + image.getWidth() + "x" + image.getHeight() + ", elvárt: " + IMAGEWIDTH + "x" + IMAGEHEIGHT);
					}
				}
				
				//jpeg tipus check
				final ImageMetadata metadata = Imaging.getMetadata(dto.getOkmanyKep());
				if (metadata != null && !(metadata instanceof JpegImageMetadata)) {
					errors.add(CLASSNAME + " " + OKMANYKEP + " képformátum nem JPEG");
				}
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		//lejarati idő
		if(dto.getLejarDat() == null) {
			errors.add(CLASSNAME + " " + LEJARATIIDO + " hiányzik");
			dto.setErvenyes(false);
		} else {
			if(dto.getLejarDat().getTime() > System.currentTimeMillis()) {
				dto.setErvenyes(true);
			} else {
				dto.setErvenyes(false);
			}
		}
				
		return errors;
	}
	
	private void checkOkmanySzam(String tipus, String szam) {
		
		logger.debug("checkOkmanySzam called");
		String errorStart = CLASSNAME + " " + OKMANYSZAM + " (" + szam + ") ";
		
		switch (tipus) {
		case "1":
			if(szam.length() != 8) {
				errors.add(errorStart + "hossza nem megfelelő. Kapott: " + szam.length() + ", elvárt: 8.");
				return;
			}
			String s1 = szam.substring(0, 6);
			String s2 = szam.substring(6, 8);
			
			if (!s1.matches("^[0-9]+$") || !s2.toUpperCase().matches("^[A-Z]+$")) {
				errors.add(errorStart + " formátuma nem megfelelő (6 szám + 2 betű)");					
		    }
			break;
		case "2":
			if(szam.length() != 9) {
				errors.add(errorStart + " hossza nem megfelelő. Kapott: " + szam.length() + ", elvárt: 8.");
				return;
			}
			s1 = szam.substring(0, 2);
			s2 = szam.substring(2, 9);
			
			if (!s2.matches("^[0-9]+$") || !s1.toUpperCase().matches("^[A-Z]+$")) {
				errors.add(errorStart + " formátuma nem megfelelő (2 betű + 7 szám)");
		    }
			break;
		case "3":
			if(szam.length() != 8) {
				errors.add(errorStart + " hossza nem megfelelő. Kapott: " + szam.length() + ", elvárt: 8.");
				return;
			}
			s1 = szam.substring(0, 2);
			s2 = szam.substring(2, 8);
			
			if (!s2.matches("^[0-9]+$") || !s1.toUpperCase().matches("^[A-Z]+$")) {
				errors.add(errorStart + " formátuma nem megfelelő (2 betű + 6 szám)");
		    }
			break;
		default:		
			if(szam.length() != 10) {
				errors.add(CLASSNAME + " " + OKMANYSZAM + " hossza nem megfelelő. Kapott: " + szam.length() + ", elvárt: 10.");
			}
			break;

		}
	}
	
	
	private void fillOkmanytipusok() {
		JSONParser parser = new JSONParser();
		
		okmanytipusok = new HashMap<String, String>();
		
		File file = new File(getClass().getClassLoader().getResource(OKMANYTIPUS_JSON).getFile());
		
		try {
			JSONObject object = (JSONObject) parser.parse(new FileReader(file));
			
			JSONArray list = (JSONArray) object.get("rows");
			for (Object orow : list) {
				JSONObject row = (JSONObject) orow;
				okmanytipusok.put((String)row.get("kod"), (String)row.get("ertek"));
			}
			logger.debug("Okmanytipusok json file has been parsed and loaded.");
		} catch (Exception e) {			
			logger.error(e.getMessage(), e);
		}
	}

	public HashMap<String, String> getOkmanytipusok() {
		if(okmanytipusok == null) {
			fillOkmanytipusok();
		}
		return okmanytipusok;
	}

	public void setOkmanytipusok(HashMap<String, String> okmanytipusok) {
		this.okmanytipusok = okmanytipusok;
	}

}

