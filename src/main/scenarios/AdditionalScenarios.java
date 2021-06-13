package main.scenarios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.Pyromaniac;
import main.api.exceptions.FireproofBuildingException;
import main.api.exceptions.NoFireFoundException;
import main.api.exceptions.NoFirefightersAvailableException;
import main.api.exceptions.OutOfCityBoundsException;
import main.impls.CityImpl;

public class AdditionalScenarios {

	private City basicCity;
	private FireDispatch fireDispatch;
	private List<Firefighter> firefighters;

	@Before
	public void init() throws FireproofBuildingException {
		basicCity = new CityImpl(10, 10, new CityNode(1, 1));
		fireDispatch = this.basicCity.getFireDispatch();
	}

	@Test
	public void tripleFire() throws FireproofBuildingException, NoFireFoundException, OutOfCityBoundsException,
			NoFirefightersAvailableException {
		CityNode[] fireNodes = { new CityNode(8, 4), new CityNode(2, 8), new CityNode(6, 3) };
		Pyromaniac.setFires(this.basicCity, fireNodes);

		fireDispatch.setFirefighters(3);
		firefighters = fireDispatch.getFirefighters();
		firefighters.get(1).setLocation(new CityNode(6, 8));
		firefighters.get(2).setLocation(new CityNode(9, 0));
		fireDispatch.dispatchFirefighters(fireNodes);

		// expect first firefighter to still be at fire station
		assertEquals(firefighters.get(0).getLocation(), basicCity.getFireStation().getLocation());
		assertEquals(firefighters.get(0).distanceTraveled(), 0);
		assertEquals(firefighters.get(1).distanceTraveled(), 4);
		assertEquals(firefighters.get(2).distanceTraveled(), 8);
		for (CityNode fireNode : fireNodes) {
			assertFalse(basicCity.getBuilding(fireNode).isBurning());
		}
	}

	@Test
	public void nodeListOnlyContainsFireNodes() throws FireproofBuildingException, OutOfCityBoundsException,
			NoFireFoundException, NoFirefightersAvailableException {
		CityNode houseOnFire = new CityNode(1, 0);
		CityNode apartmentOnFire = new CityNode(2, 0);
		CityNode dunderMifflin = new CityNode(9, 9);

		Pyromaniac.setFire(basicCity, houseOnFire);
		Pyromaniac.setFire(basicCity, apartmentOnFire);

		CityNode[] nodes = { houseOnFire, apartmentOnFire, dunderMifflin };

		fireDispatch.setFirefighters(1);
		fireDispatch.dispatchFirefighters(nodes);
		Firefighter firefighter = fireDispatch.getFirefighters().get(0);

		// confirm firefighter never went to dunderMifflin
		assertEquals(firefighter.getLocation(), apartmentOnFire);
		assertEquals(firefighter.distanceTraveled(), 2);
	}

	@Test
	public void noFirefightersAvailableException() {
		fireDispatch.getFirefighters().clear();
		Assert.assertThrows(NoFirefightersAvailableException.class, () -> {
			fireDispatch.dispatchFirefighters(new CityNode(4, 4));
		});
	}
}
