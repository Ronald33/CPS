package ronald.tesis.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public abstract class Helper
{
	// Clustering
	public static double getDistance(double a[], double b[]) throws Exception
	{
		double distance = 0;
		int a_length = a.length, b_length = b.length;
		if(a_length == b_length)
		{
			for(int i = 0; i < a_length; i++)
			{
				double difference = a[i] - b[i]; 
				distance += Math.pow(difference, 2);
			}
		}
		else { throw new Exception(Arrays.toString(a) + " and " + Arrays.toString(b) + " have different dimentions"); }
		return Math.sqrt(distance);
	}
	// Folders
	public static String createFolder(String path) throws Exception
	{
		Path _path = Paths.get(path);
		if(!Files.exists(_path)) { Files.createDirectories(_path); }
		return path;
	}
	// Files
	public static void writeFile(String path, String content) throws IOException
	{
		FileWriter fw = new FileWriter(path);
		fw.write(content);
		fw.close();
	}
	public static boolean existsFile(String path)
	{
		File f = new File(path);
		return f.exists();
	}
	public static void deleteFile(String path)
	{
		File f = new File(path);
		f.delete();
	}
	// Arrays
	public static <T extends Number> double getSumArray(T[] array)
	{
		int length = array.length;
		double sum = 0;
		for(int i = 0; i < length; i++) { sum += array[i].doubleValue(); }
		return sum;
	}
	
//	public static int getSum(int array[])
//	{
//		int length = array.length;
//		int sum = 0;
//		for(int i = 0; i < length; i++) { sum += array[i]; }
//		return sum;
//	}
//	public static double getSum(double array[])
//	{
//		int length = array.length;
//		double sum = 0;
//		for(int i = 0; i < length; i++) { sum += array[i]; }
//		return sum;
//	}
	public static double avg(Double array[]) throws Exception
	{
		int length = array.length;
		if(length > 0) { return getSumArray(array) / length; }
		else { throw new Exception("Length of array is zero"); }
	}
	public static boolean in_array(int value, ArrayList<Integer> array)
	{
		int size = array.size();
		for (int i = 0; i < size; i++) { if(array.get(i) == value) { return true; } }
		return false;
	}
	// Matrix
	public static double getSumMatrix(double matrix[][])
	{
		int height = matrix.length;
		double sum = 0;
		if(height > 0)
		{
			int width = matrix[0].length;
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++) { sum += matrix[i][j]; }
			}
		}
		else { throw new InvalidParameterException(); }
		return sum;
	}
	public static double getAverageMatrix(double matrix[][])
	{
		int height = matrix.length, width = matrix[0].length, total = height * width;
		return getSumMatrix(matrix) / total;
	}
	// Utilities
//	public static String getTimestamp()
//	{
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        return Long.toString(timestamp.getTime());
//	}
	public static int getRandom(int start, int end)
	{
		Random rand = new Random();
	    int randomNum = rand.nextInt((end - start) + 1) + start;
	    return randomNum;
	}
	public static double[] lineToDouble(String line, int dimention) throws Exception
	{
		String[] ls = line.split("\\s+");
		int dimentionInFile = ls.length;
		if(dimention > dimentionInFile) { throw new Exception("The size established is greather than the dimention in the file"); }
		double values[] = new double[dimention];
		for (int i = 0; i < dimention; i++) { values[i] = Double.parseDouble(ls[i]); }
		return values;
	}
	// Math
	public static double round(double value, int places)
	{
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	public static double getPercentWith100(double value_100, double value)
	{
		if(value_100 == 0 && value == 0) { return 100; }
		return 100 * value / value_100;
	}
	public static double getValueWith100(double value_100, double percent)
	{
		if(value_100 == 0 || percent == 0) { return 0; }
		return value_100 * percent / 100;
	}
	// String
	public static String join(double data[], String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		int length = data.length;
		for(int i = 0; i < length; i++)
		{
			sb.append(data[i]);
			if(i  != length - 1) { sb.append(delimiter); }
		}
		return sb.toString();
	}
	
	public static String join(double data[]) { return join(data, "\t"); }
	public static String getUniqueId() { return UUID.randomUUID().toString(); }
	public static String arrayToCSV(double data[][])
	{
		StringBuilder sb = new StringBuilder();
		int height = data.length;
		if(height > 0)
		{
			int width = data[0].length;
			if(width > 0)
			{
				for(int i = 0; i < height; i++)
				{
					sb.append(join(data[i]));
					if(i != height - 1) { sb.append("\n"); }
				}
			}
			else { throw new InvalidParameterException(); }
		}
		else { throw new InvalidParameterException(); }
		return sb.toString();
	}
	// Bash
	private static String execCommandAndGetFirstLine(String command) throws IOException, InterruptedException
	{
		String [] commands = new String[]{"/bin/bash", "-c", command};
		Process p = Runtime.getRuntime().exec(commands);
		p.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		return br.readLine();
	}
	public static String getLineOfFile(String path, int number_of_line) throws IOException, InterruptedException
	{
		String command = "cat " + path + " | head -n " + number_of_line + " | tail -n 1";
		return execCommandAndGetFirstLine(command);
	}
	// Random
	public static int getRandomUnique(int min, int max, ArrayList<Integer> selecteds)
	{
		HashSet<Integer> discarteds = new HashSet<>();
		do
		{
			int random = Helper.getRandom(min, max);
			if (Helper.in_array(random, selecteds)) { discarteds.add(random); }
			else { selecteds.add(random); return random; }
		}
		while ((max - min + 1) != discarteds.size());
		return -1;
	}
}
