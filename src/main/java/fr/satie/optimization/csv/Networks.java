package fr.satie.optimization.csv;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.data.Network;
import fr.satie.optimization.data.GraphNodal;
import fr.satie.optimization.data.GraphNode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * File <b>Networks</b> located on fr.satie.optimization.csv
 * Networks is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 10:04
 * @since 0.1
 */
public class Networks implements GraphNode {

	private final List<GraphNodal> networks;

	public Networks(List<String[]> csv) {
		this.networks = new ArrayList<>();
		for (String[] strings : csv)
			if (strings.length >= 1 && !strings[0].startsWith("#"))
				networks.add(new Network(strings));
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug("Successfully load {} buses from file.", networks.size());
	}

	public GraphNodal getNode(int buseId) {
		return networks.stream().filter(net -> net.getId() == buseId).findFirst().orElse(null);
	}

	public Stream<GraphNodal> getNodes() {
		return networks.stream();
	}
}
