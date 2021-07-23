package lab2.geoPosition;

public class GeoTrack extends GeoRoute{
	
	private String date;

	public GeoTrack(String name, String date) {
		super(name);
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
