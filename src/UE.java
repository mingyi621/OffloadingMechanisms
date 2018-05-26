import java.io.Serializable;
import java.util.List;

public class UE implements Serializable
{
	double[] demand;
	double maxLatency;
	
	double[] latency;
	int[] preference;
	int proposeTo = -1;
	int preferenceCount = -1;
	
	boolean accept = false;
	
	// For inter-offloading
	double valuation;
	double[] askArray;
	double[] bidArray;
	double[] utilityArray;
	
	// Constructor for intra-offloading
	public UE(double[] d, double max)
	{
		setDemand(d);
		setMaximumLatency(max);
	}
	// Construtor for inter-offloading
	public UE(double[] d, double max, double value)
	{
		setDemand(d);
		setMaximumLatency(max);
		setValuation(value);
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
	
	// below for inter-offloading
	public void setValuation(double v)
	{
		valuation = v;
	}
	public double getValuation()
	{
		return valuation;
	}
	public void setAskArray(double[] a)
	{
		askArray = a;
	}
	public void setAskArray(List<Server> serverList)
	{
		double[] askArray = new double[serverList.size()];
		for(int i = 0; i < serverList.size(); i++)
		{
			double[] costArray = serverList.get(i).getCostArray();
			double[] demand = getDemand();
			askArray[i] = demand[0] * costArray[0] + demand[1] * costArray[1] + demand[2] * costArray[2];			
		}
		setAskArray(askArray);
	}
	public double[] getAskArray()
	{
		return askArray;
	}
	public void setBidArray(double[] b)
	{
		bidArray = b;
	}
	public double[] getBidArray()
	{
		return bidArray;
	}
	public void setUtilityArray(double[] u)
	{
		utilityArray = u;
	}
	public void initializeUtilityArray()
	{
		utilityArray = new double[getBidArray().length];
		for(int i = 0; i < utilityArray.length; i++)
		{	
			int w = 1;
			utilityArray[i] = getValuation() - w * getLatency()[i] - getBidArray()[i];
		}
	}
	public double[] getUtilityArray()
	{
		return utilityArray;
	}
}
