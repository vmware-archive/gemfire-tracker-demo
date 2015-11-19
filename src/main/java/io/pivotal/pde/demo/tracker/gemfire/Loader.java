package io.pivotal.pde.demo.tracker.gemfire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gemstone.gemfire.cache.Region;

public class Loader {
	public static void main(String []args){
		ClassPathXmlApplicationContext ctx = null;
		try {
			ctx = new ClassPathXmlApplicationContext("tracker.xml");
			
			Region<String, CheckIn> region = ctx.getBean("checkInRegion", Region.class);
			
			AtomicBoolean running = new AtomicBoolean(true);
			StopperThread stopper = new StopperThread(running);
			stopper.start();
			
			while(running.get()){
				CheckIn c = randomCheckIn();
				region.put(c.getCity(), c);
				System.out.println(String.format("%s %s", c.getPlate(), c.getCity()));
				try {Thread.sleep(5000);}
				catch(InterruptedException x){}
			}
			System.out.println("Bye");
			
		} catch(Exception x){
			x.printStackTrace(System.err);
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	private static String []plates = {
	  "QWE111","ASD222", "POI098","ABC765","HHG767", "DSA345", "TKN567", "ABC123"};
	
	private static String []locations = {
	   "Atlanta, GA", "Baltimore, MD", "Cincinnati, OH", "Dallas, TX",
	   "Houston, TX", "Las Vegas, NV", "Philadelphia, PA", "Reston, VA",
	   "Chicago, IL", "Washington, DC", "Los Angeles, CA", "Austin, TX", "New York, NY",
	   "Charleston, SC", "Miami, FL", "Tampa, FL", "Mobile, AL", "Chattanooga, TN"};

	private static Random rand = new Random();
	
	private static CheckIn randomCheckIn(){
		CheckIn result = new CheckIn();
		result.setId(UUID.randomUUID().toString());
		result.setTimestamp(new Date());
		result.setCity(locations[rand.nextInt(locations.length)]);
		result.setPlate(plates[rand.nextInt(plates.length)]);
		return result;
	}
	
	private static class StopperThread extends Thread {

		private AtomicBoolean signal;
		
		public StopperThread(AtomicBoolean signal){
			this.signal = signal;
		}
		
		@Override
		public void run() {
			System.out.println("Press Enter to Stop");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				reader.readLine();
			} catch(IOException iox){
				// shut down anyway
			}
			signal.set(false);
		}
		
	}
	
}
