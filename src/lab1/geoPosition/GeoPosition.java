package lab1.geoPosition;

public class GeoPosition {

	private double latitude;
	private double longitude;

	public GeoPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;

	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public String toString() {
		return "(" + this.latitude + ", " + this.longitude + ")";
	}

	public boolean isNorthernHemisphere() {
		if (this.latitude >= 0)
			return true;
		else
			return false;

	}

	public boolean isSouthernHemisphere() {
		if (this.latitude < 0)
			return true;
		else
			return false;
	}

	private static double degToRad(double deg) {
		return deg * Math.PI / 180;
	}

	public static double localDistanceInKm(GeoPosition haw, GeoPosition mainStation) {
		double dY = 111.3 * Math.abs(haw.latitude - mainStation.latitude);
		double dX = 111.3 * Math.cos(degToRad((haw.latitude + mainStation.latitude)) / 2)
				* Math.abs(haw.longitude - mainStation.longitude);
		return Math.sqrt(dX * dX + dY * dY);
	}

	public double distanceInKm(GeoPosition mainStation) {
		return 6378.388 * Math.acos(Math.sin(degToRad(this.latitude)) * Math.sin(degToRad(mainStation.latitude))
				+ Math.cos(degToRad(this.latitude)) * Math.cos(degToRad(mainStation.latitude))
						* Math.cos(degToRad(mainStation.longitude - this.longitude)));
	}

	public static double distanceInKm(GeoPosition haw, GeoPosition mainStation) {
		return 6378.388 * Math.acos(Math.sin(degToRad(haw.latitude)) * Math.sin(degToRad(mainStation.latitude))
				+ Math.cos(degToRad(haw.latitude)) * Math.cos(degToRad(mainStation.latitude))
						* Math.cos(degToRad(mainStation.longitude - haw.longitude)));
	}

}
