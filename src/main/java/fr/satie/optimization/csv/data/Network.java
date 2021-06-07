package fr.satie.optimization.csv.data;

import fr.satie.optimization.data.GraphNodal;

/**
 * File <b>Network</b> located on fr.satie.optimization.csv
 * Network is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 10:04
 * @since 0.1
 */
public class Network implements GraphNodal {

	private final int id, voltage;
	private final String  name;
	private final Country country;
	private final double  lat, lon;

	public Network(String[] data) {
		this.id = Integer.parseInt(data[0]);
		this.name = data[1];
		this.country = Country.getEnum(data[2]);
		this.voltage = Integer.parseInt(data[3]);
		this.lat = Double.parseDouble(data[4]);
		this.lon = Double.parseDouble(data[5]);
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

	public int getVoltage() {
		return voltage;
	}
}
