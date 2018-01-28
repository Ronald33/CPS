package ronald.tesis.comparative.weights;

import ronald.tesis.point.Point;

public class WReduce
{
	private double _r1;
	private double _r2;
	private double _r3;
	
	public WReduce(double r1, double r2)
	{
		this._r1 = r1;
		this._r2 = r2;
		this._r3 = 1 - (r1 + r2);
	}
	public WReduce(Point p) { this(p.getValues()); }
	public WReduce(double values[]) { this(values[0], values[1]); }
	
	public double getR1() { return _r1; }
	public double getR2() { return _r2; }
	public double getR3() { return _r3; }
}
