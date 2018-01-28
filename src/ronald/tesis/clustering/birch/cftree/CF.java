package ronald.tesis.clustering.birch.cftree;

import java.io.Serializable;
import java.util.Arrays;
import ronald.tesis.point.Point;

public class CF implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int _dimention;
	private int _n;
	private double _LS[];
	private double _SS[];
	
	public CF(int dimention)
	{
		this._dimention = dimention;
		this.initialize();
	}
	
	public CF(Point point)
	{
		this(point.getDimention());
		this._n = 1;
		for(int i = 0; i < this._dimention; i++)
		{
			this._LS[i] = point.getValues()[i];
			this._SS[i] = Math.pow(point.getValues()[i], 2);
		}
	}
	
	private void initialize()
	{
		this._n = 0;
		this._LS = new double[this._dimention];
		this._SS = new double[this._dimention];
	}
	
	public void add(CF cf) throws Exception { this.addOrRemove(cf, true); }
	public void subtract(CF cf) throws Exception { this.addOrRemove(cf, false); }
	
	private void addOrRemove(CF cf, boolean add) throws Exception
	{
		if(this._dimention == cf._dimention)
		{
			int i_add = add ? 1 : -1;
			this._n += cf.getN() * i_add;
			for(int i = 0; i < this._dimention; i++)
			{
				this._LS[i] += cf.getLS()[i] * i_add;
				this._SS[i] += cf.getSS()[i] * i_add;
			}
		}
		else { throw new Exception(this + " and " + cf + " have different dimentions"); }
	}
	
	public double distance(CF cf)
	{
		double distance = 0;
		for(int i = 0; i < this._dimention; i++)
		{
			double difference = (this.getLS()[i] / this.getN()) - (cf.getLS()[i] / cf.getN());
			distance += Math.pow(difference, 2);
		}
		return Math.sqrt(distance);
	}
	
	public CF getClone()
	{
		CF clone = new CF(this._dimention);
		clone.setN(this._n);
		clone.setLS(this._LS.clone());
		clone.setSS(this._SS.clone());
		return clone;
	}

	/* GyS */
	public int getDimention() { return this._dimention; }
	public int getN() { return this._n; }
	public void setN(int n) { this._n = n; }
	public double[] getLS() { return this._LS; }
	public void setLS(double[] LS) { this._LS = LS; }
	public double[] getSS() { return this._SS; }
	public void setSS(double[] SS) { this._SS = SS; }
	/* End GyS */
	
	/* Overrides */
	@Override
	public String toString() { return "[" + this._n + ", " + Arrays.toString(this._LS) + ", " + Arrays.toString(this._SS) + "]"; }
	/* End Overrides */
}
