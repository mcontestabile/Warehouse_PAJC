package it.unibs.pajc.warehouse;

import java.util.Random;

public class RandomNumbers {
	
	private static Random random = new Random();
	
	public static int obtainInt(int min, int max)
	{
		 int range = max + 1 - min;
		 int casual = random.nextInt(range);
		 return casual + min;
	}
	
	public static double obtainDouble(double min, double max)
	{
		double range = max - min;
		double casual = random.nextDouble();
		double positiveValueObtained = range * casual;
	 
		return positiveValueObtained + min;
	}

}