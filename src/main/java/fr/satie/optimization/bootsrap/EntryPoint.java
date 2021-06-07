package fr.satie.optimization.bootsrap;

import fr.satie.optimization.Optimizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File <b>EntryPoint</b> located on fr.satie.optimization.bootsrap
 * EntryPoint is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 08:23
 * @since 0.1
 */
public class EntryPoint {

	public static void main(String[] args) {
		final Map<String, List<String>> params = new HashMap<>();

		List<String> options = null;
		for (final String a : args) {
			if (a.charAt(0) == '-') {
				if (a.length() < 2) {
					System.err.println("Error at argument " + a);
					return;
				}
				options = new ArrayList<>();
				params.put(a.substring(1), options);
			} else if (options != null)
				options.add(a);
			else {
				System.err.println("Illegal parameter usage");
				return;
			}
		}
		final boolean gui        = params.containsKey("gui") && !params.get("gui").isEmpty() && Boolean.parseBoolean(params.get("gui").get(0));
		final boolean debug      = params.containsKey("debug") && !params.get("debug").isEmpty() && Boolean.parseBoolean(params.get("debug").get(0));
		final boolean generators = params.containsKey("generator") && !params.get("generator").isEmpty() && Boolean.parseBoolean(params.get("generator").get(0));
		String        date       = params.containsKey("date") && params.get("date").size() == 2 ? params.get("date").get(0) + " " + params.get("date").get(1) : "2012-01-01 00:00:00";
		int           worker     = params.containsKey("worker") && params.get("worker").size() == 1 ? Integer.parseInt(params.get("worker").get(0)) : 2;
		String        algo       = params.containsKey("algo") && params.get("algo").size() == 1 ? params.get("algo").get(0) : "scipy";
		if (!algo.equals("scipy") && !algo.equals("admm"))
			algo = "scipy";
		if (params.containsKey("date") && params.get("date").size() == 1)
			date = "all";
		new Optimizer(gui, debug, generators, date, algo, worker);
	}
}
