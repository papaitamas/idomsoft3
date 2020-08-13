package hu.yerico.idomsofttest.rest2.domain;

import java.util.List;

public class ReturnOkmanyDTO {

	private OkmanyDTO okmanyDto;
	
	private List<String> hibak;

	public OkmanyDTO getOkmanyDto() {
		return okmanyDto;
	}

	public void setOkmanyDto(OkmanyDTO okmanyDto) {
		this.okmanyDto = okmanyDto;
	}

	public List<String> getHibak() {
		return hibak;
	}

	public void setHibak(List<String> hibak) {
		this.hibak = hibak;
	}

}
