package ronald.tesis.clustering.birch.cftree;

import java.util.ArrayList;

public class NonLeaf extends Node
{
	private static final long serialVersionUID = 1L;
	private ArrayList<Node> _children;
	
	public NonLeaf(int maxLength)
	{
		super(maxLength);
		this._children = new ArrayList<>();
	}
	
	public void addChild(Node node) throws Exception
	{
		this._children.add(node);
		node.setParent(this);
	}
	
	public void updateParents(CF cf) throws Exception
	{
		NonLeaf current = this;
		do
		{
			NonLeaf parent = current._parent;
			if(parent == null) { break; }
			else { parent._sum.add(cf); current = parent; }
		}
		while(true); 
	}
	
	@Override
	public void transferFromBrother(Node node, CFTree cftree) throws Exception
	{
		NonLeaf nonLeaf = (NonLeaf) node;
		this.addChildrensFromBrother(nonLeaf); // Revisar
		this.getSum().add(nonLeaf.getSum());
	}
	
	private void addChildrensFromBrother(NonLeaf brother)
	{
		ArrayList<Node> children = brother.getChildren();
		int size = children.size();
		for(int i = 0; i < size; i++)
		{
			Node node = children.get(i);
			this._children.add(node);
			node.setParent(this);
		}
	}
	
	/* PM */
	/* End PM */
	
	/* Node */
	@Override
	public int getSize() { return this._children.size(); }
	@Override
	public CF getEntryByIndex(int index) { return this._children.get(index).getSum(); }
	@Override
	protected void insertIntoClosest(CF cf, CFTree cftree) throws Exception
	{
		int closest_index = Helper.getIndexClosest(this, cf);
		this.getChildren().get(closest_index).insertIntoClosest(cf, cftree);
	}
	/* End Node */
	
	/* GyS */
	public ArrayList<Node> getChildren() { return this._children; }
	/* End GyS */

	@Override
	protected void transferElementByIndex(int index, Node destination) throws Exception
	{
		NonLeaf nonLeaf = (NonLeaf) destination;
		Node element = this._children.get(index);
		CF cf = this.getEntryByIndex(index);
		nonLeaf.addChild(element);
		nonLeaf.addToSum(cf);
		this._children.remove(index);
		this.subtractToSum(cf);
	}

	@Override
	public boolean verifyAndSplit(CFTree cftree) throws Exception
	{
		if(this._children.size() > this._maxLength)
		{
			if(this.getParent() == null) { this.addParent(cftree.getRoot()); cftree.setRoot(this.getParent()); }
			NonLeaf newNonLeaf = new NonLeaf(cftree.getB());
			this.fillNodeSplitted(newNonLeaf);
			this._parent.addChild(newNonLeaf);
			/* Update Indexs */
			cftree.setDataRefine(this);
			/* End Update Indexs */

			this._parent.verifyAndSplit(cftree); // Recursive
			return true;
		}
		else { return false; }
	}
	
	public void addParent(NonLeaf root) throws Exception
	{
		NonLeaf newRoot = new NonLeaf(this._maxLength);
		newRoot.addChild(this);
		newRoot.addToSum(this._sum);
	}
	
	public int getIndexByChild(Node node)
	{
		int size = this._children.size();
		for(int i = 0; i < size; i++) { if(this._children.get(i) == node) { return i; } }
		return -1;
	}
	
	public void joinNodes(int index1, int index2, CFTree cftree) throws Exception
	{
		cftree.getEntries();
		this._children.get(index1).transferFromBrother(this._children.get(index2), cftree);
		this._children.remove(index2);
		cftree.setStopNode(null);
		cftree.setIndex1(-1);
		cftree.setIndex2(-1);
		this._children.get(index1).verifyAndSplit(cftree);
	}
}