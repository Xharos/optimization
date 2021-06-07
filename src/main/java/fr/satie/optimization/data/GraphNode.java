package fr.satie.optimization.data;

import java.util.stream.Stream;

/**
 * File <b>GraphNode</b> located on fr.satie.optimization.data
 * GraphNode is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 14:55
 * @since 0.2
 */
public interface GraphNode {

	GraphNodal getNode(int id);

	Stream<GraphNodal> getNodes();

}
