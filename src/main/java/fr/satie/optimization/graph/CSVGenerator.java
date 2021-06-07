package fr.satie.optimization.graph;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.DataManager;
import fr.satie.optimization.csv.data.Country;
import fr.satie.optimization.data.GraphNodal;
import fr.satie.optimization.graph.utils.ColorGradient;
import java.awt.Color;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.Generator;

/**
 * File <b>CSVGenerator</b> located on fr.satie.optimization.graph
 * CSVGenerator is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 15:26
 * @since 0.1
 */
public class CSVGenerator extends BaseGenerator implements Generator {

	private final DataManager manager;
	private final Color[]     gradient;
	private final boolean     generator;

	public CSVGenerator(DataManager manager, boolean generator) {
		this.gradient = ColorGradient.generateGradient(Country.values().length);
		this.generator = generator;
		this.manager = manager;
	}

	@Override
	public void begin() {
		manager.getNetworks().getNodes().forEach(this::addNode);
		if (generator)
			manager.getGenerators().getNodes().forEach(this::addNode);
	}

	@Override
	public boolean nextEvents() {
		return false;
	}

	@Override
	public void end() {

	}

	protected void addNode(GraphNodal gen) {
		String id = Integer.toString(gen.getId());
		addNode(id, gen.getLon(), gen.getLat());
		Color c = gradient[gen.getCountry().ordinal()];
		sendNodeAttributeAdded(sourceId, id, "ui.style", "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");");
		if (gen instanceof fr.satie.optimization.csv.data.Generator)
			sendNodeAttributeAdded(sourceId, id, "ui.style", "size: 3px, 3px; " +
					"shape: box; " +
					"fill-color: gray;");
	}
}
