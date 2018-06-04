import java.io.Serializable;
import java.util.*;

public class Server implements Serializable
{
	double[] capacity;
	int[] preference;
	
	double[] weight;
	double alpha;
	
	// for Random
	double[] used;
	
	List<UE> servedUEList = new ArrayList<>();
	
	// for inter-offloading
	double[] costArray;
	double[] utilityArray;
	double[] orderArrayOfIndexOfUEs;
 	
	// Constructor for intra-offloading
	public Server(double[] capacity)
	{
		setCapacity(capacity);
		setWeight(1);
		setAlpha(1);
		setUsed();
	}
	
	// Constructor for inter-offloading
	public Server(double[] capacity, double[] cost)
	{
		setCapacity(capacity);
		setWeight(1);
		setAlpha(1);
		setUsed();
		setCostArray(cost);
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
	public void showPreference()
	{
		for(int i = 0; i < getPreference().length; i++)
		{
			System.out.printf("%d ", getPreference()[i]);
		}
		System.out.println();
	}
	public void addServedUEList(UE ue)
	{
		servedUEList.add(ue);
	}
	public List<UE> getServedUEList()
	{
		return servedUEList;
	}
	
	// below for inter-offloading
	public void setCostArray(double[] c)
	{
		costArray = c;
	}
	public double[] getCostArray()
	{
		return costArray;
	}
	public void setUtilityArray(List<UE> ueList, int whichServer)
	{
		utilityArray = new double[ueList.size()];
		refreshUtilityArray(ueList, whichServer);
	}
	public double[] getUtilityArray()
	{
		return utilityArray;
	}
	public void refreshUtilityArray(List<UE> ueList, int whichServer)
	{
		for(int i = 0; i < utilityArray.length; i++)
		{
//			utilityArray[i] = ueList.get(i).getBidArray()[whichServer] 
//							- costValueOfDemand(ueList.get(i).getDemand())
//							- preferenceFunction(weight, ueList.get(i).getDemand(), capacity, alpha);
			utilityArray[i] = ( ueList.get(i).getBidArray()[whichServer] - costValueOfDemand(ueList.get(i).getDemand()) ) 
							/ preferenceFunction(weight, ueList.get(i).getDemand(), capacity, alpha);
		}
	}
	public void showUtilityArray(int s)
	{
		System.out.printf("The utility of Server %d: ", s);
		for(int i = 0; i < utilityArray.length; i++)
		{
			System.out.printf("%.2f, ", utilityArray[i]);
		}
		System.out.println();
	}
	public double costValueOfDemand(double[] demand)
	{
		return demand[0] * getCostArray()[0] + demand[1] * getCostArray()[1] + demand[2] * getCostArray()[2];
	}
	public void setOrderArrayOfIndexOfUEs()
	{
		orderArrayOfIndexOfUEs = new double[getUtilityArray().length];
		for(int i = 0; i < orderArrayOfIndexOfUEs.length; i++)
		{
			orderArrayOfIndexOfUEs[i] = i;
		}
		double[] tempUtilityArray = new double[utilityArray.length];
		System.arraycopy(utilityArray, 0, tempUtilityArray, 0, utilityArray.length);
		Function.inverseMergeSort(tempUtilityArray, orderArrayOfIndexOfUEs);
	}
	public double[] getOrderArrayOfIndexOfUEs()
	{
		return orderArrayOfIndexOfUEs;
	}
	public void showOrderArrayOfIndexOfUEs(int s)
	{
		System.out.printf("The server %d's order array: ", s);
		for(int i = 0; i < orderArrayOfIndexOfUEs.length; i++)
		{
			System.out.printf("%.0f, ", orderArrayOfIndexOfUEs[i]);
		}
		System.out.println();
	}
}
