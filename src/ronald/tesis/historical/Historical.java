package ronald.tesis.historical;

import java.io.IOException;
import java.util.ArrayList;

import ronald.tesis.point.Point;

public class Historical
{
	public static void saveHistorical(ArrayList<Point> points, int percentage, String path) throws IOException
	{
		ArrayList<Point> subpoints = Helper.getSubSample(points, percentage);
		ronald.tesis.point.Helper.addPointsToFile(subpoints, path);
	}
}
