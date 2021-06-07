package fr.satie.optimization.csv.data;

import java.util.Locale;

/**
 * File <b>Energy</b> located on fr.satie.optimization.csv.data
 * Energy is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 17:43
 * @since 0.2
 */
public enum Energy {

	BIOMASS,
	COAL,
	FUEL_OIL,
	GEOTHERMAL,
	HYDRO,
	LIGNITE,
	NATURAL_GAS,
	NUCLEAR,
	WASTE,
	UNKNOWN;

	public static Energy getEnum(String name) {
		for (Energy e : values()) {
			if (e.name().replace('_', ' ').toLowerCase(Locale.ROOT).equalsIgnoreCase(name.toLowerCase(Locale.ROOT)))
				return e;
		}
		throw new IllegalArgumentException("Given energy is not registered in enum : " + name);
	}

}
