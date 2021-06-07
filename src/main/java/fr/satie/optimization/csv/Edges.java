package fr.satie.optimization.csv;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.data.EdgeLine;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * File <b>Edges</b> located on fr.satie.optimization.csv
 * Edges is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:18
 * @since 0.2
 */
public class Edges {

	private final List<EdgeLine> edges;

	public Edges(List<String[]> csv) {
		this.edges = new ArrayList<>();
		for (String[] strings : csv)
			if (strings.length >= 1 && !strings[0].startsWith("#"))
				edges.add(new EdgeLine(strings));
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug("Successfully load {} edges from file.", edges.size());
	}

	public Stream<EdgeLine> getNodes() {
		return edges.stream();
	}

}
