package fr.satie.optimization.csv;

/**
 * File <b>CSVType</b> located on fr.satie.optimization.csv
 * CSVType is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 14:59
 * @since 0.2
 */
public enum CSVType {

	GENERATORS("generator_info"),
	NETWORKS("network_nodes"),
	LOAD("load_signal"),
	NETWORKS_EDGES("network_edges");

	private final String file;

	CSVType(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return file + ".csv";
	}
}
