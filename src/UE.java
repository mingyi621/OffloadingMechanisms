
public class UE 
{
	double[] demand;
	double[] latency;
	double maxLatency;
	
	int[] preference;
	int proposeTo = -1;
	int preferenceCount = -1;
	
	boolean accept = false;
	
	public UE(double[] d, double max)
	{
		setDemand(d);
		setMaximumLatency(max);
	}
	
	public void setDemand(double[] d)
	{
		demand = new double[d.length];
		for(int i = 0; i < d.length; i++)
		{
			demand[i] = d[i];
		}
	}
	public double[] getDemand()
	{
		return demand;
	}	
	public void setMaximumLatency(double m)
	{
		maxLatency = m;
	}
	public double getMaximumLatency()
	{
		return maxLatency;
	}
	public void setLatency(double[] l)
	{
		latency = new double[l.length];
		for(int i = 0; i < l.length; i++)
			latency[i] = l[i]; 
	}
	public double[] getLatency()
	{
		return latency;
	}
	public void setPreference()
	{
		preference = new int[getLatency().length];
		double[] index = new double[getLatency().length];
		for(int i = 0; i < getLatency().length; i++)
		{
			index[i] = i;
		}
		Function.mergeSort(getLatency(),index);
		for(int i = 0; i < getLatency().length; i++)
		{
			if(getLatency()[i]>getMaximumLatency())
				index[i] = -1;
			preference[i] = (int)index[i];
		}
	}
	public int[] getPreference()
	{
		return preference;
	}
	public void setProposeTo()
	{
		setPreferenceCount(1);
		if(getPreferenceCount()<getPreference().length)
			proposeTo = getPreference()[getPreferenceCount()];
		else
			proposeTo = -1;
		setAccept(false);
	}
	public int getProposeTo()
	{
		return proposeTo;
	}
	public void setPreferenceCount(int c)
	{
		preferenceCount += c;
	}
	public int getPreferenceCount()
	{
		return preferenceCount;
	}
	public void setAccept(boolean a)
	{
		accept = a;
	}
	public boolean getAccept()
	{
		return accept;
	}
}
