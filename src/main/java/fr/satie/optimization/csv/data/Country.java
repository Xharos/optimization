package fr.satie.optimization.csv.data;

import java.util.Locale;

/**
 * File <b>Country</b> located on fr.satie.optimization.csv.data
 * Country is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 15:01
 * @since 0.1
 */
public enum Country {

	FRANCE("FRA"),
	SLOVAKIA("SVK"),
	PORTUGAL("POR"),
	GERMANY("DEU"),
	BELGIUM("BEL"),
	CZECH_REPUBLIC("CZE"),
	HUNGARY("HUN"),
	SWITZERLAND("CHE"),
	NETHERLANDS("NLD"),
	POLAND("POL"),
	GREECE("GRC"),
	BULGARIA("BGR"),
	SLOVENIA("SVN"),
	ITALY("ITA"),
	MONTENEGRO("MNE"),
	ALBANIA("ALB"),
	KOSOVO("KOS"),
	REPUBLIC_OF_MACEDONIA("MKD"),
	ROMANIA("ROU"),
	SPAIN("ESP"),
	SERBIA("SRB"),
	DENMARK("DNK"),
	BOSNIA_AND_HERZEGOVINA("BIH"),
	AUSTRIA("AUT"),
	CROATIA("HRV"),
	LUXEMBOURG("LUX");

	private final String name;

	Country(String name) {
		this.name = name;
	}

	public static Country getEnum(String name) {
		for (Country v : values()) {
			if (v.name().replace('_', ' ').toLowerCase(Locale.ROOT).equalsIgnoreCase(name.toLowerCase(Locale.ROOT)))
				return v;
			if (v.getName().equalsIgnoreCase(name))
				return v;
		}
		throw new IllegalArgumentException("Given country is not registered in enum : " + name);
	}

	public String getName() {
		return name;
	}
}
