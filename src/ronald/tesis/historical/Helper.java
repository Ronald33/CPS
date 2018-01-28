package ronald.tesis.historical;

import java.util.ArrayList;

import ronald.tesis.point.Point;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	public static ArrayList<Point> getSubSample(ArrayList<Point> points, int percentage)
	{
		int size = (int) Math.ceil(Helper.getValueWith100(points.size(), percentage));
		ArrayList<Integer> useds = new ArrayList<>();
		ArrayList<Point> subpoints = new ArrayList<>();
		for(int i = 0; i < size; i++)
		{
			int index = Helper.getRandomUnique(0, size - 1, useds);
			subpoints.add(points.get(index));
		}
		return subpoints;
	}
}
