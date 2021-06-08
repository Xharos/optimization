package fr.satie.optimization.data;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.data.Country;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * File <b>LiteDatabase</b> located on fr.satie.optimization.data
 * LiteDatabase is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 07/06/2021 at 14:10
 * @since 0.3
 */
public class LiteDatabase {

	private final String     dbName;
	private final String     countryValues;
	private       Connection connection;

	public LiteDatabase(String dbName) {
		this.dbName = dbName;
		List<String>  countries = Arrays.stream(Country.values()).map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
		StringBuilder builder   = new StringBuilder();
		for (String country : countries) {
			if (!country.equals("kos"))
				builder.append(country).append(", ");
		}
		String countryStatement = builder.toString();
		this.countryValues = countryStatement.substring(0, countryStatement.length() - 2);
	}

	public void connect() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:sqlite/europe-power.db");
			DatabaseMetaData meta = connection.getMetaData();
			Optimizer.getLogger().info("Create {} sqlite database", dbName);
			Optimizer.getLogger().trace("The driver name is " + meta.getDriverName());
		} catch (SQLException e) {
			Optimizer.getLogger().error("SQLite connection failed...");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void post(String result, String algo, String date) {
		StringBuilder values = new StringBuilder();
		values.append("'").append(date).append("'").append(", ");
		String[] parts = result.split("\n");
		for (String part : parts) {
			values.append((int) Double.parseDouble(part)).append(", ");
		}
		try {
			String    query     = values.toString();
			Statement statement = connection.createStatement();
			statement.executeUpdate("REPLACE into " + algo + " (id, " + countryValues + ") values(" + query.substring(0, query.length() - 2) + ");");
		} catch (Exception e) {
			Optimizer.getLogger().warn("Cannot post result to sqlite database...");
			e.printStackTrace();
		}
	}

	public void setup(String algo) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs        = statement.executeQuery("select fra from " + algo + " where id='2012-01-01-00-00-00';");
			if (!rs.next()) {
				create(algo);
			}
		} catch (Exception e) {
			Optimizer.getLogger().error("SQLite init statement failed...");
			if (e.getMessage().contains("missing database"))
				create(algo);
			else {
				e.printStackTrace();
				System.exit(1);
			}
		} /*finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				Optimizer.getLogger().error("SQLite connection close failed...");
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}*/
	}

	private void create(String algo) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("drop table if exists " + algo);
			List<String>  countries = Arrays.stream(Country.values()).map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
			StringBuilder builder   = new StringBuilder();
			for (String country : countries) {
				if (!country.equals("kos"))
					builder.append(country).append(" ").append("integer, ");
			}
			String countryStatement = builder.toString();
			String st               = countryStatement.substring(0, countryStatement.length() - 2);
			Optimizer.getLogger().debug(st);
			statement.executeUpdate("create table " + algo + " (id string PRIMARY KEY, " + st + ")");
		} catch (Exception e) {
			Optimizer.getLogger().error("SQLite init statement failed...");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
