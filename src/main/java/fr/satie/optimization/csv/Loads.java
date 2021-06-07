package fr.satie.optimization.csv;

import fr.satie.optimization.Optimizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * File <b>Loads</b> located on fr.satie.optimization.csv
 * Loads is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 18/05/2021 at 08:56
 * @since 0.2
 */
public class Loads {

	private final Map<String, double[]> loads;
	private final List<Integer>         busesID; //intlist

	public Loads(List<String[]> csv) {
		this.busesID = new LinkedList<>();
		this.loads = new ConcurrentHashMap<>();
		String[] buses = csv.get(0);
		for (int i = 1; i < buses.length; i++)
			busesID.add(Integer.parseInt(buses[i]));
		csv.parallelStream().forEach(strings -> {
			if (!strings[0].startsWith("#")) {
				double[] a = new double[busesID.size()];
				for (int i = 1; i < strings.length; i++)
					a[i - 1] = Double.parseDouble(strings[i]);
				loads.put(strings[0], a);
			}
		});
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug("Successfully load {} loads from file.", loads.keySet().size());
	}

	public double getData(String dateToLoad, int busId) {
		try {
			return loads.get(dateToLoad)[busesID.indexOf(busId)];
		} catch (Exception e) {
			e.printStackTrace();
			Optimizer.getLogger().error("Invalid date format, see README.md for usage : {}", dateToLoad);
			System.exit(1);
			return 0;
		}
	}

	public Set<String> getDates() {
		return loads.keySet();
	}

	public Stream<double[]> getNodes() {
		return loads.values().stream();
	}

	public int getSize() {
		return loads.size();
	}

}
