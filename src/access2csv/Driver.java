package access2csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import com.healthmarketscience.jackcess.*;
import org.apache.commons.cli.*;

public class Driver
{
	static int export(Database db, String tableName, Writer csv) throws IOException
	{
		Table table = db.getTable(tableName);
		String[] buffer = new String[table.getColumnCount()];
		CSVWriter writer = new CSVWriter(new BufferedWriter(csv));		
		int rows = 0;
		try
		{
			for(Row row : table)
			{
				int i = 0;
				for (Object object : row.values())
				{
					buffer[i++] = object == null ? null : object.toString();
				}
				writer.writeNext(buffer);
				rows++;
			}
		}
		finally
		{			
			writer.close();
		}
		return rows;
	}

	static void schema(Database db) throws IOException
	{
		try
		{
			for(String tableName : db.getTableNames())
			{
				Table table = db.getTable(tableName);
				System.out.println(String.format("CREATE TABLE %s (", tableName));
				for(Column col : table.getColumns())
				{
					System.out.println(String.format("  %s %s,", 
							col.getName(), col.getType()));
				}
				System.out.println(")");
			}
		}
		finally
		{
			db.close();
		}

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		String header="Application to read contents from a MS Access file";
		String footer="";
		Options options = new Options();
		options.addOption(new Option("h",//Short option
				"help", //Long option
				false, //No extra argumets
				"Show help"
				));
		options.addOption(new Option("s",//Short option
				"schema", //Long option
				false, //No extra argumets
				"Create schema definitions"
				));
		options.addOption(new Option("i",//Short option
				"db", //Long option
				true, //Extra argumets
				"Access DB to open"
				));
		options.addOption(new Option("t",//Short option
				"tables", //Long option
				false, //Extra argumets
				"Tables to extract"
				));
		options.addOption(new Option("p",//Short option
				"pass", //Long option
				true, //Extra argumets
				"Password to open file"
				));
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd=null;
		try
		{
			cmd = parser.parse(options, args);
		}
		catch(ParseException e)
		{
			System.err.println("Error parsing arguments");
			HelpFormatter formatter=new HelpFormatter();
			formatter.printHelp("access2csv", header, options, footer, true);
			System.exit(0);
		}

		if(cmd.hasOption("h")||cmd.hasOption("help"))
		{
			HelpFormatter formatter=new HelpFormatter();
			formatter.printHelp("access2csv", header, options, footer, true);
			System.exit(0);
		}
		String dbFile=null;
		if(cmd.hasOption("i"))
			dbFile=cmd.getOptionValue("i");
		else if(cmd.hasOption("db"))
			dbFile=cmd.getOptionValue("db");
		else
		{
			System.out.println("Error, no Access DB supplied");
			HelpFormatter formatter=new HelpFormatter();
			formatter.printHelp("access2csv", header, options, footer, true);
			System.exit(-1);
		}
		boolean exportSchema=cmd.hasOption("s")||cmd.hasOption("schema");
		String password=null;
		if(cmd.hasOption("p"))
			password=cmd.getOptionValue("p");
		else if(cmd.hasOption("pass"))
			password=cmd.getOptionValue("pass");
		boolean exportTables=false;

		if(cmd.hasOption("t")||cmd.hasOption("tables"))
			exportTables=true;

		Database db = null;
		try
		{
			if(password==null)
				db=new DatabaseBuilder(new File(dbFile)).setCodecProvider(new CryptCodecProvider()).open();
			else
				db=new DatabaseBuilder(new File(dbFile)).setCodecProvider(new CryptCodecProvider(password)).open();

			if(exportSchema)
				schema(db);
			if(exportTables)
			{
				PrintWriter writer=new PrintWriter(System.out);
				for(int i=0;i<cmd.getArgs().length;i++)
					export(db, cmd.getArgs()[i], writer);
			}
		}
		finally
		{
			if(db!=null)
				try
				{
					db.close();
				}
				catch(Exception e) {}
		}
	}
}
