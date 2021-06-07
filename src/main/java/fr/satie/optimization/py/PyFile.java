package fr.satie.optimization.py;

/**
 * File <b>PyFile</b> located on fr.satie.optimization.py
 * PyFile is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 19/05/2021 at 10:02
 * @since 0.2
 */
public enum PyFile {

	NP_POLY_INTERP1D("np_poly_interp1D"),
	SCIPY_OPTIMIZE_MARKET("sc_min_poly_simple_market"),
	ADMM_MARKET("admm_poly_decentralized_market");

	private final String fileName;

	PyFile(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
