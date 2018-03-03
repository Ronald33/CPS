package ronald.tesis.comparative;

import java.math.BigDecimal;
import java.util.ArrayList;

import ronald.tesis.point.Point;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	public static double getError(double value_100, double value)
	{
		double percent = getPercentWith100(value_100, value);
		double error = Math.abs(100 - percent);
		return error;
	}
	
	public static double round(double value, int decimals)
	{
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static Point getPointRandomInFile(String path, int min, int max, int dimention) throws Exception
	{
		int random = Helper.getRandom(min, max);
		return new Point(lineToDouble(getLineOfFile(path, random), dimention));
	}
	
	// SAMR
	public static Point getAverage(ArrayList<Point> points) throws Exception
	{
		int size = points.size();
		if(size > 0)
		{
			int dimention = points.get(0).getDimention();
			double values[] = new double[dimention]; 
			for(int i = 0; i < size; i++) // Sum of all elements
			{
				for(int j = 0; j < dimention; j++)
				{
					values[j] += points.get(i).getValues()[j]; 
				}
			}
			// Calculating the average
			for(int i = 0; i < dimention; i++) { values[i] = values[i] / size; }
			return new Point(values);
		}
		else
		{
			throw new Exception("Error calculating average from array empty");
		}
	}
}