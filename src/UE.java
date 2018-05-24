import java.io.Serializable;

public class UE implements Serializable
{
	double[] demand;
	double maxLatency;
	
	double[] latency;
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
	public void showLatency()
	{
		for(int i = 0; i < getLatency().length; i++)
		{
			System.out.printf("%.0f ", getLatency()[i]);
		}
		System.out.println();
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
	public void setProposeTo(int p)
	{
		proposeTo = p;
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
	public void showPreferenceCount()
	{
		System.out.printf(" %d", getPreferenceCount());
	}
	public void setAccept(boolean a)
	{
		accept = a;
	}
	public boolean getAccept()
	{
		return accept;
	}
	public void showAccept()
	{
		System.out.print(getAccept() ? " 1" : " 0");
	}
	public int checkTheCount(int server)
	{
		int count = 1;
		for(int i = 0; i < getPreference().length; i++)
		{
			if(getPreference()[i] == server || getPreference()[i] == -1)
				break;
			else
				count++;
		}
		return count;
	}
	public void showPreference()
	{
		for(int i = 0; i < getPreference().length; i++)
		{
			System.out.printf("%d ", getPreference()[i]);
		}
		System.out.println();
	}
}
