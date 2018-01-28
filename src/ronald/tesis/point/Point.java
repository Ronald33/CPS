package ronald.tesis.point;

import java.util.Arrays;

public class Point
{
	private double _values[];
	
	public Point(double ... values) { this._values = values; }

	public double getDistance(Point point) throws Exception
	{
		return ronald.tesis.helper.Helper.getDistance(this._values, point.getValues());
	}
	public Point getClone() { return new Point(this._values); }
	public int getDimention() { return this._values.length; }
	public String toString(String separator)
	{
		int dimention = this._values.length;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < dimention; i++)
		{
			sb.append(this._values[i]);
			if(i != dimention - 1) { sb.append(separator); }
		}
		return sb.toString();
	}
	/* GyS */
	public void setValues(double ... values) { this._values = values; }
	public double[] getValues() { return this._values; }
	/* End GyS */
	/* Overrides */
	@Override
	public String toString() { return Arrays.toString(this._values); }
	/* End Overrides */
}