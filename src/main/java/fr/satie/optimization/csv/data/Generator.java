package fr.satie.optimization.csv.data;

import fr.satie.optimization.data.GraphNodal;

/**
 * File <b>Generator</b> located on fr.satie.optimization.csv
 * Generator is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 14:58
 * @since 0.1
 */
public class Generator implements GraphNodal {

	private final int id, origin;
	private final String name, status;
	private final Energy primary, secondary;
	private final Country country;
	private final double  lat, lon, capacity, lincost, cyclecost, minuptime, mindowntime, minonlinecapacity;

	public Generator(String[] data) {
		this.id = Integer.parseInt(data[0]);
		this.name = data[1];
		this.country = Country.getEnum(data[2]);
		this.origin = Integer.parseInt(data[3]);
		this.lat = Double.parseDouble(data[4]);
		this.lon = Double.parseDouble(data[5]);
		this.status = data[6];
		this.primary = Energy.getEnum(data[7]);
		this.secondary = Energy.getEnum(data[8]);
		this.capacity = Double.parseDouble(data[9]);
		this.lincost = Double.parseDouble(data[10]);
		this.cyclecost = Double.parseDouble(data[11]);
		this.minuptime = Double.parseDouble(data[12]);
		this.mindowntime = Double.parseDouble(data[13]);
		this.minonlinecapacity = Double.parseDouble(data[14]);
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLon() {
		return lon;
	}

	@Override
	public Country getCountry() {
		return country;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return id;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getCyclecost() {
		return cyclecost;
	}

	public double getLincost() {
		return lincost;
	}
}
