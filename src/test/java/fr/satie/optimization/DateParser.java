package fr.satie.optimization;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * File <b>DateParser</b> located on fr.satie.optimization
 * DateParser is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 18/05/2021 at 09:00
 * @since 0.2
 */
public class DateParser {

	@Test
	public void parse() throws ParseException {
		SimpleDateFormat formatter           = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		String           dateFromFile        = "2012-01-01 00:00:00";
		Date             date                = formatter.parse(dateFromFile);
		String           formattedDateString = formatter.format(date);
		Assertions.assertEquals(formattedDateString, dateFromFile);
	}

}
