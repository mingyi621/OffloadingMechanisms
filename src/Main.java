import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException
	{
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
