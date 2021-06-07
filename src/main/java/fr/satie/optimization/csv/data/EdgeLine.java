package fr.satie.optimization.csv.data;

/**
 * File <b>EdgeLine</b> located on fr.satie.optimization.csv.data
 * EdgeLine is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:20
 * @since 0.2
 */
public class EdgeLine {

	private final int from, to;
	private final double reactance, admittance, limit, length;

	public EdgeLine(String[] datas) {
		this.from = Integer.parseInt(datas[0]);
		this.to = Integer.parseInt(datas[1]);
		this.reactance = Double.parseDouble(datas[2]);
		this.admittance = Double.parseDouble(datas[3]);
		this.limit = Double.parseDouble(datas[4]);
		this.length = Double.parseDouble(datas[5]);
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public String toString() {
		return from + "-" + to;
	}
}
