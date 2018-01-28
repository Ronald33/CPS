package ronald.tesis.comparative.weights;

import ronald.tesis.point.Point;

public class WMap
{
	private double _m1;
	private double _m2;
	
	public WMap(double m1)
	{
		this._m1 = m1;
		this._m2 = 1 - m1;
	}
	public WMap(Point p) { this(p.getValues()); }
	public WMap(double values[]) { this(values[0]); }
	
	public double getM1() { return _m1; }
	public double getM2() { return _m2; }
	
	@Override
	public String toString() { return "[" + this._m1 + ", " + this._m2 + "]"; }
}