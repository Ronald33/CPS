package ronald.tesis.clustering.kmeans;

import java.util.ArrayList;
import java.util.Arrays;

import ronald.tesis.clustering.Clustering;
import ronald.tesis.point.Point;

public class KMeans extends Clustering
{
	private int _k = Config.k;
	private int _maxIterations = Config.maxIterations;
	private int _numberOfIteration;
	private boolean _converged;
	private Point[] _centroids;
	
	public KMeans()
	{
		
	}
	
	public KMeans(int k, int maxIterations)
	{
		this._k = k;
		this._maxIterations = maxIterations;
	}

	@Override
	public void execute(ArrayList<Point> points) throws Exception
	{
		this.start();
		this.initialize();
		this.setPoints(points);
		this.setRandomCentroids();
		this.setClusters();
		this.end();
	}
	
	private void initialize()
	{
		this._centroids = new Point[this._k];
		this._numberOfIteration = 0;
		this._converged = false;
	}
	
	private void setRandomCentroids() throws Exception
	{
		int points_size = this._points.size();
		
		for(int i = 0; i < this._k; i++)
		{
			this._centroids[i] = this._points.get(Helper.getRandom(0, points_size - 1)).getClone();
		}
	}
	private void setClusters() throws Exception
	{
		int size_points = this._points.size(), size_centroids = this._centroids.length;
		do
		{
			this._numberOfIteration++;
			double sum[][] = new double[size_centroids][this._dimention];
			int size_cluster[] = new int[size_centroids];

			for (int i = 0; i < size_points; i++)
			{
				Point point = this._points.get(i);
				double distance_min = Double.MAX_VALUE;
				int centroid_index = 0;
				// Detectamos el centroide mas cercano
				for (int j = 0; j < size_centroids; j++)
				{
					double distance = point.getDistance(this._centroids[j]);
					if (distance < distance_min) { distance_min = distance; centroid_index = j; }
				}
				// Vamos acumulando los puntos que pertenecen al cluster
				for (int j = 0; j < this._dimention; j++) { sum[centroid_index][j] += this._points.get(i).getValues()[j]; }
				size_cluster[centroid_index]++; // Vamos sumando la cantidad de puntos que pertenecen al cluster
			}
			this._converged = this.tryToConverge(sum, size_cluster); // Movemos los centroides
			if(this._numberOfIteration >= this._maxIterations) { break; }
		}
		while (!this._converged);
	}
	private boolean tryToConverge(double sum[][], int size_cluster[])
	{
		int centroids_size = this._centroids.length;
		boolean converged = true;
		for (int i = 0; i < centroids_size; i++)
		{
			Point centroid = this._centroids[i];
			double values[] = Helper.getCentroid(sum[i], size_cluster[i]);
			if (!Arrays.equals(centroid.getValues(), values))
			{
				centroid.setValues(values);
//				this._centroids[i].setValues(values);
				converged = false;
			}
		}
		return converged;
	}
	
	/* GyS */
	public void setK(int k) { this._k = k; }
	public void setMaxIterations(int maxIterations) { this._maxIterations = maxIterations; }
	public int getDimention() { return this._dimention; }
	public Point[] getCentroids() { return this._centroids; }
	/* End GyS */

	@Override
	public void saveReport(String path, boolean savePlot) throws Exception
	{
		Helper.createFolder(path);
		this.appendTimeToReport();
		this._report.append("Dimention: " + this._dimention + "\n");
		this._report.append("K: " + this._k + "\n");
		this._report.append("Max. Iterations: " + this._maxIterations + "\n");
		this._report.append("Number Of Iterations: " + this._numberOfIteration + "\n");
		this._report.append("Converged?: " + this._converged + "\n");
		Helper.writeFile(path + "/report.txt", this.getReport());
		if(savePlot) { ronald.tesis.point.Helper.writePointsAndCentroids(this._points, this._centroids, path + "/points.txt"); }
	}
}