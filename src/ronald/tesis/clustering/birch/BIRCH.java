package ronald.tesis.clustering.birch;

import java.io.IOException;
import java.util.ArrayList;

import ronald.tesis.clustering.Clustering;
import ronald.tesis.clustering.birch.cftree.CF;
import ronald.tesis.clustering.birch.cftree.CFTree;
import ronald.tesis.clustering.birch.cftree.Helper;
import ronald.tesis.point.Point;

public class BIRCH extends Clustering
{
	private CFTree _cftree;
	private CFTree _cftreeCompacted;
	private double _T = Config.T;
	private int _B = Config.B;
	private int _L = Config.L;
	private String _id;
	
	public BIRCH(String id) throws Exception
	{
		this._id = id;
		this._cftree = CFTree.load(this.getStreamingPath());
		this._T = this._cftree.getT();
		this._B = this._cftree.getB();
		this._L = this._cftree.getL();
	}
	
	public BIRCH(double T, int B, int L, String id) throws Exception
	{
		this._T = T;
		this._B = B;
		this._L = L;
		this._id = id;
		this._cftree = new CFTree(this._T, this._B, this._L);
		this.createFolders();
	}
	public String getStreamingPath() { return Config.folderStreaming + "/" + this._id + "/cftreeCompacted.birch"; }
	private void createFolders() throws Exception
	{
		Helper.createFolder(Config.folderStreaming + "/" + this._id);
//		Helper.createFolder(Config.folderHistorical + "/" + this._id);
	}
	
	public void save() throws IOException
	{
		this._cftreeCompacted.save(this.getStreamingPath());
//		ronald.tesis.historical.Historical.saveHistorical(this._points, percentage, this.getHistoricalPath());
	}
	
//	private void setB() throws Exception
//	{
//		ArrayList<Integer> wk = new ArrayList<>();
//		int k = 2;
//		// Recover subsample
//		ArrayList<Point> points = ronald.tesis.point.Helper.fileToPoints(this.getHistoricalPath(), this._dimention);
////		ronald.tesis.historical.Helper.getSubSample(this._points, );
//		while(true)
//		{
//			KMeans km = new KMeans();
//			km.setK(k);
//			km.execute(points);
//		}
//	}
	
	@Override
	public void execute(ArrayList<Point> points) throws Exception
	{
		this.start();
		this.setPoints(points);
		this.fillFirstCFTree();
		this.fillCFTreeCompacted();
		ArrayList<CF> entries = phase3(this._cftreeCompacted.getEntries(), this._T);
		this._centroids = Helper.getMedias(entries);
		this.end();
	}

	@Override
	public void saveReport(String path, boolean savePlot) throws Exception
	{
		Helper.createFolder(path);
		this.appendTimeToReport();
		this._report.append("Dimention: " + this._dimention + "\n");
		this._report.append("T: " + this._T + "\n");
		this._report.append("B: " + this._B + "\n");
		this._report.append("L: " + this._L + "\n");
		Helper.writeFile(path + "/report.txt", this.getReport());
		if(savePlot) { ronald.tesis.point.Helper.writePointsAndCentroids(this._points, this._centroids, path + "/points.txt"); }
	}
	
	private void fillFirstCFTree() throws Exception
	{
		int points_size = this._points.size();
		for(int i = 0; i < points_size; i++) { this._cftree.insert(this._points.get(i)); }
	}
	
	private void fillCFTreeCompacted() throws Exception
	{
		this._cftreeCompacted = new CFTree(this._cftree.getT(), this._cftree.getB(), this._cftree.getL());
		ArrayList<CF> entries = this._cftree.getEntries();
		
		int entries_size = entries.size();
		for(int i = 0; i < entries_size; i++) { this._cftreeCompacted.insert(entries.get(i)); }
	}
	
	private static ArrayList<CF> phase3(ArrayList<CF> entries, double T) throws Exception
	{
		int centroids_size = entries.size();
		if(centroids_size > 1)
		{
			double distance = 0, max_distance = Double.MAX_VALUE;
			int c1 = 0, c2 = 0;
			
			for(int i = 0; i < centroids_size - 1; i++)
			{
				for(int j = i + 1; j < centroids_size; j++)
				{
					distance = entries.get(i).distance(entries.get(j));
					if(distance < max_distance)
					{
						max_distance = distance;
						c1 = i;
						c2 = j;
					}
				}
			}
			if(distance < T)
			{
				entries.get(c1).add(entries.get(c2));
				entries.remove(c2);
				return phase3(entries, T);
			}
			else { return entries; }
		}
		else { return entries; }
	}

	/* GyS */
	public CFTree getCFTree() { return this._cftree; }
	public CFTree getCFTreeCompacted() { return this._cftreeCompacted; }
	public double getT() { return this._T; }
	public int getB() { return this._B; }
	public int getL() { return this._L; }
	public String getId() { return this._id; }
	/* End GyS */
}