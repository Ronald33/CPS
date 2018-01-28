package ronald.tesis.clustering.kmeans;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	public static double[] getCentroid(double sum[], int numberOfPoints)
	{
		int dimention = sum.length;
		double centroid[] = new double[dimention];
		for (int i = 0; i < dimention; i++) { centroid[i] = sum[i] / numberOfPoints; }
		return centroid;
	}
}
