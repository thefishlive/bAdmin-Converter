package io.github.thefishlive.badmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting bAdmin converter version " + Main.class.getPackage().getImplementationVersion());
		
		OptionParser parser = new OptionParser();
		OptionSpec<?> helpOption = parser.accepts("help", "Show help");
		OptionSpec<String> hostOption = parser.accepts("host", "sets the mysql host").withRequiredArg().ofType(String.class).defaultsTo("localhost");
		OptionSpec<String> dbOption = parser.accepts("database", "sets the mysql database").withRequiredArg().ofType(String.class).defaultsTo("minecraft");
		OptionSpec<Integer> portOption = parser.accepts("port", "sets the mysql port").withRequiredArg().ofType(Integer.class).defaultsTo(3306);
		OptionSpec<String> userOption = parser.accepts("user", "sets the mysql user").withRequiredArg().ofType(String.class).defaultsTo("root");
		OptionSpec<String> passOption = parser.accepts("pass", "sets the mysql password").withRequiredArg().ofType(String.class).defaultsTo("");
		
		OptionSet options = parser.parse(args);
		
		if (options.has(helpOption)) {
			try {
				parser.printHelpOn(System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		String host = hostOption.value(options);
		String database = dbOption.value(options);
		int port = portOption.value(options);
		String user = userOption.value(options);
		String pass = passOption.value(options);
		
		convertDatabase("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
	}
	
	private static void convertDatabase(String database, String user, String password) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find mysql jdbc driver.");
			return;
		}
		
		Connection conn;
		int count = 0;
		int failed = 0;
		
		try {
			conn = DriverManager.getConnection(database, user, password);
			
			PreparedStatement query = conn.prepareStatement("SELECT count(`id`) FROM `figadmin`");
			ResultSet set = query.executeQuery();

			if (!set.next()) {
				System.err.println("A unknown error has occurred");
				return;
			}
			
			System.out.println("Converting " + set.getInt(1) + " entries to new ban format");
			
			query = conn.prepareStatement("SELECT * FROM `figadmin`");
			set = query.executeQuery();

			while(set.next()) {
				DataEntry entry = new DataEntry(set.getString(1), set.getString(2), set.getString(3), set.getLong(4), set.getLong(5), set.getInt(6));
				
				PreparedStatement statement = conn.prepareStatement("INSERT INTO `badmin_data` VALUES (?,?,?,?,?);");
				
				ConvertionWorker worker = new ConvertionWorker(entry);
				
				if (!worker.run(statement)) {
					failed++;
					continue;
				}
				
				try {
					statement.execute();
				} catch (SQLException ex) {
					System.err.println("Error executing query...");
					System.err.println(statement.toString());
					ex.printStackTrace(System.err);
				}
				count++;
			}
			
			System.out.println("Converted " + count + " ban entries to new format");
			System.err.println("Failed to convert " + failed + " ban entries");
		
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
