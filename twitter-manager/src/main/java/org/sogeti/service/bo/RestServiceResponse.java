package org.sogeti.service.bo;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

public class RestServiceResponse {
	private String nomService;
	private String serviceRunning;
	private List<String>  result;

    public RestServiceResponse(String nomService, String serviceRunning, List<String> result) {
        this.nomService = nomService;
        this.serviceRunning = serviceRunning;
        this.result = result;
    }
    
	public String getNomService() {
		return nomService;
	}

	public void setNomService(String nomService) {
		this.nomService = nomService;
	}

	public String getServiceRunning() {
		return serviceRunning;
	}

	public void setServiceRunning(String serviceRunning) {
		this.serviceRunning = serviceRunning;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}
}
