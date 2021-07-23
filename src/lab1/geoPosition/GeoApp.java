package lab1.geoPosition;

public class GeoApp {

	public static void main(String[] args) {

		GeoPosition haw = new GeoPosition(53.557078, 10.023109);
		GeoPosition eiffelTower = new GeoPosition(48.858363, 2.294481);
		GeoPosition palmaDeMallorca = new GeoPosition(39.562553, 2.661947);
		GeoPosition lasVegas = new GeoPosition(36.156214, -115.148736);
		GeoPosition copacabana = new GeoPosition(-22.971177, -43.182543);
		GeoPosition waikikiBeach = new GeoPosition(21.281004, -157.837456);
		GeoPosition surfersParadise = new GeoPosition(-28.002695, 153.431781);
		GeoPosition northPole = new GeoPosition(90.0, 10.023109);
		GeoPosition equator = new GeoPosition(0.0, 10.023109);
		GeoPosition southPole = new GeoPosition(-90.0, 10.023109);

		final String[] places = { "HAW", "Eiffel Tower", "Palma de Mallorca", "Las Vegas", "Copacabana",
				"Waikiki Beach", "Surfers Paradise", "North pole", "equator", "South pole" };
		final GeoPosition[] geoPositions = { haw, eiffelTower, palmaDeMallorca, lasVegas, copacabana, waikikiBeach,
				surfersParadise, northPole, equator, southPole };

		System.out.println(
				"Place             | Lattitude | Longitude | Distance km(exact) | Distance km(local) | Error %");
		System.out.println(
				"--------------------------------------------------------------------------------------------------");

		for (int i = 0; i < 10; i++) {

			double error;

			if (i == 7)
				System.out.println(
						"--------------------------------------------------------------------------------------------------");

			if (i == 0)
				System.out.printf("%17s | %9.2f | %9.2f | %18s | %18s | %9s\n", places[i],
						geoPositions[i].getLatitude(), geoPositions[i].getLongitude(), '-', '-', '-');
			else {
				error = Math
						.abs(geoPositions[i].distanceInKm(haw) - GeoPosition.localDistanceInKm(haw, geoPositions[i]))
						/ geoPositions[i].distanceInKm(haw) * 100;
				System.out.printf("%17s | %9.2f | %9.2f | %18.2f | %18.2f | %9.2f %%\n", places[i],
						geoPositions[i].getLatitude(), geoPositions[i].getLongitude(),
						geoPositions[i].distanceInKm(haw), GeoPosition.localDistanceInKm(haw, geoPositions[i]), error);
			}
		}
	}
}
