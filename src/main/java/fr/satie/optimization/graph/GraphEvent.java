package fr.satie.optimization.graph;

import fr.satie.optimization.Optimizer;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.ViewerListener;

/**
 * File <b>GraphEvent</b> located on fr.satie.optimization.graph
 * GraphEvent is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:29
 * @since 0.2
 */
public class GraphEvent implements ViewerListener {

	public volatile boolean loop = true;
	private final Graph graph;

	public GraphEvent(Graph graph) {
		this.graph = graph;
	}

	@Override
	public void viewClosed(String viewName) {
		this.loop = false;
	}

	@Override
	public void buttonPushed(String id) {
		String label = (String) graph.getNode(id).getAttribute("label");
		graph.getNode(id).setAttribute("label", label == null ? Optimizer.getInstance().getManager().getNode(Integer.parseInt(id)).getName() : null);
	}

	@Override
	public void buttonReleased(String id) {
	}

	@Override
	public void mouseOver(String id) {

	}

	@Override
	public void mouseLeft(String id) {

	}
}
