package main.firefighters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.exceptions.NoFireFoundException;
import main.api.exceptions.NoFirefightersAvailableException;
import main.api.exceptions.OutOfCityBoundsException;

public class FireDispatchImpl implements FireDispatch {
	private List<Firefighter> firefighters;
	private City city;

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
	public void dispatchFirefighters(CityNode... burningBuildings)
			throws NoFireFoundException, OutOfCityBoundsException, NoFirefightersAvailableException {
		if (this.firefighters.isEmpty()) {
			throw new NoFirefightersAvailableException();
		}

		Collection<CityNode> fireNodes = filterFireNodes(burningBuildings);

		for (CityNode fireNode : fireNodes) {
			Firefighter closestFirefighter = findClosestFirefighter(fireNode);

			closestFirefighter.addDistanceTraveled(getDistance(closestFirefighter.getLocation(), fireNode));
			closestFirefighter.setLocation(fireNode);

			city.getBuilding(fireNode).extinguishFire();
		}
	}

	// avoid throwing exception if list contains node(s) not on fire, filter instead
	private Collection<CityNode> filterFireNodes(CityNode[] burningBuildings) {
		Collection<CityNode> fireNodes = new ArrayList<>();
		fireNodes.addAll(Arrays.asList(burningBuildings));
		fireNodes = fireNodes.stream().filter(fireNode -> city.getBuilding(fireNode).isBurning())
				.collect(Collectors.toList());
		return fireNodes;
	}

	private Firefighter findClosestFirefighter(CityNode burningBuilding) throws NoFireFoundException {
		// default to first firefighter in list
		Firefighter closestFirefighter = this.firefighters.get(0);
		int shortestDistance = getDistance(burningBuilding, closestFirefighter.getLocation());

		for (Firefighter firefighter : this.firefighters) {
			int distance = getDistance(burningBuilding, firefighter.getLocation());
			shortestDistance = Math.min(shortestDistance, distance);
			closestFirefighter = (distance == shortestDistance ? firefighter : closestFirefighter);
		}

		return closestFirefighter;
	}

	private int getDistance(CityNode destination, CityNode currentLocation) {
		return Math.abs(destination.getX() - currentLocation.getX())
				+ Math.abs(destination.getY() - currentLocation.getY());
	}
}
