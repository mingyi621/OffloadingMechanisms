import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class Main2 {

	public static void main(String[] args) throws IOException
	{
		bulkSetDataProcessor();
//		oneSetDataProcessor(500, 9, 2, 0); // UE, server, ordinal, algo
	}
	public static void bulkSetDataProcessor() throws IOException
	{
		int[] UERange = { 50, 1000 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int numberOfSetForEachUE = 1;
		
		for(int algo = 0; algo <= 2; algo++)
		{
			for(int server = serverRange[0]; server <= serverRange[1]; server = server + serverInterval)
			{	
				for(int UE = UERange[0]; UE <= UERange[1]; UE = UE + UEInterval)
				{
					for(int ordinal = 0; ordinal < numberOfSetForEachUE; ordinal++)
					{
						oneSetDataProcessor(UE, server, ordinal, algo);
					}
				}
			}	
		}
	}
	public static void oneSetDataProcessor(int UE, int server, int ordinal, int algo) throws IOException
	{
		// Basic input settings.
//		int UE = 40;
//		int server = 4;
//		int ordinal = 1;
//		int algo = 0; 
		
		String algoString = Function.algoNumberToAlgoStream(algo);
		
		String inputUEPath = "input/" + "UE/" + UE + "/" + "UE" + ordinal + ".csv";
		String inputServerPath = "input/" + "server/" + server + "/" + "server" + ordinal + ".csv";
		String inputLatencyPath = "input/" + "latency/" + "UE" + UE + "-" + "server" + server + "/" + "latency" + ordinal +".csv";
		
		// Output settings.
		String outputDirectory = "output/" + "inter" + "/" + algoString + "/" +  "UE" + UE + "-" + "server" + server + "/" ;
		String outputFile = "output" + ordinal +".csv";	
		
		Function.checkDirectoryWhetherExist(outputDirectory);    
	    String outputPath = outputDirectory + outputFile;
		
	    // Read UE and server files.
		List<UE> ueList = ReadUE(inputUEPath);
		List<Server> serverList = ReadServer(inputServerPath);
		
		// 1. Set Latency from OCS
		FileReader f = new FileReader(inputLatencyPath);
		BufferedReader b = new BufferedReader(f);
		for(int i = 0; i < ueList.size(); i++)
		{
			String[] record = b.readLine().split(",");
			double[] latency = new double[record.length];
			for(int j = 0; j < latency.length; j++)
			{
				latency[j] = Double.parseDouble(record[j]);
			}
			ueList.get(i).setLatency(latency);
		}
		
		// For inter: 1.1 Set Ask Array from the Servers, and initialize the bidArray as the askArray, and the utilityArray of UEs
		for(int i = 0; i < ueList.size(); i++)
		{
			ueList.get(i).setAskArray(serverList);
			double[] bidArray = new double[serverList.size()];
			System.arraycopy(ueList.get(i).getAskArray(), 0, bidArray, 0, ueList.get(i).getAskArray().length);
			ueList.get(i).setBidArray(bidArray);
			ueList.get(i).initializeUtilityArray();
		}
		
		// For inter: 1.2 Set utilityArray of the servers.
		for(int i = 0; i < serverList.size(); i++)
		{
			serverList.get(i).setUtilityArray(ueList, i);
			serverList.get(i).showUtilityArray(i);
		}
		
//		// 2. Set UEs' Preference List
//		for(int i = 0; i < ueList.size(); i++)
//		{
//			ueList.get(i).setPreference();
//			System.out.printf("Preference list of UE %d: ",i);
//			ueList.get(i).showPreference();
//		}
//		
//		
//		// 3. Set Servers' Preference List
//		for(int i = 0; i < serverList.size(); i++)
//		{
//			serverList.get(i).setPreference(ueList);
//			System.out.printf("Preference list of server %d: ",i);
//			serverList.get(i).showPreference();
//		}
		
		switch(algo)
		{
			case 0:
				deferredAcceptanceAlgorithmWithTransfer(ueList, serverList);
				break;
		
			case 1:
				RandomAlgorithmWithTransfer2(ueList, serverList);
				break;
				
			case 2:
				BostonMechanismWithTransfer(ueList, serverList);
				break;
		
			case 3:	
				WithoutOutsourcing(ueList, serverList);
				break;
				
			default:
				break;
		}
		// Write To File
		WriteToFile(ueList, serverList, outputPath);
		outputObjectToFile(ueList, serverList, outputDirectory, ordinal);
	}
	public static List<UE> ReadUE(String file) throws IOException
	{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		List<UE> list = new ArrayList<>();
		
		String line = null;
		double[] latency;
		while ((line = br.readLine()) != null) 
		{
			String[] record = line.split(",");
			double cpu = Double.parseDouble(record[0]);
			double memory = Double.parseDouble(record[1]);
			double storage = Double.parseDouble(record[2]);
			double maxLatency = Double.parseDouble(record[3]);
			double valuation = Double.parseDouble(record[4]);
			double[] demand = {cpu, memory, storage};
			UE ue = new UE(demand, maxLatency, valuation);
			
			list.add(ue);
		}
		return list;	
	}
	public static List<Server> ReadServer(String file) throws IOException
	{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		List<Server> list = new ArrayList<>();
		
		String line = null;
		while ((line = br.readLine()) != null) 
		{
			String[] record = line.split(",");
			double cpu = Double.parseDouble(record[0]);
			double memory = Double.parseDouble(record[1]);
			double storage = Double.parseDouble(record[2]);
			double[] capacity = {cpu, memory, storage};
			double costPerCpu = Double.parseDouble(record[3]);
			double costPerMemory = Double.parseDouble(record[4]);
			double costPerStorage = Double.parseDouble(record[5]);
			double[] costPerResourceArray = {costPerCpu, costPerMemory, costPerStorage};
			Server server = new Server(capacity, costPerResourceArray);
			list.add(server);
		}
		
		return list;
	}
	public static void deferredAcceptanceAlgorithm(List<UE> ueList, List<Server> serverList)
	{
		boolean globalRejection;
		do
		{
			globalRejection = false;
			// 4. UEs propose to the first preferred server.
			for(int i = 0; i < ueList.size(); i++)
			{
				if((ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() == -1)
						||	(ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() > -1 && ueList.get(i).getProposeTo() != -1))
				{
					ueList.get(i).setProposeTo();
					System.out.printf("UE %d proposes to server %d.\n", i, ueList.get(i).getProposeTo());
				}
			}
			// 5. The Servers check the preference list from top to down,
			// 	  if the ith UE proposed, check its demand,
			//	  if smaller than capacity, continue, until exceed capacity
					
			for(int i = 0; i < serverList.size(); i++)
			{
				double[] used = new double[serverList.get(i).getCapacity().length];
				for(int j = 0; j < used.length; j++)
				{
					used[j] = 0;
					serverList.get(i).setUsed();
				}
				boolean accept = true;
				for(int j = 0; j < ueList.size(); j++)
				{
					if(ueList.get(serverList.get(i).getPreference()[j]).getProposeTo() == i)
					{
						System.out.printf("algo: Server %d got propose from UE %d. ", i, serverList.get(i).getPreference()[j]);
						if(accept)
						{
							for(int k = 0; k < serverList.get(i).getCapacity().length; k++)
							{
								if(used[k] + ueList.get(serverList.get(i).getPreference()[j]).getDemand()[k] > serverList.get(i).getCapacity()[k])
								{
									accept = false;
									globalRejection = true;
									System.out.println("\n The capacity is full.");
									break;
								}
							}
						}
						if(accept)
						{
							for(int k = 0; k < serverList.get(i).getCapacity().length; k++)
							{
								used[k] += ueList.get(serverList.get(i).getPreference()[j]).getDemand()[k];
							}
							serverList.get(i).setUsed(ueList.get(serverList.get(i).getPreference()[j]).getDemand());
							ueList.get(serverList.get(i).getPreference()[j]).setAccept(true);
							System.out.printf("UE %d is accepted by server %d.\n", serverList.get(i).getPreference()[j], i);
						}
						else
						{
							ueList.get(serverList.get(i).getPreference()[j]).setAccept(false);
							System.out.printf("UE %d is rejected by server %d.\n", serverList.get(i).getPreference()[j], i);
						}		
					}
				}
			}
		}while(globalRejection);
		
		// Set each server's servedUEList.
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d accepted UE ", i);
			for(int j = 0; j < ueList.size(); j++)
			{
				if(ueList.get(j).getAccept() == true && ueList.get(j).getProposeTo() == i)
				{
					serverList.get(i).addServedUEList(ueList.get(j));
					System.out.printf("%d ", j);
				}
			}
			System.out.println();
		}
	}
	public static void deferredAcceptanceAlgorithmWithTransfer(List<UE> ueList, List<Server> serverList)
	{
		boolean globalRejection;
//		int count = 0;
		do{
//			count ++;
			globalRejection = false;
			for(int i = 0; i < ueList.size(); i++)
			{
				if((ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() == -1))
				{
					int theBiggestIndex = ueList.get(i).checkTheBiggestIndexInUtilityArray();
					ueList.get(i).setProposeTo(theBiggestIndex);
					
					ueList.get(i).setPreferenceCount(1);
					ueList.get(i).showValuation(i);
					ueList.get(i).showBidArray(i);
					ueList.get(i).showLatency(i);
					ueList.get(i).showUtilityArray(i);	
					System.out.printf("UE %d's maximum latency: %.0f\n", i, ueList.get(i).getMaximumLatency());
					System.out.printf("UE %d propose to server %d.\n", i, theBiggestIndex);
				}
				else if(ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() > -1 && ueList.get(i).getProposeTo() != -1)
				{
					int theBiggestIndex = ueList.get(i).getProposeTo();
					double[] bidArray = ueList.get(i).getBidArray();
					
//					bidArray[theBiggestIndex] += ueList.get(i).getEpsilon(); 					// original implement by me.
					bidArray[theBiggestIndex] += ueList.get(i).getEpsilon(theBiggestIndex);		// Yen's advice.
					
					ueList.get(i).setBidArray(bidArray);
					ueList.get(i).refreshUtilityArray();
					theBiggestIndex = ueList.get(i).checkTheBiggestIndexInUtilityArray();
					ueList.get(i).setProposeTo(theBiggestIndex);
					
					ueList.get(i).setPreferenceCount(1);
					ueList.get(i).showValuation(i);
					ueList.get(i).showBidArray(i);
					ueList.get(i).showLatency(i);
					ueList.get(i).showUtilityArray(i);
					System.out.printf("UE %d's maximum latency: %.0f\n", i, ueList.get(i).getMaximumLatency());
					System.out.printf("UE %d propose to server %d.\n", i, theBiggestIndex);
				}	
			}
			for(int i = 0; i < serverList.size(); i++)
			{
				serverList.get(i).refreshUtilityArray(ueList, i);
				serverList.get(i).setOrderArrayOfIndexOfUEs();
				serverList.get(i).showOrderArrayOfIndexOfUEs(i);
				double[] used = new double[serverList.get(i).getCapacity().length];
				for(int j = 0; j < used.length; j++)
				{
					used[j] = 0;
					serverList.get(i).setUsed();
				}
				boolean accept = true;
				for(int j = 0; j < serverList.get(i).getUtilityArray().length; j++)
				{
					int proposer = (int)serverList.get(i).getOrderArrayOfIndexOfUEs()[j];
					if(ueList.get(proposer).getProposeTo() == i)
					{
						if(accept)
						{
							for(int k = 0; k < serverList.get(i).getCapacity().length; k++)
							{
								if(used[k] + ueList.get(proposer).getDemand()[k] > serverList.get(i).getCapacity()[k])
								{
									accept = false;
									globalRejection = true;
									System.out.println("\n The capacity is full.");
									break;
								}
							}
						}
						if(accept)
						{
							for(int k = 0; k < serverList.get(i).getCapacity().length; k++)
							{
								used[k] += ueList.get(proposer).getDemand()[k];
							}
							serverList.get(i).setUsed(ueList.get(proposer).getDemand());
							ueList.get(proposer).setAccept(true);
							System.out.printf("UE %d is accepted by server %d.\n", proposer, i);
						}
						else
						{
							ueList.get(proposer).setAccept(false);
							System.out.printf("UE %d is rejected by server %d.\n", proposer, i);
						}
					}
				}
			}
		}while(globalRejection);
//		}while(count <= 5);
		// Set each server's servedUEList.
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d accepted UE ", i);
			for(int j = 0; j < ueList.size(); j++)
			{
				if(ueList.get(j).getAccept() == true && ueList.get(j).getProposeTo() == i)
				{
					serverList.get(i).addServedUEList(ueList.get(j));
					System.out.printf("%d ", j);
				}
			}
			System.out.println();
		}
		
		double totalRevenue = 0;
		double totalProfit = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				int proposeTo = ueList.get(i).getProposeTo();
				double bid = ueList.get(i).getBidArray()[proposeTo];
				double ask = ueList.get(i).getAskArray()[proposeTo];
				totalRevenue += bid;
				totalProfit += (bid - ask);
			}
		}
		System.out.printf("TotalRevenue = %.2f\n", totalRevenue);
		System.out.printf("Total Profit = %.2f\n", totalProfit);
	}
	
	public static void RandomAlgorithm(List<UE> ueList, List<Server> serverList)
	{
		Random ra = new Random();
		int server = -1;
		
		// Each UE randomly selects a server while satisfy latency 
		for(int i = 0; i < ueList.size(); i++)
		{
			int count = 0;
			boolean[] triedServer = new boolean[serverList.size()];
			for(int j = 0; j < triedServer.length; j++)
			{
				triedServer[j] = false;
			}
			do{
				count++; // the number of tries.
				if(count == serverList.size() + 1) // if tries exceed the number of servers
					break;
				while(true)
				{	
					server = ra.nextInt(serverList.size());
					if(triedServer[server] == true)
						continue;
					ueList.get(i).setProposeTo(server);
					triedServer[server] = true;
					System.out.printf("UE %d propose to server %d.\n", i, server);
					break;
				}
				int c = ueList.get(i).checkTheCount(server);
				System.out.printf("c = %d\n", c);
				if(c <= ueList.get(i).getLatency().length && ueList.get(i).getLatency()[c-1] > ueList.get(i).getMaximumLatency())// do not satisfy latency
				{
					System.out.printf("Do not satisfy latency.\n");
					continue;
				}
				if(!serverList.get(server).checkWhetherExceedCapacity(ueList.get(i).getDemand())) // if not exceed capacity
				{
					serverList.get(server).setUsed(ueList.get(i).getDemand());	
					ueList.get(i).setAccept(true);
					System.out.println("Accepted.");
					ueList.get(i).setPreferenceCount(c);
					break;
				}
				else
				{
					System.out.println("Exceed Capacity.");
				}
			}while(count <= serverList.size()); // not to exceed # servers tries
			if(count == serverList.size() + 1)
			{	
				ueList.get(i).setProposeTo(-1);
				int c = ueList.get(i).checkTheCount(-1);
				ueList.get(i).setPreferenceCount(c);
			}
		}
		// Set each server's servedUEList.
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d accepted UE ", i);
			for(int j = 0; j < ueList.size(); j++)
			{
				if(ueList.get(j).getAccept() == true && ueList.get(j).getProposeTo() == i)
				{
					serverList.get(i).addServedUEList(ueList.get(j));
					System.out.printf("%d ", j);
				}
			}
			System.out.println();
		}
	}
	public static void RandomAlgorithmWithTransfer(List<UE> ueList, List<Server> serverList)  // Bad Algorithm
	{
		Random ra = new Random();
		int server = -1;
		
		boolean[][] triedServer = new boolean[ueList.size()][serverList.size()];
		boolean[][] negativeUtilityIndicator = new boolean[ueList.size()][serverList.size()];
		// Each UE randomly selects a server while satisfy latency 
		for(int i = 0; i < ueList.size(); i++)
		{
			for(int j = 0; j < serverList.size(); j++)
			{
				triedServer[i][j] = false;
				negativeUtilityIndicator[i][j] = false;
			}
		}
		boolean continueIndicator;
		boolean globalIndicator;
		int[] countNegative = new int[ueList.size()];
		for(int i = 0; i < countNegative.length; i++)	
			countNegative[i] = 0;
		do{
			globalIndicator = false;
			continueIndicator = false;
			for(int i = 0; i < ueList.size(); i++)
			{	
				boolean[] localTried = new boolean[serverList.size()];
				for(int j = 0; j < localTried.length; j++)	localTried[j] = false;
				while(countNegative[i] < serverList.size())
				{
					server = ra.nextInt(serverList.size());
					if(localTried[server] == true)
						continue;
					if(negativeUtilityIndicator[i][server] == true)
					{
						localTried[server] = true;
						countNegative[i]++;
						continue;
					}
					if(triedServer[i][server] == true)
					{
						double[] bidArray = ueList.get(i).getBidArray();
						bidArray[server] += ueList.get(i).getEpsilon();
						ueList.get(i).setBidArray(bidArray);
						ueList.get(i).refreshUtilityArray();
						if(ueList.get(i).getUtilityArray()[server] < 0)
						{
							negativeUtilityIndicator[i][server] = true;
							countNegative[i]++;
							continue;
						}
					}	
					ueList.get(i).setProposeTo(server);
					triedServer[i][server] = true;
					System.out.printf("UE %d propose to server %d.\n", i, server);
					continueIndicator = true;
					if(!serverList.get(server).checkWhetherExceedCapacity(ueList.get(i).getDemand())) // if not exceed capacity
					{
						serverList.get(server).setUsed(ueList.get(i).getDemand());	
						ueList.get(i).setAccept(true);
						System.out.println("Accepted.");
						break;
					}
					else
					{
						System.out.println("Exceed Capacity.");
						localTried[server] = true;
					}
				}		
			}
		}while(continueIndicator);
		// Set each server's servedUEList.
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d accepted UE ", i);
			for(int j = 0; j < ueList.size(); j++)
			{
				if(ueList.get(j).getAccept() == true && ueList.get(j).getProposeTo() == i)
				{
					serverList.get(i).addServedUEList(ueList.get(j));
					System.out.printf("%d ", j);
				}
			}
			System.out.println();
		}
	}
	public static void RandomAlgorithmWithTransfer2(List<UE> ueList, List<Server> serverList)
	{
		Random random = new Random();
		int server = -1;
		boolean[][] triedIndicator = new boolean[ueList.size()][serverList.size()];
		for(int i = 0; i < ueList.size(); i++)
			for(int j = 0; j < serverList.size(); j++)
				triedIndicator[i][j] = false;
		
		boolean globalContinueIndicator;
		
		do
		{
			globalContinueIndicator = false;
			for(int i = 0; i < ueList.size(); i++)
			{
				if(ueList.get(i).getAccept())  // If the UE is accepted, then continue
					continue;
				if(ueList.get(i).getAllNegativeUtility()) // If all the servers for the UE have negative utilities, then it cannot be matched.
					continue;
			
				boolean[] negativeUtilityIndicator = new boolean[serverList.size()];
				for(int j = 0; j < negativeUtilityIndicator.length; j++) negativeUtilityIndicator[j] = false;
				int countNegative = 0;
			
				// Start to choose a server.
				do{
					server = random.nextInt(serverList.size());			// Random select a server.
					if(negativeUtilityIndicator[server] == true)		// If the server has been checked, choose another server.
						continue;
					if(ueList.get(i).getUtilityArray()[server] < 0)		// If the server has negative utility to UE i, choose another server.
					{
						negativeUtilityIndicator[server] = true;
						countNegative++;
					}
					if(countNegative == serverList.size())
					break;
				}while(negativeUtilityIndicator[server] == true);
			
				if(countNegative == serverList.size())  // all the servers for this UE have negative utility, set to unmatched.
				{
					ueList.get(i).setAllNegativeUtility(true);
					continue; 	/* Still need to modify */
				}
			
				
				if(triedIndicator[i][server] == true)  // The server has been tried by this UE, but at that time, it exceed the capacity.
				{
					double[] bidArray = ueList.get(i).getBidArray();
//					bidArray[server] += ueList.get(i).getEpsilon();				// original implement by me.
					bidArray[server] += ueList.get(i).getEpsilon(server);		// Yen's advice.
					
					ueList.get(i).setBidArray(bidArray);
					ueList.get(i).refreshUtilityArray();
					if(ueList.get(i).getUtilityArray()[server] >= 0)
						ueList.get(i).setProposeTo(server);
					else
					{
						ueList.get(i).setProposeTo(-1);
						continue;
					}
				}
				else
				{
					ueList.get(i).setProposeTo(server);
				}
				
				triedIndicator[i][server] = true;
				
				ueList.get(i).showValuation(i);
				ueList.get(i).showBidArray(i);
				ueList.get(i).showLatency(i);
				ueList.get(i).showUtilityArray(i);
				System.out.printf("UE %d's maximum latency: %.0f\n", i, ueList.get(i).getMaximumLatency());
				System.out.printf("UE %d propose to server %d.\n", i, server);
				
				if(!serverList.get(server).checkWhetherExceedCapacity(ueList.get(i).getDemand()))
				{
					ueList.get(i).setAccept(true);
					System.out.printf("UE %d got accepted from server %d\n", i, server);
					serverList.get(server).setUsed(ueList.get(i).getDemand());
				}
				else
				{
					System.out.printf("Server %d exceeds the capacity for UE %d.\n", server, i);
					globalContinueIndicator = true;
				}
			}
		}while(globalContinueIndicator == true);
		
		// Set each server's servedUEList.
		for(int i = 0; i < serverList.size(); i++)
		{
			System.out.printf("Server %d accepted UE ", i);
			for(int j = 0; j < ueList.size(); j++)
			{
				if(ueList.get(j).getAccept() == true && ueList.get(j).getProposeTo() == i)
				{
					serverList.get(i).addServedUEList(ueList.get(j));
					System.out.printf("%d ", j);
				}
			}
			System.out.println();
		}
	}
	public static void BostonMechanism(List<UE> ueList, List<Server> serverList)
	{
		boolean globalRejection;
		boolean continueIndicator;
		do{
			globalRejection = false;
			continueIndicator = false;
			// Each UE propose to the first server in its preference list
			for(int i = 0; i < ueList.size(); i++)
			{
				if((ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() == -1)
					||	(ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() > -1 && ueList.get(i).getProposeTo() != -1))
				{
					ueList.get(i).setProposeTo();
					System.out.printf("UE %d propose to server %d.\n", i, ueList.get(i).getProposeTo());
					if(ueList.get(i).getProposeTo() != -1)
					{
						continueIndicator = true;
					}
				}
			}
			if(!continueIndicator)
				break;
		
			for(int i = 0; i < serverList.size(); i++)
			{
				for(int j = 0; j < serverList.get(i).getPreference().length; j++)
				{
					// If UE j is not accepted and propose to server i 
					if(ueList.get(serverList.get(i).getPreference()[j]).getAccept() == false 
							&& ueList.get(serverList.get(i).getPreference()[j]).getProposeTo() == i	)
					{
						int proposeUE = serverList.get(i).getPreference()[j];
						System.out.printf("Server %d got propose from UE %d.\n", i, proposeUE);
						// If the proposed UE's demand still not exceed server i's capacity
						if(serverList.get(i).checkWhetherExceedCapacity(ueList.get(proposeUE).getDemand()) == false)
						{
							System.out.printf("UE %d got accepted.\n", proposeUE);
							ueList.get(proposeUE).setAccept(true);
							serverList.get(i).setUsed(ueList.get(proposeUE).getDemand());
							serverList.get(i).addServedUEList(ueList.get(proposeUE));
						}
						else
						{
							globalRejection = true;
							System.out.printf("UE %d got rejected. Exceed the capacity.\n", proposeUE);
							
						}
					}
				}
			}
		}while(globalRejection);
	}
	public static void BostonMechanismWithTransfer(List<UE> ueList, List<Server> serverList)
	{
		boolean globalRejection;
		boolean continueIndicator;
		do{
			globalRejection = false;
			continueIndicator = false;
			for(int i = 0; i < ueList.size(); i++)
			{
				if((ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() == -1))
				{
					int theBiggestIndex = ueList.get(i).checkTheBiggestIndexInUtilityArray();
					ueList.get(i).setProposeTo(theBiggestIndex);
					
					ueList.get(i).setPreferenceCount(1);
					ueList.get(i).showValuation(i);
					ueList.get(i).showBidArray(i);
					ueList.get(i).showLatency(i);
					ueList.get(i).showUtilityArray(i);	
					System.out.printf("UE %d's maximum latency: %.0f\n", i, ueList.get(i).getMaximumLatency());
					System.out.printf("UE %d propose to server %d.\n", i, theBiggestIndex);
				}
				else if(ueList.get(i).getAccept() == false && ueList.get(i).getPreferenceCount() > -1 && ueList.get(i).getProposeTo() != -1)
				{
					int theBiggestIndex = ueList.get(i).getProposeTo();
					double[] bidArray = ueList.get(i).getBidArray();
					
//					bidArray[theBiggestIndex] += ueList.get(i).getEpsilon();				// original implement by me.
					bidArray[theBiggestIndex] += ueList.get(i).getEpsilon(theBiggestIndex);	// Yen's advice.
					
					ueList.get(i).setBidArray(bidArray);
					ueList.get(i).refreshUtilityArray();
					theBiggestIndex = ueList.get(i).checkTheBiggestIndexInUtilityArray();
					ueList.get(i).setProposeTo(theBiggestIndex);
					
					ueList.get(i).setPreferenceCount(1);
					ueList.get(i).showValuation(i);
					ueList.get(i).showBidArray(i);
					ueList.get(i).showLatency(i);
					ueList.get(i).showUtilityArray(i);
					System.out.printf("UE %d's maximum latency: %.0f\n", i, ueList.get(i).getMaximumLatency());
					System.out.printf("UE %d propose to server %d.\n", i, theBiggestIndex);
				}
				if(ueList.get(i).getProposeTo() != -1)
				{
					continueIndicator = true;
				}
			}
			for(int i = 0; i < serverList.size(); i++)
			{
				serverList.get(i).refreshUtilityArray(ueList, i);
				serverList.get(i).setOrderArrayOfIndexOfUEs();
				serverList.get(i).showOrderArrayOfIndexOfUEs(i);
				double[] used = new double[serverList.get(i).getCapacity().length];
				boolean accept = true;
				for(int j = 0; j < serverList.get(i).getUtilityArray().length; j++)
				{
					int proposer = (int)serverList.get(i).getOrderArrayOfIndexOfUEs()[j];
					if(ueList.get(proposer).getAccept() == false && ueList.get(proposer).getProposeTo() == i)
					{
						if(serverList.get(i).checkWhetherExceedCapacity(ueList.get(proposer).getDemand()) == false)
						{
							System.out.printf("UE %d got accepted.\n", proposer);
							ueList.get(proposer).setAccept(true);
							serverList.get(i).setUsed(ueList.get(proposer).getDemand());
							serverList.get(i).addServedUEList(ueList.get(proposer));
						}
						else
						{
							globalRejection = true;
							System.out.printf("UE %d got rejected. Exceed the capacity.\n", proposer);	
						}
					}
				}
			}
		}while(globalRejection);
		double totalRevenue = 0;
		double totalProfit = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				int proposeTo = ueList.get(i).getProposeTo();
				double bid = ueList.get(i).getBidArray()[proposeTo];
				double ask = ueList.get(i).getAskArray()[proposeTo];
				totalRevenue += bid;
				totalProfit += (bid - ask);
			}
		}
		System.out.printf("TotalRevenue = %.2f\n", totalRevenue);
		System.out.printf("Total Profit = %.2f\n", totalProfit);
	}
	public static void WithoutOutsourcing(List<UE> ueList, List<Server> serverList)
	{
		for(int i = 0; i < ueList.size(); i++)
		{
			ueList.get(i).setProposeTo();
			System.out.printf("UE %d propose to server %d.\n", i, ueList.get(i).getProposeTo());
		}
		for(int i = 0; i < serverList.size(); i++)
		{
			for(int j = 0; j < serverList.get(i).getPreference().length; j++)
			{
				if(ueList.get(serverList.get(i).getPreference()[j]).getProposeTo() == i)
				{
					System.out.printf("Server %d got propose from UE %d.\n", i, serverList.get(i).getPreference()[j]);
					if(serverList.get(i).checkWhetherExceedCapacity(ueList.get(serverList.get(i).getPreference()[j]).getDemand()) == false)
					{
						System.out.printf("UE %d got accepted.\n", serverList.get(i).getPreference()[j]);
						ueList.get(serverList.get(i).getPreference()[j]).setAccept(true);
						serverList.get(i).setUsed(ueList.get(serverList.get(i).getPreference()[j]).getDemand());
						serverList.get(i).addServedUEList(ueList.get(serverList.get(i).getPreference()[j]));
					}
					else
					{
						System.out.println("Exceed the capacity.\n");
						ueList.get(serverList.get(i).getPreference()[j]).setProposeTo(-1);
						int c = ueList.get(serverList.get(i).getPreference()[j]).checkTheCount(-1);
						if(ueList.get(serverList.get(i).getPreference()[j]).getPreferenceCount() == -1)
							ueList.get(serverList.get(i).getPreference()[j]).setPreferenceCount(c);
						else
						{
							int c1 = ueList.get(serverList.get(i).getPreference()[j]).getPreferenceCount();
							ueList.get(serverList.get(i).getPreference()[j]).setPreferenceCount(c - c1 - 1);
						}
					}
				}
			}
		}
	}	
	public static void WriteToFile(List<UE> ueList, List<Server> serverList, String outputFile) throws IOException
	{
		FileWriter w = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(w);
		
		String line;
		int count = 0;
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
				count++;
				line = Integer.toString(i) + "," + Integer.toString(ueList.get(i).getProposeTo());
				System.out.printf("UE %d matches to server %d\n", i, ueList.get(i).getProposeTo());
			}
			else
			{
				line = Integer.toString(i) + "," + "-1";
				System.out.printf("UE %d matches failed.\n", i);
			}
			System.out.println(line);
			bw.write(line);
			bw.newLine();
		}
		System.out.printf("Total %d accepts.\n", count);
		bw.close();
		
	}
	
	public static void outputObjectToFile(List<UE> ueList, List<Server> serverList, String outputDirectory, int ordinal) throws IOException
	{
		String outputPathUE = outputDirectory + "UE" + ordinal + ".ue";
		String outputPathServer = outputDirectory + "server" + ordinal + ".server";
		
		FileOutputStream fosUE = new FileOutputStream(outputPathUE);
		ObjectOutputStream oosUE = new ObjectOutputStream(fosUE);
		oosUE.writeObject(ueList);
		oosUE.close();
		
		FileOutputStream fosServer = new FileOutputStream(outputPathServer);
		ObjectOutputStream oosServer = new ObjectOutputStream(fosServer);
		oosServer.writeObject(serverList);
		oosServer.close();		
	}
	
	public static void oldImplement() throws IOException
	{
		System.out.println("Old implement");
		FileReader fr = new FileReader("input.txt");
		BufferedReader br = new BufferedReader(fr);
		
		// A temp string.
		String[] temp;
		String tempS;
		
		// initialize the Servers
		int numberOfServers = Integer.parseInt(br.readLine());
		Server[] server = new Server[numberOfServers];
		for(int i = 0; i < numberOfServers; i++)
		{ 
			temp = br.readLine().split(" ");
			double[] capacity = new double[temp.length];
			for(int j = 0; j < temp.length; j++)
			{
				capacity[j] = Double.parseDouble(temp[j]);
			}
			server[i] = new Server(capacity);
		}
		
		// initialize the UEs
		int numberOfUEs = Integer.parseInt(br.readLine());
		UE[] ue = new UE[numberOfUEs];
		for(int i = 0; i < numberOfUEs; i++)
		{
			temp = br.readLine().split(" ");
			double[] demand = new double[temp.length];
			for(int j = 0; j < temp.length; j++)
			{
				demand[j] = Double.parseDouble(temp[j]);
			}
			tempS = br.readLine();
			double maxLatency = Double.parseDouble(tempS);
			ue[i] = new UE(demand, maxLatency);
		}
		
		// Algorithm starts.
		
		// 1. UEs get latency list from the OCS.
		for(int i = 0; i < numberOfUEs; i++)
		{
			temp = br.readLine().split(" ");
			double[] latency = new double[temp.length];
			for(int j = 0; j < temp.length; j++)
			{
				latency[j] = Double.parseDouble(temp[j]);
			}
			ue[i].setLatency(latency);
		}
		
		// 2. UEs set the preference list to the servers.
		for(int i = 0; i < numberOfUEs; i++)
		{
			ue[i].setPreference();
		}
		
		// 3. Servers set the preference list to the UEs.
		for(int i = 0; i < numberOfServers; i++)
		{		
			server[i].setPreference(ue);
			System.out.printf("The preference list of server %d is:", i);
			for(int j = 0; j < server[i].getPreference().length; j++)
			{
				System.out.printf(" %d", server[i].getPreference()[j]);
			}
			System.out.println();
		}
		
		boolean globalRejection = false;
		do{
			globalRejection = false;
			// 4. UEs propose to the first preferred server.
			for(int i = 0; i < numberOfUEs; i++)
			{
				if(ue[i].getAccept() == false)
				{
					ue[i].setProposeTo();
					System.out.printf("UE %d proposes to server %d.\n", i, ue[i].getProposeTo());
				}
			}
		
			// 5. The Servers check the preference list from top to down,
			// 	  if the ith UE proposed, check its demand,
			//	  if smaller than capacity, continue, until exceed capacity
		
			for(int i = 0; i < numberOfServers; i++)
			{
				double[] used = new double[server[i].getCapacity().length];
				for(int j = 0; j < used.length; j++)
				{
					used[j] = 0;
				}
				for(int j = 0; j < numberOfUEs; j++)
				{
					if(ue[server[i].getPreference()[j]].getProposeTo() == i)
					{
						System.out.printf("algo: UE %d proposed to %d.\n", server[i].getPreference()[j], i);
						boolean accept = true;
						for(int k = 0; k < server[i].getCapacity().length; k++)
						{
							if(used[k] + ue[server[i].getPreference()[j]].getDemand()[k] > server[i].getCapacity()[k])
							{
								accept = false;
								globalRejection = true;
								break;
							}
						}
						if(accept)
						{
							for(int k = 0; k < server[i].getCapacity().length; k++)
							{
								used[k] += ue[server[i].getPreference()[j]].getDemand()[k];
							}
							ue[server[i].getPreference()[j]].setAccept(true);
							System.out.printf("UE %d is accepted by server %d.\n", server[i].getPreference()[j], i);
						}
						else
						{
							ue[server[i].getPreference()[j]].setAccept(false);
							System.out.printf("UE %d is rejected by server %d.\n", server[i].getPreference()[j], i);
							break;
						}		
					}
				}
			}
		}while(globalRejection == true);

		// Print out the result.
		for(int i = 0; i < numberOfUEs; i++)
		{
			if(ue[i].getAccept())
				System.out.printf("UE %d matches to server %d\n", i, ue[i].getProposeTo());
			else
				System.out.printf("UE %d matches failed.\n", i);
		}
	}

}
