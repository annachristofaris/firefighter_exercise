package main.firefighters;

import main.api.Building;
import main.api.CityNode;
import main.api.Firefighter;

public class FirefighterImpl implements Firefighter {
	private CityNode location;
	private int distanceTraveled;

	public FirefighterImpl(Building fireStation) {
		this.location = fireStation.getLocation();
		this.distanceTraveled = 0;
	}

	@Override
	public CityNode getLocation() {
		return location;
	}

	@Override
	public int distanceTraveled() {
		return distanceTraveled;
	}

	@Override
	public void setLocation(CityNode destination) {
		this.location = destination;
	}
	
	public void addDistanceTraveled(int distance) {
		this.distanceTraveled += distance;
	}
	
}
