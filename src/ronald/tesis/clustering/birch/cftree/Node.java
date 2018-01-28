package ronald.tesis.clustering.birch.cftree;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Node implements Serializable
{
	private static final long serialVersionUID = 1L;
	protected CF _sum;
	protected NonLeaf _parent;
	protected int _maxLength;
	
	public Node(int maxLength)
	{
		this._maxLength = maxLength;
	}
	
	protected void addToSum(CF cf) throws Exception
	{
		if(this._sum == null) { this._sum = cf.getClone(); }
		else { this._sum.add(cf); }
	}
	protected void subtractToSum(CF cf) throws Exception { this._sum.subtract(cf); }
	
	protected void fillNodeSplitted(Node newNode) throws Exception
	{
		int farthest[] = Helper.getFarthest(this);
		int index_1 = farthest[0], index_2 = farthest[1]; 
		CF cf1 = this.getEntryByIndex(index_1), cf2 = this.getEntryByIndex(index_2);
		this.transferElementByIndex(index_2, newNode);
		int size = this.getSize();
		for(int i = 0; i < size; i++)
		{
			if(i != index_1)
			{
				CF cfi = this.getEntryByIndex(i);
				if(cfi.distance(cf2) < cfi.distance(cf1))
				{
					this.transferElementByIndex(i, newNode);
					i--;
					size--;
				}
			}
		}
	}
	
	/* GyS */
	public CF getSum() { return this._sum; }
//	public void setSum(CF sum) { this._sum = sum.getClone(); }
	public NonLeaf getParent() { return this._parent; }
	public void setParent(NonLeaf parent) { this._parent = parent; }
	public int getMaxLength() { return this._maxLength; }
	public void setMaxLength(int maxLength) { this._maxLength = maxLength; }
	/* End GyS */
	
	/* Abstract */
	protected abstract int getSize();
	protected abstract CF getEntryByIndex(int index);
	protected abstract void insertIntoClosest(CF cf, CFTree cftree) throws Exception;
	protected abstract void transferElementByIndex(int index, Node destination) throws Exception;
	protected abstract void transferFromBrother(Node node, CFTree cftree) throws Exception;
	protected abstract boolean verifyAndSplit(CFTree cftree) throws Exception;
//	protected abstract boolean verifyAndSplit() throws Exception;
	/* End Abstract */
	
	@Override
	public String toString()
	{
		ArrayList<CF> my_entries = new ArrayList<>();
		for(int i = 0; i < this.getSize(); i++)
		{
			my_entries.add(this.getEntryByIndex(i));
		}
		return my_entries.toString();
	}
}