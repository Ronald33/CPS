package ronald.tesis.clustering;

import java.util.ArrayList;

import ronald.tesis.point.Point;

public abstract class Clustering
{
	private long _start;
	private long _end;
	protected int _dimention;
	protected StringBuilder _report = new StringBuilder();
	protected ArrayList<Point> _points;
	protected Point _centroids[];
	
	public abstract void execute(ArrayList<Point> points) throws Exception;
	public abstract void saveReport(String path, boolean savePlot) throws Exception;
	
	protected void start() { this._start = System.currentTimeMillis(); }
	public void end() { this._end = System.currentTimeMillis(); }
	public long getTime() { return this._end - this._start; }
	public Point[] getCentroids() { return this._centroids; }
	protected String getReport() { return this._report.toString(); }
	public void setPoints(ArrayList<Point> points)
	{
		this._points = points;
		this._dimention = this._points.get(0).getDimention();
	}
	
	protected void appendTimeToReport()
	{
		this._report.append("Size: " + this._points.size() + "\n");
		this._report.append("Time: " + this.getTime() + "\n");
	}
}