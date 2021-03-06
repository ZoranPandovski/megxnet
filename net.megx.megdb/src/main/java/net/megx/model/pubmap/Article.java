package net.megx.model.pubmap;

import java.util.Date;

public class Article {

	private Integer pmid;
	private Double lon;
	private Double lat;
	private String articleXML;
	private String userName;
	private String megxBarJSON;
	private Date created;
	private String worldRegion;
	private String place;

	public Integer getPmid() {
		return pmid;
	}

	public void setPmid(Integer pmid) {
		this.pmid = pmid;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public String getArticleXML() {
		return articleXML;
	}

	public void setArticleXML(String articleXML) {
		this.articleXML = articleXML;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMegxBarJSON() {
		return megxBarJSON;
	}

	public void setMegxBarJSON(String megxBarJSON) {
		this.megxBarJSON = megxBarJSON;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getWorldRegion() {
		return worldRegion;
	}

	public void setWorldRegion(String worldRegion) {
		this.worldRegion = worldRegion;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "Article [pmid=" + pmid + ", lon=" + lon + ", lat=" + lat
				+ ", articleXML=" + articleXML + ", userName=" + userName
				+ ", megxBarJSON=" + megxBarJSON + ", created=" + created
				+ ", worldRegion=" + worldRegion + ", place=" + place + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((articleXML == null) ? 0 : articleXML.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		result = prime * result
				+ ((megxBarJSON == null) ? 0 : megxBarJSON.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result + ((pmid == null) ? 0 : pmid.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		result = prime * result
				+ ((worldRegion == null) ? 0 : worldRegion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (articleXML == null) {
			if (other.articleXML != null)
				return false;
		} else if (!articleXML.equals(other.articleXML))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		if (megxBarJSON == null) {
			if (other.megxBarJSON != null)
				return false;
		} else if (!megxBarJSON.equals(other.megxBarJSON))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (pmid == null) {
			if (other.pmid != null)
				return false;
		} else if (!pmid.equals(other.pmid))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (worldRegion == null) {
			if (other.worldRegion != null)
				return false;
		} else if (!worldRegion.equals(other.worldRegion))
			return false;
		return true;
	}

}
