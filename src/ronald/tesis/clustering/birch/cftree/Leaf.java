package ronald.tesis.clustering.birch.cftree;

import java.util.ArrayList;

public class Leaf extends Node
{
	private static final long serialVersionUID = 1L;
	private ArrayList<CF> _entries;
	private Leaf _next; 
	private Leaf _prev;
	
	public Leaf(int maxLength)
	{
		super(maxLength);
		this._entries = new ArrayList<>();
	}
	
	@Override
	public boolean verifyAndSplit(CFTree cftree) throws Exception
	{
		if(this.getSize() > this._maxLength)
		{
			Leaf newLeaf = new Leaf(this._maxLength);
			this.fillNodeSplitted(newLeaf);
			this._parent.addChild(newLeaf);
			newLeaf.setNext(this._next);
			newLeaf.setPrev(this);
			if(this._next != null) { this._next.setPrev(newLeaf); }
			this.setNext(newLeaf);
			/* Update Indexs */
			cftree.setDataRefine(this);
			/* End Update Indexs */
			this._parent.verifyAndSplit(cftree);
			return true;
		}
		else { return false; }
	}
	
	@Override
	public void transferFromBrother(Node node, CFTree cftree) throws Exception
	{
		Leaf leaf = (Leaf) node;
		this._entries.addAll(leaf.getEntries());
		this.getSum().add(leaf.getSum());
		if(leaf.getPrev() != null) { leaf.getPrev().setNext(leaf.getNext()); }
		if(leaf.getNext() != null) { leaf.getNext().setPrev(leaf.getPrev()); }
		if(leaf == cftree.getFirstLeaf()) { cftree.setFirstLeaf(leaf.getNext()); }
	}
	
	/* GyS */
	public ArrayList<CF> getEntries() { return this._entries; }
	public void addEntry(CF entry) { this._entries.add(entry); }
	public Leaf getNext() { return this._next; }
	public void setNext(Leaf next) { this._next = next; }
	public Leaf getPrev() { return this._prev; }
	public void setPrev(Leaf prev) { this._prev = prev; }
	/* End GyS */

	/* Node */
	@Override
	protected int getSize() { return this._entries.size(); }
	@Override
	protected CF getEntryByIndex(int index) { return this._entries.get(index); }
	@Override
	protected void insertIntoClosest(CF cf, CFTree cftree) throws Exception
	{
		int closest_index = Helper.getIndexClosest(this, cf);
		CF closest_entry = this.getEntryByIndex(closest_index);
		double distance = closest_entry.distance(cf);
		
		if(distance <= cftree.getT()) { closest_entry.add(cf); }
		else { this.addEntry(cf); }
		this.addToSum(cf);
		this._parent.addToSum(cf);
		this._parent.updateParents(cf);
		if(this.verifyAndSplit(cftree)) { cftree.refine(); }
	}
	@Override
	protected void transferElementByIndex(int index, Node destination) throws Exception
	{
		Leaf leaf = (Leaf) destination;
		CF element = this.getEntryByIndex(index);
		leaf.addEntry(element);
		leaf.addToSum(element);
		this._entries.remove(index);
		this.subtractToSum(element);
	}
	/* End Node */
}