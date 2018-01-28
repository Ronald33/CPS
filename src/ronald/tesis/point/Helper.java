package ronald.tesis.point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	public static void addPointsToFile(ArrayList<Point> points, String path) throws IOException
	{
		savePoints(points, path, true);
	}
	public static void writePointsToFile(ArrayList<Point> points, String path) throws IOException
	{
		savePoints(points, path, false);
	}
	private static void savePoints(ArrayList<Point> points, String path, boolean append) throws IOException
	{
		FileWriter fw = new FileWriter(path, append);
		int size = points.size();
		for(int i = 0; i < size; i++) { fw.write(points.get(i).toString("\t") + "\n"); }
	    fw.close();
	}
	public static void writePointsAndCentroids(ArrayList<Point> points, Point centroids[], String path) throws Exception
	{
		FileWriter fw = new FileWriter(path, true);
		int size = points.size(), 
			centroids_size = centroids.length, 
			centroidIndex = -1;
		double distance_min;
		for(int i = 0; i < size; i++)
		{
			Point point = points.get(i);
			distance_min = Double.MAX_VALUE;
			for(int j = 0; j < centroids_size; j++)
			{
				double distance = point.getDistance(centroids[j]);
				if(distance < distance_min)
				{
					distance_min = distance;
					centroidIndex = j;
				}
			}
			fw.write(point.toString("\t") + "\t" + centroidIndex + "\n");
		}
		fw.close();
	}
	public static ArrayList<Point> fileToPoints(String path, int dimention) throws Exception
	{
		ArrayList<Point> points = new ArrayList<>();
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		while ((line = br.readLine()) != null)
		{
			double values[] = lineToDouble(line, dimention);
			points.add(new Point(values));
		}
		br.close();
		return points;
	}
	public static ArrayList<Point> getRandomPoints(int size, int dimention, int min, int max)
	{
		ArrayList<Point> points = new ArrayList<>();
		for(int i = 0; i < size; i++)
		{
			double values[] = new double[dimention];
			for(int j = 0; j < dimention; j++) { values[j] = Helper.getRandom(min, max); }
			points.add(new Point(values));
		}
		return points;
	}
}
