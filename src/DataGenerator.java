import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator 
{
	public static void main(String[] args) throws IOException
	{
		// Basic environment settings.
		int UE = 400;
		int server = 21;
		double percentage = 0.6; // the percentage of UEs have latency constraint.
		int[] maxLatencyRange = {1,100};
		double[] area = {100, 100};

		// File Path settings.
		String UEdirectory = "input/" + "UE/" + Integer.toString(UE);
		String UEfileName = "UE1.csv";
		String serverDirectory = "input/" + "server/" + Integer.toString(server);
		String serverFileName = "server1.csv";
		String latencyDirectory = "input/" + "latency/" + "UE" + Integer.toString(UE) + "-" + "server" + Integer.toString(server);
		String latencyFileName = "latency1.csv";
	    
		File UEdir = new File(UEdirectory);
	    if (!UEdir.exists())	UEdir.mkdir();	    
	    File serverDir = new File(serverDirectory);
	    if (!serverDir.exists())	serverDir.mkdirs();
	    File latencyDir = new File(latencyDirectory);
	    if (!latencyDir.exists())	latencyDir.mkdirs();
	        
		String outputUEPath = UEdirectory + "/" + UEfileName;
		String outputServerPath = serverDirectory + "/" + serverFileName;
		String outputLatencyPath = latencyDirectory + "/" + latencyFileName;
		
		// Process UE files.
		List<double[]> requestList = generateRequest(UE);
		List<int[]> maxLatencyList = generateMaxLatency(UE, percentage, maxLatencyRange, area);	
		writeToUEFile(outputUEPath, requestList, maxLatencyList);	
		
		// Process server files.
		List<double[]> serverList = generateServer(server);
		writeToServerFile(outputServerPath, serverList);	
		
		// Process latency files.
//		List<int[]> latencyList = generateLatencyList(UE, server, area);
//		List<int[]> latencyList = generateLatencyListWithLocality(UE, server, area);
		List<int[]> latencyList = generateLatencyListWithClusterAndLocality(UE, server, area);
		writeToLatencyFile(outputLatencyPath, latencyList);
		
	}
	public static List<double[]> generateRequest(int UE)
	{
		List<double[]> list = new ArrayList<>();		
		Random random = new Random();
		double d = 0;
		double[] vm1 = {1, 3.75, 4};
		double[] vm2 = {2, 7.5, 32};
		double[] vm3 = {4, 15, 80};
		double[] vm4 = {8, 30, 160};
		
		System.out.println("Generating Requests:");
		for(int i = 0; i < UE; i++)
		{
			d = random.nextGaussian() / 1;
			int m1 = Math.abs((int) Math.round(d));
			d = random.nextGaussian() / 1;
			int m2 = Math.abs((int) Math.round(d));
			d = random.nextGaussian() / 2;
			int m3 = Math.abs((int) Math.round(d));	
			d = random.nextGaussian() / 3;
			int m4 = Math.abs((int) Math.round(d));
		
			double cpu = m1 * vm1[0] + m2 * vm2[0] + m3 * vm3[0] + m4 * vm4[0];
			double memory = m1 * vm1[1] + m2 * vm2[1] + m3 * vm3[1] + m4 * vm4[1];
			double storage = m1 * vm1[2] + m2 * vm2[2] + m3 * vm3[2] + m4 * vm4[2];
			
			if(cpu == 0 && memory == 0 && storage == 0)
			{
				i--;
				continue;
			}
		
			System.out.printf("%.0f, %.2f, %.0f\n", cpu, memory, storage);
			double[] row = {cpu, memory, storage};
			list.add(row);
		}
		System.out.println();
		return list;
	}
	public static List<int[]> generateMaxLatency(int UE, double percentage, int[] latencyRange, double[] area)
	{
		List<int[]> list = new ArrayList<>();
		Random random = new Random();
		
		System.out.println("Generating max latency:");
		for(int i = 0; i < UE; i++)
		{
			int[] latency = {-1};
			if(random.nextDouble() > percentage)
				latency[0] = (int) (area[0] * area[1]);
			else
			{
				latency[0] = random.nextInt(latencyRange[1]-latencyRange[0]+1) + latencyRange[0];
			}
			System.out.printf("Latency = %d\n", latency[0]);
			list.add(latency);
		}
		System.out.println();
		return list;
	}
	public static void test()
	{
		Random random = new Random();
		
		for(int h = 0; h < 10; h++)
		{
			int zero = 0;
			int one = 0;
			int two = 0;
			int three = 0;
			for(int i = 0; i < 100; i++)
			{
			
				double d = random.nextGaussian()/1;
				int m = (int) Math.floor(Math.abs(d));
				//System.out.println(m);
				if (m == 0) zero++;
				if (m == 1) one++;
				if (m == 2) two++;
				if (m == 3) three++;
			}
			System.out.printf("%d,%d,%d,%d\n", zero, one, two, three);
		}
	}
	public static void writeToUEFile(String outputFile, List<double[]> requestList, List<int[]> maxLatencyList) throws IOException
	{
		FileWriter w = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(w);
		
		String row;
		System.out.println("Writing UE files:");
		for(int i = 0; i < requestList.size(); i++)
		{ 
			row = String.valueOf(requestList.get(i)[0]) + ","
				+ String.valueOf(requestList.get(i)[1]) + ","
				+ String.valueOf(requestList.get(i)[2]) + ","
				+ String.valueOf(maxLatencyList.get(i)[0]);
			System.out.println(row);
			bw.write(row);
			bw.newLine();
		}
		System.out.println();
		
		bw.flush();
		bw.close();
	}
	public static List<double[]> generateServer(int server)
	{
		// Server List Settings.
		double[] mean = {50, 200, 500};
		double[] sd = {10, 50, 100};
		
		Random random = new Random();
		List<double[]> list = new ArrayList<>();
		
		
		for(int i = 0; i < server; i++)
		{
			double[] row = { Math.round((random.nextGaussian() * sd[0] + mean[0]) ),
						     Math.round((random.nextGaussian() * sd[1] + mean[1]) ),
						     Math.round((random.nextGaussian() * sd[2] + mean[2]) )};
			list.add(row);
			
		}
		System.out.println();
		return list;
	}
	public static void writeToServerFile(String outputFile, List<double[]> serverList) throws IOException
	{
		FileWriter w = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(w);
	
		System.out.println("Writing servers:");
		for(int i = 0; i < serverList.size(); i++)
		{ 
			String row = String.valueOf(serverList.get(i)[0]) + ","
					   + String.valueOf(serverList.get(i)[1]) + ","
				       + String.valueOf(serverList.get(i)[2]);
			System.out.println(row);
			bw.write(row);
			bw.newLine();
		}
		System.out.println();
		
		bw.flush();
		bw.close();
	}
	public static List<int[]> generateLatencyList(int UE, int server, double[] area)
	{
		List<Point2D> UECoordinate = new ArrayList<>();
		List<Point2D> serverCoordinate = new ArrayList<>();
		List<int[]> distance = new ArrayList<>();
		
		Random random = new Random();
		
		for(int i = 0; i < UE; i++)
		{	
			Point2D coordinate = new Point2D.Double();
			coordinate.setLocation(random.nextDouble() * area[0], random.nextDouble() * area[1]);
			UECoordinate.add(coordinate);
//			System.out.println(coordinate);
		}
		
		for(int j = 0; j < server; j++)
		{
			Point2D coordinate = new Point2D.Double();
			coordinate.setLocation(random.nextDouble() * area[0], random.nextDouble() * area[1]);
			serverCoordinate.add(coordinate);
		}
		
		for(int i = 0; i < UE; i++)
		{
			int[] row = new int[server];
			for(int j = 0; j < server; j++)
			{
				row[j] = (int)UECoordinate.get(i).distance(serverCoordinate.get(j));
			}
			distance.add(row);			
		}
		XYLineAndShapeRendererDemo.showRenderer(UECoordinate, serverCoordinate);
		return distance;
	}
	public static List<int[]> generateLatencyListWithLocality(int UE, int server, double[] area)
	{
		int localityLatency = 1;
//		double servedRadius = area[0] / (Math.sqrt(server) * 2);
//		System.out.printf("Served Radius = %.2f\n", servedRadius);
		
		List<Point2D> UECoordinate = new ArrayList<>();
		List<Point2D> serverCoordinate = new ArrayList<>();
		List<int[]> distance = new ArrayList<>();
		
		Random random = new Random();
		
		for(int i = 0; i < UE; i++)
		{	
			Point2D coordinate = new Point2D.Double();
			coordinate.setLocation(random.nextDouble() * area[0], random.nextDouble() * area[1]);
			UECoordinate.add(coordinate);
		}
		
		for(int j = 0; j < server; j++)
		{
			Point2D coordinate = new Point2D.Double();
			coordinate.setLocation(random.nextDouble() * area[0], random.nextDouble() * area[1]);
			serverCoordinate.add(coordinate);
		}
		for(int i = 0; i < UE; i++)
		{
			int[] row = new int[server];
			int minimum = (int)UECoordinate.get(i).distance(serverCoordinate.get(0)); 
			int minimumServer = 0;
			Point2D minimumCoordinate = serverCoordinate.get(0);
			for(int j = 0; j < server; j++)
			{
				int checkDistance = (int)UECoordinate.get(i).distance(serverCoordinate.get(j));
				if(checkDistance < minimum)
				{
					minimum = row[j];
					minimumServer = j;
					minimumCoordinate = serverCoordinate.get(j);
				}	
			}
			for(int j = 0; j < server; j++)
			{
				if(j != minimumServer)
				{
					row[j] = (int) (localityLatency + serverCoordinate.get(j).distance(minimumCoordinate));
				}
				else 
				{
					row[j] = localityLatency;
					System.out.printf("Local served, distance = %d\n", (int) UECoordinate.get(i).distance(serverCoordinate.get(j)));
				}
			}
			distance.add(row);			
		}
		XYLineAndShapeRendererDemo.showRenderer(UECoordinate, serverCoordinate);
		
		return distance;
	}
	public static List<int[]> generateLatencyListWithClusterAndLocality(int UE, int server, double[] area)
	{
		int localityLatency = 1;
		double servedRadius = area[0] / (Math.sqrt(Math.sqrt(server)) * 2);

		System.out.printf("Served Radius = %.2f\n", servedRadius);
		int sd = server / 4;
	
		List<Point2D> UECoordinate = new ArrayList<>();
		List<Point2D> serverCoordinate = new ArrayList<>();
		List<int[]> distance = new ArrayList<>();
		
		Random random = new Random();
		
		for(int j = 0; j < server; j++)
		{
			Point2D coordinate = new Point2D.Double();
			coordinate.setLocation(random.nextDouble() * area[0], random.nextDouble() * area[1]);
			serverCoordinate.add(coordinate);
		}
		
		for(int i = 0; i < UE; i++)
		{	
			Point2D coordinate = new Point2D.Double();
			int s;
			do{
				s = (int)(random.nextGaussian() * sd + (server - 1) / 2);
			}while(s < 0 || s >= server);
			
			double X, Y;
			double theta = random.nextDouble() * 360;
			double radius = Math.abs(random.nextGaussian() * (servedRadius / 2));
		
			X = serverCoordinate.get(s).getX() + radius * Math.cos(Math.toRadians(theta));
			Y = serverCoordinate.get(s).getY() + radius * Math.sin(Math.toRadians(theta));
			
			coordinate.setLocation(X, Y);
			UECoordinate.add(coordinate);
		}
		for(int i = 0; i < UE; i++)
		{
			int[] row = new int[server];
			int minimum = (int)UECoordinate.get(i).distance(serverCoordinate.get(0)); 
			int minimumServer = 0;
			Point2D minimumCoordinate = serverCoordinate.get(0);
			for(int j = 0; j < server; j++)
			{
				int checkDistance = (int)UECoordinate.get(i).distance(serverCoordinate.get(j));
				if(checkDistance < minimum)
				{
					minimum = row[j];
					minimumServer = j;
					minimumCoordinate = serverCoordinate.get(j);
				}	
			}
			for(int j = 0; j < server; j++)
			{
				if(j == minimumServer) 
				{
					row[j] = localityLatency;
					System.out.printf("Local served, distance = %d\n", (int) UECoordinate.get(i).distance(serverCoordinate.get(j)));
				}
				else
				{
					row[j] = (int) (localityLatency + serverCoordinate.get(j).distance(minimumCoordinate));
				}
				
			}
			distance.add(row);			
		}
		XYLineAndShapeRendererDemo.showRenderer(UECoordinate, serverCoordinate);
		return distance;
	}	
	public static void writeToLatencyFile(String outputFile, List<int[]> latencyList) throws IOException
	{
		FileWriter w = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(w);
		
		System.out.println("Writing latency files:");
		for(int i = 0; i < latencyList.size(); i++)
		{
			String row = "";
			for(int j = 0; j < latencyList.get(i).length; j++)
			{
				row = row + String.valueOf(latencyList.get(i)[j]);
				if(j != latencyList.get(i).length - 1)	
					row = row + ",";
			}
			System.out.println(row);
			bw.write(row);
			bw.newLine();
		}
		System.out.println();
		
		bw.flush();
		bw.close();
	}
}