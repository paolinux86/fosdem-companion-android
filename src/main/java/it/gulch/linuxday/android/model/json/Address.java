package it.gulch.linuxday.android.model.json;

import java.io.Serializable;

/**
 * Created by paolo on 06/09/14.
 */
public class Address implements Serializable
{
	private Long address_id;

	private String address;

	private String city;

	private String state;

	private String latitude;

	private String longitude;

	private byte[] address_map;

	public Long getAddress_id()
	{
		return address_id;
	}

	public void setAddress_id(Long address_id)
	{
		this.address_id = address_id;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}

	public byte[] getAddress_map()
	{
		return address_map;
	}

	public void setAddress_map(byte[] address_map)
	{
		this.address_map = address_map;
	}
}
