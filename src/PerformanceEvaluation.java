import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceEvaluation 
{
	public static double averageOfPreference(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		int count = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			count = count + (ueList.get(i).getPreferenceCount() + 1);
		}
		System.out.println("Sum of preference = " + count);
		result = (double)count/ueList.size();
		System.out.println("Average of preference = " + result);
		return result;
	}
	public static double standardDeviationOfPreference(List<UE> ueList, List<Server> serverList)
	{
		List<Double> array = new ArrayList<>();
		for(int i = 0; i < ueList.size(); i++)
		{
			array.add((double)ueList.get(i).getPreferenceCount());
		}
		double result = Function.calculateSD(array);
		System.out.printf("SD of preference = %.2f\n", result);
		return result;
	}
	public static double balanceIndexOfServers(List<UE> ueList, List<Server> serverList)
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
		System.out.printf("overall latency = %.0f, count = %d, average latency = %.2f\n", overallLatency, acceptedUECount, result);
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
	public static double standardDeviationOfServedLatency(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		List<Double> latency = new ArrayList<>();
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept()) // If UE i is accepted by some server.
			{
				latency.add( ueList.get(i).getLatency()[ueList.get(i).getPreferenceCount()] );
			}
		}
		double sd = Function.calculateSD(latency);
		System.out.printf("SD of served latency = %.2f\n", sd);
		return sd;
		
	}
	public static double percentageOfOutsourcing(List<UE> ueList, List<Server> serverList)
	{
		double numerator = 0;
		double denominator = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
				denominator++;
			if(ueList.get(i).getAccept() && ueList.get(i).getPreferenceCount() != 0)
				numerator++;
		}
		double result = numerator/denominator;
		System.out.printf("Number of outsourcing requests = %.0f\n"
				+ "Number of served request = %.0f\n"
				+ "Percentage of outsourcing = %.2f\n", denominator, numerator, result);
		return result;
	}
	
	public static double[] performanceAverager(List<double[]> performance)
	{
		double[] result = new double[performance.get(0).length];
		for(int i = 0; i < result.length; i++)
		{
			result[i] = 0;
		}
		
		for(int i = 0; i < result.length; i++)
		{
			for(int j = 0; j < performance.size(); j++)
			{
				result[i] = result[i] + performance.get(j)[i];
			}
			result[i] = result[i] / performance.size();
		}
		return result;
	}
	public static void performanceOutputFile(int UE, int server, int algo, double[] averagedPerformanceArray) throws IOException
	{
		// Output settings.
		String algoString;
		switch(algo)
		{
			case 0:
				algoString = "DA";
				break;
			case 1:
				algoString = "Random";
				break;
			case 2:
				algoString = "Boston";
				break;
			case 3:
				algoString = "WOIntra";
				break;
			default:
				algoString = "--";
				break;
		}
		
		String outputDirectory = "performance/" + algoString + "/" ;
		String outputFile = "UE" + String.valueOf(UE) + "-" + "server" + String.valueOf(server) + ".csv";		
		
		File outputDir = new File(outputDirectory);
		if (!outputDir.exists())	outputDir.mkdir();	    
		String outputPath = outputDirectory + outputFile;
		
		FileWriter w = new FileWriter(outputPath);
		BufferedWriter bw = new BufferedWriter(w);
		
		String line = UE + "," + server + ",";
		
		for(int i = 0; i < averagedPerformanceArray.length; i++)
		{
			line = line + averagedPerformanceArray[i];
			if(i < averagedPerformanceArray.length - 1)
				line = line + ",";
		}
		System.out.println(line);
		bw.write(line);
		bw.close();
	}
}
