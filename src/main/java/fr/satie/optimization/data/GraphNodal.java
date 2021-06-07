package fr.satie.optimization.data;

import fr.satie.optimization.csv.data.Country;

/**
 * File <b>GraphNodal</b> located on fr.satie.optimization.data
 * GraphNodal is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 14:55
 * @since 0.2
 */
public interface GraphNodal {

	double getLat();

	double getLon();

	Country getCountry();

	String getName();

	int getId();

}
