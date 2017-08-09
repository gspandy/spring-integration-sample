package sanchez.sanchez.sergio.dto.response;

import org.springframework.hateoas.ResourceSupport;

public class SchoolDTO extends ResourceSupport {
	
	
	private String identity;
	private String name;
	private String residence;
	private String location;
	private String province;
	private Integer tfno;
	private String email;
	
	public SchoolDTO(){}
	
	public SchoolDTO(String identity, String name, String residence, String location, String province, Integer tfno,
			String email) {
		super();
		this.identity = identity;
		this.name = name;
		this.residence = residence;
		this.location = location;
		this.province = province;
		this.tfno = tfno;
		this.email = email;
	}


	public String getIdentity() {
		return identity;
	}


	public void setIdentity(String identity) {
		this.identity = identity;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getResidence() {
		return residence;
	}


	public void setResidence(String residence) {
		this.residence = residence;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public Integer getTfno() {
		return tfno;
	}


	public void setTfno(Integer tfno) {
		this.tfno = tfno;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
}