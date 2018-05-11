import java.util.List;

public class PerformanceEvaluation 
{
	public static double sumOfPreference(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		int count = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			count = count + (ueList.get(i).getPreferenceCount() + 1);
		}
		result = (double)count/ueList.size();
		System.out.println("Sum of preference = " + result);
		return result;
	}
	public static double balanceIndex(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		double numerator = 0;
		double denominator = 0;
		for(int i = 0; i < serverList.size(); i++)
		{
			numerator = numerator + (serverList.get(i).getUsed()[0]);
			denominator = denominator + (serverList.get(i).getUsed()[0]) * (serverList.get(i).getUsed()[0]);
		}	
		numerator = numerator * numerator;
		denominator = denominator * serverList.size();
		result = numerator/denominator;
		System.out.println("Balance index = " + result);
		return result;
	}
	public static double averageLatency(List<UE> ueList, List<Server> serverList)
	{
		for(int i = 0; i < ueList.size(); i++)
		{
			System.out.printf("UE %d's latency list: ", i);
			ueList.get(i).showLatency();
		}
		System.out.printf("Accept boolean  : ");
		for(int i = 0; i < ueList.size(); i++)
		{
			ueList.get(i).showAccept();
		}
		System.out.println();
		System.out.printf("Preference index: ");
		for(int i = 0; i < ueList.size(); i++)
		{
			ueList.get(i).showPreferenceCount();
		}
		System.out.println();
		System.out.printf("Server number   : ");
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
				System.out.print(" " + ueList.get(i).getProposeTo());
			else
				System.out.print(ueList.get(i).getProposeTo());
		}
		System.out.println();
		double overallLatency = 0;
		int acceptedUECount = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
//				System.out.printf("%.0f ", ueList.get(i).getLatency()[ueList.get(i).getPreferenceCount()]);
				overallLatency += ueList.get(i).getLatency()[ueList.get(i).getPreferenceCount()];
				acceptedUECount++;
			}		
		}
		
		double result = overallLatency / acceptedUECount;
		System.out.printf("overall latency = %.0f, count = %d, result = %.2f\n", overallLatency, acceptedUECount, result);
		return result;
	}
	public static double avergeServedUEs(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		double count = 0;
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d's served UE List size = %d\n", i, serverList.get(i).getServedUEList().size());
			count += serverList.get(i).getServedUEList().size();
		}
		result = count / serverList.size();
		System.out.printf("Count = %.0f, serverListSize = %d, Average served UE = %.2f\n", count, serverList.size(), result);
		return result;
	}
}