import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class PerformanceEvaluation2 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		bulkSetDataProcessor();
//		oneSetDataProcessor(50, 10, 0, 0); // UE, server, ordinal, algo
	}
	public static void bulkSetDataProcessor() throws ClassNotFoundException, IOException
	{
		int[] UERange = { 50, 1000 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int numberOfSetForEachUE = 10;
		
		for(int algo = 0; algo <= 2; algo++)
		{
			for(int server = serverRange[0]; server <= serverRange[1]; server = server + serverInterval)
			{	
				for(int UE = UERange[0]; UE <= UERange[1]; UE = UE + UEInterval)
				{
					List<double[]> performance = new ArrayList<>();
					List<double[]> barChartPerformance = new ArrayList<>();
					List<double[]> latencyBarChartPerformance = new ArrayList<>();
					for(int ordinal = 0; ordinal < numberOfSetForEachUE; ordinal++)
					{
						List<double[]> p = oneSetDataProcessor(UE, server, ordinal, algo);
						double[] onePerformanceArray = p.get(0);
						double[] oneBarChartArray = p.get(1);
						double[] oneLatencyBarChartArray = p.get(2);
						performance.add(onePerformanceArray);
						barChartPerformance.add(oneBarChartArray);
						latencyBarChartPerformance.add(oneLatencyBarChartArray);
					}
					double[] averagedPerformanceArray = PerformanceEvaluation.performanceAverager(performance);
					double[] averagedBarChartArray = PerformanceEvaluation.performanceAverager(barChartPerformance);
					double[] averagedLatencyBarChartArray = PerformanceEvaluation.performanceAverager(latencyBarChartPerformance);
					performanceOutputFile(UE, server, algo, averagedPerformanceArray);
					barChartPerformanceOutputFile(UE, server, algo, averagedBarChartArray);
					latencyBarChartPerformanceOutputFile(UE, server, algo, averagedLatencyBarChartArray);
				}
			}	
		}
	}
	
	public static List<double[]> oneSetDataProcessor(int ue, int server, int ordinal, int algo) throws ClassNotFoundException, IOException
	{
		List<double[]> multiPerformance = new ArrayList<>(); // For performance array and bar chart array
		
		List<UE> ueList = ueListReader(ue, server, ordinal, algo);
		List<Server> serverList = serverListReader(ue, server, ordinal, algo);
		
		double[] performance = new double[10];
		performance[0] = averageOfPreference(ueList, serverList);
		performance[1] = standardDeviationOfPreference(ueList, serverList);
		performance[2] = balanceIndexOfServers(ueList, serverList);
		performance[3] = averageLatencyForInter(ueList, serverList); // Only for inter
		performance[4] = avergeServedUEs(ueList, serverList);
		performance[5] = standardDeviationOfServedLatencyForInter(ueList, serverList); // Only for inter
		performance[6] = 0; //percentageOfOutsourcing(ueList, serverList);
		performance[7] = averageOfPreferenceOfAcceptUEs(ueList, serverList);
		performance[8] = standardDeviationOfPreferenceOfAcceptedUEs(ueList, serverList);
		performance[9] = averageRevenueOfTheServers(ueList, serverList);
		multiPerformance.add(performance);
		
		double[] barChartPerformance = preferenceCountDistributionForInter(ueList, serverList);  //useless for inter
		multiPerformance.add(barChartPerformance);	//useless for inter
		
		double[] latencyBarChartPerformance = latencyCountDistributionForInter(ueList, serverList);
		multiPerformance.add(latencyBarChartPerformance);
		
		return multiPerformance;	
	}
	
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
	public static double averageLatencyForInter(List<UE> ueList, List<Server> serverList)
	{
		double overallLatency = 0;
		int acceptedUECount = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				overallLatency += ueList.get(i).getLatency()[ueList.get(i).getProposeTo()];
				acceptedUECount++;
			}
		}
		return overallLatency / acceptedUECount;
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
	public static double standardDeviationOfServedLatencyForInter(List<UE> ueList, List<Server> serverList)
	{
		List<Double> latency = new ArrayList<>();
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept()) // If UE i is accepted by some server.
			{
				latency.add( ueList.get(i).getLatency()[ueList.get(i).getProposeTo()] );
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
		System.out.printf("Number of served requests = %.0f\n"
				+ "Number of outsourcing requests = %.0f\n"
				+ "Percentage of outsourcing = %.2f\n", denominator, numerator, result);
		return result;
	}
	public static double averageOfPreferenceOfAcceptUEs(List<UE> ueList, List<Server> serverList)
	{
		double result = 0;
		int count = 0;
		int numberOfAcceptedUEs = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				count = count + (ueList.get(i).getPreferenceCount() + 1);
				numberOfAcceptedUEs++;
			}
		}
		System.out.println("Sum of preference of accepted UEs= " + count);
		result = (double)count/numberOfAcceptedUEs;
		System.out.println("Average of preference of accepted UEs = " + result);
		return result;
	}
	public static double standardDeviationOfPreferenceOfAcceptedUEs(List<UE> ueList, List<Server> serverList)
	{
		List<Double> array = new ArrayList<>();
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				array.add((double)ueList.get(i).getPreferenceCount());	
			}
		}
		double result = Function.calculateSD(array);
		System.out.printf("SD of preference of accepted UEs = %.2f\n", result);
		return result;
	}
	public static double averageRevenueOfTheServers(List<UE> ueList, List<Server> serverList)
	{
		double total = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				total += ueList.get(i).getBidArray()[ueList.get(i).getProposeTo()];
			}
		}
		return total/serverList.size();
	}
	
	
	public static double[] preferenceCountDistribution(List<UE> ueList, List<Server> serverList)
	{
		double[] count = new double[serverList.size() + 1];
		for(int i = 0; i < count.length; i++)	
			count[i] = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
				count[ueList.get(i).getPreferenceCount()] += 1;
//			else
//				count[count.length-1] += 1;
		}
		return count;
	}
	public static double[] preferenceCountDistributionForInter(List<UE> ueList, List<Server> serverList)
	{
		double[] count = new double[serverList.size() + 1];
		for(int i = 0; i < count.length; i++)	
			count[i] = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
				count[0] += 0;
//			else
//				count[count.length-1] += 1;
		}
		return count;
	}
	
	
	public static double[] latencyCountDistribution(List<UE> ueList, List<Server> serverList)
	{
		double[] count = new double[20];
		for(int i = 0; i < count.length; i++) count[i] = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				double latency = ueList.get(i).getLatency()[ueList.get(i).getPreferenceCount()];
				count[(int)(latency/10)] += 1;
			}
		}
		return count;
	}
	public static double[] latencyCountDistributionForInter(List<UE> ueList, List<Server> serverList)
	{
		double[] count = new double[20];
		for(int i = 0; i < count.length; i++) count[i] = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				double latency = ueList.get(i).getLatency()[ueList.get(i).getProposeTo()];
				count[(int)(latency/10)] += 1;
			}
		}
		return count;
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
		
		String outputDirectory = "performance/" + "inter/" + algoString + "/" ;
		String outputFile = "UE" + String.valueOf(UE) + "-" + "server" + String.valueOf(server) + ".csv";		

		Function.checkDirectoryWhetherExist(outputDirectory);
