package main.firefighters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.exceptions.NoFireFoundException;
import main.api.exceptions.OutOfCityBoundsException;

public class FireDispatchImpl implements FireDispatch {
	private List<Firefighter> firefighters;
	private City city;
	private static Logger logger;

	public FireDispatchImpl(City city) {
		this.firefighters = new ArrayList<>();
		this.city = city;
	}

	@Override
	public void setFirefighters(int numFirefighters) {
		for (int i = 0; i < numFirefighters; i++) {
			Firefighter fireFighter = new FirefighterImpl(city.getFireStation());
			firefighters.add(fireFighter);
		}
	}

	@Override
	public List<Firefighter> getFirefighters() {
		return this.firefighters;
	}

	@Override
	public void dispatchFirefighers(CityNode... burningBuildings) {
		if (this.firefighters.isEmpty()) {
			logger.warning("No firefighters available!");
			return;
		}
		// default to first firefighter in list
		Firefighter closestFirefighter = this.firefighters.get(0);

		for (CityNode burningBuilding : burningBuildings) {
			int shortestDistance = getDistance(burningBuilding, closestFirefighter.getLocation());

			for (Firefighter firefighter : this.firefighters) {

				int distance = getDistance(burningBuilding, firefighter.getLocation());
				shortestDistance = Math.min(shortestDistance, distance);
				closestFirefighter = distance <= shortestDistance ? firefighter : closestFirefighter;
			}

			closestFirefighter.setLocation(burningBuilding);
			closestFirefighter.addDistanceTraveled(shortestDistance);

			try {
				city.getBuilding(burningBuilding).extinguishFire();
			} catch (OutOfCityBoundsException e) {
				e.printStackTrace();
			} catch (NoFireFoundException e) {
				e.printStackTrace();
				logger.warning("No fire is found at this location.");
			}
		}
	}

	private int getDistance(CityNode destination, CityNode currentLocation) {
		return Math.abs(destination.getX() - currentLocation.getX())
				+ Math.abs(destination.getY() - currentLocation.getY());
	}
}
