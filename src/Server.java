import java.util.*;

public class Server 
{
	double[] capacity;
	int[] preference;
	
	double[] weight;
	double alpha;
	
	// for Random
	double[] used;
	
	public Server(double[] c)
	{
		setCapacity(c);
		setWeight(1);
		setAlpha(1);
		setUsed();
	}
	public void setCapacity(double[] c)
	{
		capacity = new double[c.length];
		for(int i = 0; i < c.length; i++)
		{
			capacity[i] = c[i];
		}
	}
	public double[] getCapacity()
	{
		return capacity;
	}
	
	public void setPreference(UE[] u)
	{
		double[] p = new double[u.length];
		preference = new int[u.length];
		for(int i = 0; i < u.length; i++)
		{
			p[i] = preferenceFunction(weight, u[i].getDemand(), capacity, alpha);
		}
		double[] index = new double[u.length];
		for(int i = 0; i < u.length; i++)
		{
			index[i] = i;
		}
		Function.mergeSort(p,index);
		for(int i = 0; i < u.length; i++)
		{
			preference[i] = (int)index[i];
		}
		
	}
	public void setPreference(List<UE> ueList)
	{
		double[] p = new double[ueList.size()];
		preference = new int[ueList.size()];
		for(int i = 0; i < ueList.size(); i++)
		{
			p[i] = preferenceFunction(weight, ueList.get(i).getDemand(), capacity, alpha);
		}
		double[] index = new double[ueList.size()];
		for(int i = 0; i < ueList.size(); i++)
		{
			index[i] = i;
		}
		Function.mergeSort(p, index);
		for(int i = 0; i < ueList.size(); i++)
		{
			preference[i] = (int)index[i];
		}
		
	}
	public int[] getPreference()
	{
		return preference;
	}
	
	public double preferenceFunction(double[] w, double[] d, double[] c, double alpha)
	{
		double p = 0;
		for(int r = 0; r < c.length; r++)
		{
			p = p + w[r]* Math.pow(d[r]/c[r], alpha);
		}
		return p;
	}
	
	public void setWeight(int w)
	{
		weight = new double[capacity.length];
		for(int i = 0; i < capacity.length; i++)
			weight[i] = w;
	}
	public double[] getWeight()
	{
		return weight;
	}
	public void setAlpha(double i)
	{
		alpha = i;
	}
	public double getAlpha()
	{
		return alpha;
	}
	
	//For Random
	public void setUsed()
	{
		used = new double[getCapacity().length];
		for(int i = 0; i < used.length; i++)
		{
			used[i] = 0;
		}
	}
	public void setUsed(double[] demand)
	{
		for(int i = 0; i < demand.length; i++)
		{
			used[i] += demand[i];
		}
	}
	public double[] getUsed()
	{
		return used;
	}
	public boolean checkWhetherExceedCapacity(double[] demand)
	{
		boolean result = false;
		for(int i = 0; i < demand.length; i++)
		{
			if(used[i]+demand[i]>capacity[i])
			{
				result = true;
				break;
			}
		}
		return result;
	}
}
