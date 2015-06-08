package lpool.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	public static final String default_pathname = "./data/events.log";
	public static final String datetime_header = "Timestamp";
	public static final String event_header = "Event";
	public static final String separator = " || ";
	private static final String initialized_event = "Log initialized";
	
	private String logFileName;
	private Boolean validFile = true;
	private File logfile;
	
	public Logger(String filename) {
		logFileName = filename;
		validFile = initialize();
		if(!validFile) {
			System.err.println("Unable to create log file");
		}
	}
	
	public Logger() {
		logFileName = default_pathname;
		validFile = initialize();
		if(!validFile) {
			System.err.println("Unable to create log file");
		}
	}

	public Boolean initialize() {
		
		logfile = new File(logFileName);
		
		if(!logfile.exists()) {
			try {
				if(!logfile.createNewFile())
					return false;
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
			
			if(!insertHeader(logfile))
				return false;
		}
		
		return true;
	}
	
	private Boolean insertHeader(File logfile) {

		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfile, true)));
		    out.println("Log format: " + datetime_header + separator + event_header);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		log(initialized_event);
		
		return true;
	}
	
	public void log(String event) {
		if(!validFile)
			return;
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfile, true)));
		    String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date());
		    out.println(date + separator + event);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
}
