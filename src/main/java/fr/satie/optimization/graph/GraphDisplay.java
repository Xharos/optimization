package fr.satie.optimization.graph;

import fr.satie.optimization.Optimizer;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

/**
 * File <b>GraphDisplay</b> located on fr.satie.optimization.graph
 * GraphDisplay is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:29
 * @since 0.2
 */
public class GraphDisplay {

	private final Graph      graph;
	private final Generator  gen;
	private final GraphEvent event;

	public GraphDisplay(boolean generator) {
		this.graph = new SingleGraph("Europe power flow");
		this.gen = new CSVGenerator(Optimizer.getInstance().getManager(), generator);
		this.event = new GraphEvent(graph);
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		gen.addSink(graph);
		gen.begin();
		Optimizer.getInstance().linkGraph(graph);
		Optimizer.getInstance().getManager().getEdges().getNodes().forEach(line -> {
			graph.addEdge(line.toString(), Integer.toString(line.getFrom()), Integer.toString(line.getTo()));
		});
		Viewer viewer = graph.display(false);
		View   view   = viewer.getDefaultView();
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(event);
		fromViewer.addSink(graph);
		while (event.loop)
			fromViewer.pump();
		System.exit(0);
	}
}
