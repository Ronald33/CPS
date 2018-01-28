package ronald.tesis.clustering.birch.cftree;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import ronald.tesis.point.Point;

public class CFTree implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private double _T;
	private int _B; 
	private int _L;
	
	private NonLeaf _root;
	private Leaf _firstLeaf;
	
	/* Refine */
	private NonLeaf _stopNode;
	private int _index1;
	private int _index2;
	/* End Refine */
	
	public CFTree(double T, int B, int L)
	{
		this._T = T;
		this._B = B;
		this._L = L;
	}
	
	public void insert(Point point) throws Exception { this.insert(new CF(point)); }
	public void insert(CF cf) throws Exception
	{
		if(this._root == null) { this.insertFirstCF(cf); }
		else { this._root.insertIntoClosest(cf, this); }
	}
	
	public void save(String path) throws IOException
	{
		FileOutputStream fileOut = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fileOut);
		oos.writeObject(this);
		oos.close();
	    fileOut.close();
	}
	public static CFTree load(String path) throws IOException, ClassNotFoundException
	{
		FileInputStream fileIn = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fileIn);
		CFTree cft = (CFTree) ois.readObject();
		ois.close();
		fileIn.close();
		return cft;
	}
	
	public ArrayList<CF> getEntries()
	{
		ArrayList<CF> entries = new ArrayList<>();
		Leaf current = this._firstLeaf;
		while(current != null)
		{
			int size = current.getSize();
			for(int i = 0; i < size; i++) { entries.add(current.getEntryByIndex(i).getClone()); }
			current = current.getNext();
		}
		return entries;
	}
	
	/* PM */
	private void insertFirstCF(CF cf) throws Exception
	{
		Leaf leaf = new Leaf(_L);
		leaf.addEntry(cf);
		leaf.addToSum(cf);
		this._firstLeaf = leaf;
		
		this._root = new NonLeaf(_B);
		this._root.addToSum(cf);
		this._root.addChild(leaf);
	}
	/* End PM */
	
	/* GyS */
	public void setRoot(NonLeaf root) { this._root = root; }
	public NonLeaf getRoot() { return this._root; }
	public void setFirstLeaf(Leaf firstLeaf) { this._firstLeaf = firstLeaf; }
	public Leaf getFirstLeaf() { return this._firstLeaf; }
	public double getT() { return this._T; }
	public int getB() { return this._B; }
	public int getL() { return this._L; }
	/* End GyS */
	
	/* Refine */
	public void refine() throws Exception
	{
		if(this._stopNode.getSize() > 2)
		{
			int closest[] = Helper.getClosest(this.getStopNode());
			int closest1 = closest[0];
			int closest2 = closest[1];
			if(closest1 != this.getIndex1() || closest2 != this.getIndex2())
			{
				this.getStopNode().joinNodes(closest1, closest2, this);
			}
		}
	}
	public void setDataRefine(Node node)
	{
		this._stopNode = node.getParent();
		this._index1 = node.getParent().getIndexByChild(node);
		this._index2 = node.getParent().getSize() - 1;
	}
	public NonLeaf getStopNode() { return this._stopNode; }
	public void setStopNode(NonLeaf stopNode) { this._stopNode = stopNode; }
	public int getIndex1() { return this._index1; }
	public void setIndex1(int index1) { this._index1 = index1; }
	public int getIndex2() { return this._index2; }
	public void setIndex2(int index2) { this._index2 = index2; }
	/* End Refine */
}