package hu.yerico.idomsofttest.rest2.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.yerico.idomsofttest.rest2.domain.OkmanyDTO;
import hu.yerico.idomsofttest.rest2.domain.ReturnOkmanyDTO;
import hu.yerico.idomsofttest.rest2.service.OkmanyService;


@RestController
@RequestMapping("/api/okmany")
public class OkmanyController {
	
	private static Logger logger = Logger.getLogger(OkmanyController.class);
	
	@Autowired
	OkmanyService okmanyService;
	
	@PostMapping("/check")
	public ReturnOkmanyDTO checkAndFillOkmanyDTO(@RequestBody OkmanyDTO dto) {
		logger.debug("checkAndFillOkmanyDTO called.");
		logger.debug("dto: " + dto);
		List<String> errors = new ArrayList<String>();
		ReturnOkmanyDTO returnDto = new ReturnOkmanyDTO();
		
		if(dto == null || okmanyDtoIsEmpty(dto)) {
			errors.add("OkmanyDTO hiányzik.");
			returnDto.setHibak(errors);
			returnDto.setOkmanyDto(dto);
			return returnDto;			
		}
		
		errors.addAll(okmanyService.checkOkmanyDTO(dto));
		
		returnDto.setOkmanyDto(dto);
		returnDto.setHibak(errors);
		
		return returnDto;
	}
	
	private boolean okmanyDtoIsEmpty(OkmanyDTO dto) {
		if (	dto.getLejarDat() == null &&
				dto.getOkmanySzam() == null &&
				dto.getOkmTipus() == null) {
			return true;
		}
		return false;
	}

}

