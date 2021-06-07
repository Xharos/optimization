package fr.satie.optimization.graph.utils;

import java.awt.Color;

/**
 * File <b>ColorGradient</b> located on fr.satie.optimization.graph.utils
 * ColorGradient is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:42
 * @since 0.2
 */
public class ColorGradient {

	public static Color[] generateGradient(int element) {
		double  jump   = 1.0 / (element * 1.0);
		Color[] colors = new Color[element];
		for (int i = 0; i < colors.length; i++)
			colors[i] = new Color(Color.HSBtoRGB((float) (jump * i), 1.0f, 1.0f));
		return colors;
	}

}
