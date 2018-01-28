package ronald.tesis.clustering.birch;

import java.io.Serializable;

public abstract class Config implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static double T = 5;
	public static int B = 5;
	public static int L = 5;
	public static String folderStreaming = "streaming";
}
