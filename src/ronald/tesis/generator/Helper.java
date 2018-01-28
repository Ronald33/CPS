package ronald.tesis.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	private static int M1 = 0;
	private static int R1 = 1;
	private static int R2 = 2;
	
	public static double ranges_grep[][] = new double[][]{{0.025, 0.002}, {0.429, 0.417}, {0.002, 0.003}};
	public static double ranges_kmeans[][] = new double[][]{{0.151, 0.40}, {0.095, 0.099}, {0.013, 0.007}};
	public static double ranges_pi[][] = new double[][]{{0.009, 0.003}, {0.001, 0.001}, {0.000, 0.000}};
	public static double ranges_sort[][] = new double[][]{{0.219, 0.146}, {0.021, 0.046}, {0.060, 0.029}};
	public static double ranges_validate[][] = new double[][]{{0.001, 0.001}, {0.022, 0.033}, {0.002, 0.005}};
	public static double ranges_wc[][] = new double[][]{{0.502, 0.049}, {0.001, 0.001}, {0.001, 0.000}};
	
	public static double ranges[][][] = new double[][][]{
		ranges_grep, ranges_kmeans, ranges_pi, ranges_sort, ranges_validate, ranges_wc
	};
	
	private static int GREP = 0;
	private static int KMEANS = 1;
	private static int PI = 2;
	private static int SORT = 3;
	private static int VALIDATE = 4;
	private static int WC = 5;
	
	public static ArrayList<String> jobs = new ArrayList<>(Arrays.asList("grep", "kmeans", "pi", "sort", "validate", "wordcount"));
	
	public static ArrayList<String> getNameJobs(int size_jobs)
	{
		ArrayList<String> names = new ArrayList<>();
		for(int i = 0; i < size_jobs; i++) { names.add(Config.nameJob + (i+1)); }
		return names;
	}
		
	public static void writeWeights(int min_id, int max_id, int amount_of_tasks) throws Exception
	{
		writeGreps(min_id, max_id, amount_of_tasks);
		writeKMeans(min_id, max_id, amount_of_tasks);
		writePis(min_id, max_id, amount_of_tasks);
		writeSorts(min_id, max_id, amount_of_tasks);
		writeValidates(min_id, max_id, amount_of_tasks);
		writeWordCounts(min_id, max_id, amount_of_tasks);
	}
	public static void writeGreps(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.920, 0.648, 0.997}, 
			{0.520, 0.925, 0.581}, 
			{0.070, 0.008, 0.002} 
		};
		writeWeights(jobs.get(GREP), min_id, max_id, amount_of_tasks, averages, ranges[GREP]);
	}
	public static void writeKMeans(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.880}, 
			{0.540}, 
			{0.020} 
		};
		writeWeights(jobs.get(KMEANS), min_id, max_id, amount_of_tasks, averages, ranges[KMEANS]);
	}
	public static void writePis(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.996}, 
			{0.998}, 
			{0.000} 
		};
		writeWeights(jobs.get(PI), min_id, max_id, amount_of_tasks, averages, ranges[PI]);
	}
	public static void writeSorts(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.090, 0.806, 0.823}, 
			{0.700, 0.415, 0.801}, 
			{0.030, 0.027, 0.089} 
		};
		writeWeights(jobs.get(SORT), min_id, max_id, amount_of_tasks, averages, ranges[SORT]);
	}
	public static void writeValidates(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.999}, 
			{0.283}, 
			{0.007} 
		};
		writeWeights(jobs.get(VALIDATE), min_id, max_id, amount_of_tasks, averages, ranges[VALIDATE]);
	}
	public static void writeWordCounts(int min_id, int max_id, int amount_of_tasks) throws Exception
	{		
		double averages[][] = new double[][]{
			{0.210, 0.900, 0.870, 0.911}, 
			{0.370, 0.470, 0.888, 0.988}, 
			{0.020, 0.110, 0.015, 0.001} 
		};
		writeWeights(jobs.get(WC), min_id, max_id, amount_of_tasks, averages, ranges[WC]);
	}
	public static void writeRandomJobs(int min_id, int max_id, int amount_of_tasks, int min_job_id, int max_job_id) throws Exception
	{
		for(int i = min_job_id; i <= max_job_id; i++)
		{
			String folder = Config.nameJob + i;
			double m1 = getRandom(0, 1, Config.numberOfDecimals);
			double r1 = getRandom(0, 1, Config.numberOfDecimals);
			double r2 = getRandom(0, 1 - r1, Config.numberOfDecimals);
			double averages[][] = new double[][] {
				{m1}, 
				{r1}, 
				{r2}				
			};
			writeWeights(folder, min_id, max_id, amount_of_tasks, averages, ranges[Helper.getRandom(0, ranges.length - 1)]);
		}
	}
	private static void writeWeights(String folder, int min_id, int max_id, int amount_of_tasks, double averages[][], double ranges[][]) throws Exception
	{
		String path_map = Config.path + "/" + folder + "/map"; 
		String path_reduce = Config.path + "/" + folder + "/reduce";
		Helper.createFolder(path_map);
		Helper.createFolder(path_reduce);
		
		int types = averages[0].length;
		for(int i = min_id; i <= max_id; i++)
		{
			int type = getRandom(0, types - 1);
			// Map
			double average_m1 = averages[M1][type];
			Range m1 = new Range(average_m1 - ranges[M1][0], average_m1 + ranges[M1][1]);
			Helper.writeFile(Config.path + "/" + folder + "/map/" + i, generateWeights(amount_of_tasks, Config.numberOfDecimals, m1));
			// Reduce
			double average_r1 = averages[R1][type];
			double average_r2 = averages[R2][type];
			Range r1 = new Range(average_r1 - ranges[R1][0], average_r1 + ranges[R1][1]);
			Range r2 = new Range(average_r2 - ranges[R2][0], average_r2 + ranges[R2][1]);
			Helper.writeFile(Config.path + "/" + folder + "/reduce/" + i, generateWeights(amount_of_tasks, Config.numberOfDecimals, r1, r2));
		}
	}
	public static double getRandom(double start, double end, int number_of_decimals)
	{
		Random r = new Random();
		double a = Math.pow(10.0, number_of_decimals);
		double result = r.nextDouble() * (end - start) + start;
		return Math.round(result *  a) / a;
	}
	public static double getRandom(Range range, int number_of_decimals)
	{
		return getRandom(range.getStart(), range.getEnd(), number_of_decimals);
	}
	public static String generateWeights(int size, int number_of_decimals, Range ... ranges)
	{
		StringBuilder sb = new StringBuilder();
		int length = ranges.length;
		for(int i = 0; i < size; i++)
		{
			double counter = 0;
			for(int j = 0; j < length; j++)
			{
				double value = Helper.getRandom(ranges[j], number_of_decimals);
				sb.append(value);
				sb.append(" ");
				counter += value;
			}
			sb.append(Helper.round(1 - counter, number_of_decimals));
			sb.append("\n");
		}
		return sb.toString();
	}
}