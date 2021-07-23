package lab2.geoPosition;

import java.util.ArrayList;

import lab1.geoPosition.GeoPosition;


public class GeoRoute implements Distance, Comparable<GeoRoute> {
	
	private String name;
	private ArrayList<GeoPosition> waypoints;
	
	public GeoRoute(String name) {
		this.name = name;
		this.waypoints = new ArrayList<GeoPosition>();
	}
	
	public GeoRoute(String name, ArrayList<GeoPosition> waypoints) {
		this.name = name;
		this.waypoints = waypoints;
	}
	
	public String toString() {
		return String.format("%s (%.1f km)", this.getName(), getDistance());
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;		
	}

	public void addWaypoint(GeoPosition myGeoPosition) {
		this.waypoints.add(myGeoPosition);		
	}

	public void removeWaypoint(int i) {
		if (i < this.getNumberWaypoints())
			this.waypoints.remove(i);
	}

	public int getNumberWaypoints() {
		return this.waypoints.size();
	}

	public GeoPosition getWaypoint(int i) {
		if (i < this.getNumberWaypoints())
			return this.waypoints.get(i);
		return null;
	}

	public GeoPosition[] getWaypoints() {
		return this.waypoints.toArray(
				new GeoPosition[this.getNumberWaypoints()]);
	}

	@Override
	public int compareTo(GeoRoute otherGeoRoute) {
		if (this.getDistance() < otherGeoRoute.getDistance())
			return -1;
		else if (this.getDistance() > otherGeoRoute.getDistance())
			return 1;
		else
			return 0;
	}

	@Override
	public double getDistance() {
		double distance = 0.0;
		for (int i = 0; i < this.getNumberWaypoints() - 1; i++) {
			distance += GeoPosition.distanceInKm(this.getWaypoint(i), this.getWaypoint(i+1));
		}
		return distance;
	}

}