//		File outputDir = new File(outputDirectory);
//		if (!outputDir.exists())	outputDir.mkdir();	    
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
	public static void barChartPerformanceOutputFile(int UE, int server, int algo, double[] averagedPerformanceArray) throws IOException
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
		
		String outputDirectory = "performance/" + "inter/" + algoString + "/" ;
		String outputFile = "UE" + String.valueOf(UE) + "-" + "server" + String.valueOf(server) + "-" + "BarChart"+ ".csv";		
		
		Function.checkDirectoryWhetherExist(outputDirectory);
//		File outputDir = new File(outputDirectory);
//		if (!outputDir.exists())	outputDir.mkdir();	    
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
	public static void latencyBarChartPerformanceOutputFile(int UE, int server, int algo, double[] averagedPerformanceArray) throws IOException
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
		
		String outputDirectory = "performance/" + "inter" + "/" + algoString + "/" ;
		String outputFile = "UE" + String.valueOf(UE) + "-" + "server" + String.valueOf(server) + "-" + "LatencyBarChart"+ ".csv";		
		
		Function.checkDirectoryWhetherExist(outputDirectory);
//		File outputDir = new File(outputDirectory);
//		if (!outputDir.exists())	outputDir.mkdir();	    
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

	public static List<UE> ueListReader(int UE, int server, int ordinal, int algo) throws IOException, ClassNotFoundException
	{
		String algoString = Function.algoNumberToAlgoStream(algo);
		String inputUEFilePath = "output/" + "inter" + "/" + algoString + "/" + "UE" + UE + "-" + "server" + server + "/" + "UE" + ordinal + ".ue";

		FileInputStream fisUE = new FileInputStream(inputUEFilePath);
		ObjectInputStream oisUE = new ObjectInputStream(fisUE);
		
		List<UE> ueList = (List<UE>) oisUE.readObject();
		oisUE.close();
		
		return ueList;
	}
	public static List<Server> serverListReader(int UE, int server, int ordinal, int algo) throws IOException, ClassNotFoundException
	{
		String algoString = Function.algoNumberToAlgoStream(algo);
		String inputUEFilePath = "output/" + "inter" + "/" + algoString + "/" + "UE" + UE + "-" + "server" + server + "/" + "server" + ordinal + ".server";

		FileInputStream fisServer = new FileInputStream(inputUEFilePath);
		ObjectInputStream oisServer = new ObjectInputStream(fisServer);
		
		List<Server> serverList = (List<Server>) oisServer.readObject();
		oisServer.close();
		
		return serverList;
	}
}
