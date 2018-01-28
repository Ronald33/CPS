package ronald.tesis.generator;

public class Range
{
	private double _start;
	private double _end;
	
	public Range(double start, double end)
	{
		this._start = start > 0 ? start : 0;
		this._end = end < 1 ? end : 1;
	}
	
	public double getStart() { return this._start; }
	public double getEnd() { return this._end; }
	
	@Override
	public String toString() { return "[" + this._start + ", " + this._end + "]"; }
}
