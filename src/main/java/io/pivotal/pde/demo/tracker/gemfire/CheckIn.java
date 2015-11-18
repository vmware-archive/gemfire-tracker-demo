package io.pivotal.pde.demo.tracker.gemfire;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

@Region("CheckIn")
public class CheckIn {
	@Id
	private String id;
	private String plate;
	private String city;
	private Date timestamp;
	
	
	// will always be generated
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
