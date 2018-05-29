import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class Main {

	public static void main(String[] args) throws IOException
	{
		bulkSetDataProcessor();
//		oneSetDataProcessor(10, 2, 0, 0); // UE, server, ordinal, algo
	}
	public static void bulkSetDataProcessor() throws IOException
	{
		int[] UERange = { 50, 1000 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int numberOfSetForEachUE = 100;
		
		for(int algo = 0; algo <= 3; algo++)
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
		String outputDirectory = "output/" + algoString + "/" +  "UE" + UE + "-" + "server" + server + "/" ;
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
		
		// 2. Set UEs' Preference List
		for(int i = 0; i < ueList.size(); i++)
		{
			ueList.get(i).setPreference();
			System.out.printf("Preference list of UE %d: ",i);
			ueList.get(i).showPreference();
		}
		
		
		// 3. Set Servers' Preference List
		for(int i = 0; i < serverList.size(); i++)
		{
			serverList.get(i).setPreference(ueList);
			System.out.printf("Preference list of server %d: ",i);
			serverList.get(i).showPreference();
		}
		
		switch(algo)
		{
			case 0:
				deferredAcceptanceAlgorithm(ueList, serverList);
				break;
		
			case 1:
				RandomAlgorithm(ueList, serverList);
				break;
				
			case 2:
				BostonMechanism(ueList, serverList);
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
			double[] demand = {cpu, memory, storage};
			UE ue = new UE(demand, maxLatency);
			
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
			Server server = new Server(capacity);
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
		for(int i = 0; i < ueList.size(); i++)
		{
			if(ueList.get(i).getAccept())
			{
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
