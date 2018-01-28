package ronald.tesis.comparative;

import java.math.BigDecimal;

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
}