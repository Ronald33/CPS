package ronald.tesis.clustering.birch.cftree;

import java.util.ArrayList;

import ronald.tesis.point.Point;

public abstract class Helper extends ronald.tesis.helper.Helper
{
	public static int getIndexClosest(Node node, CF cf)
	{
		int size = node.getSize(), min_index = 0;
		double min_distance = Double.MAX_VALUE;
		for(int i = 0; i < size; i++)
		{
			double distance = node.getEntryByIndex(i).distance(cf);
			if(distance < min_distance) { min_distance = distance; min_index = i; }
		}
		return min_index;
	}
	public static int[] getFarthest(Node node)
	{
		int size = node.getSize(), index_1 = 0, index_2 = 0;
		double max_distance = 0;
		for(int i = 0; i < size - 1; i++)
		{
			for(int j = i + 1; j < size; j++)
			{
				CF cf1 = node.getEntryByIndex(i);
				CF cf2 = node.getEntryByIndex(j);
				double distance = cf1.distance(cf2);
				if(distance > max_distance)
				{
					index_1 = i;
					index_2 = j;
					max_distance = distance;
				}
			}
		}
		return new int[]{index_1, index_2};
	}
	public static int[] getClosest(Node node)
	{
		int size = node.getSize(), index_1 = 0, index_2 = 0;
		double distance_min = Double.MAX_VALUE;
		for(int i = 0; i < size - 1; i++)
		{
			for(int j = i + 1; j < size; j++)
			{
				CF cf1 = node.getEntryByIndex(i);
				CF cf2 = node.getEntryByIndex(j);
				double distance = cf1.distance(cf2);
				if(distance < distance_min)
				{
					index_1 = i;
					index_2 = j;
					distance_min = distance;
				}
			}
		}
		return new int[]{index_1, index_2};
	}
	private static void printVG(Node node, StringBuilder sb)
	{
		if(node != null)
		{
			sb.append(node.hashCode() + "[label=\"" + node + "\"];");
			sb.append("\n");
			if(node.getClass().equals(NonLeaf.class))
			{
				NonLeaf non_leaf = (NonLeaf) node;
				int size = non_leaf.getSize();
				for(int i = 0; i < size; i++)
				{
					Node child = non_leaf.getChildren().get(i);
					printVG(child, sb);
					sb.append(node.hashCode() + " -> " + child.hashCode());
					sb.append("\n");
				}
			}
		}
	}
	public static String getGVFromCFtree(CFTree cftree)
	{
		StringBuilder sb = new StringBuilder();
		if(cftree.getRoot() != null)
		{
			sb.append("digraph g {");
			sb.append("\n");
			sb.append("node [shape=record];");
			sb.append("\n");
			printVG(cftree.getRoot(), sb);
			sb.append("}");
		}
		return sb.toString();
	}
	
	public static Point[] getMedias(ArrayList<CF> centroids)
	{
		int size = centroids.size();
		Point _centroids[] = new Point[size];
		
		for(int i = 0; i < size; i++)
		{
			CF cf = centroids.get(i);
			int dimention = cf.getDimention();
			double p[] = new double[dimention];
			for(int j = 0; j < dimention; j++)
			{
				p[j] = cf.getLS()[j] / cf.getN();
			}
			_centroids[i] = new Point(p);
		}
		return _centroids;
	}
}
