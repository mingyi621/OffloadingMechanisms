import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtractPerformanceData 
{
	public static void main(String[] args) throws IOException
	{
		FileWriter fw = new FileWriter("performance/figure2.csv");
		fw.write(",50,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000\n");
		
		for(int algo = 0; algo <= 5; algo++)
		{
			if(algo == 0)
				fw.write("INTRA");
			else if(algo == 1)
				fw.write("Random");
			else if(algo == 2)
				fw.write("Boston");
			else if(algo == 3)
				fw.write("WOIntra");
			else if(algo == 4) 
				continue;
			else if(algo == 5)
				fw.write("CHA");
			
			
			
			for(int i = 50; i <= 1000; i = i + 50)
			{
				String path = "performance/" + Function.algoNumberToAlgoStream(algo) + "/" + "UE"+ i + "-server10.csv";
				double data = readFile(path, 5);
				fw.write("," + data);
			}
			fw.write("\n");
		}

	}
	
	public static double readFile(String path, int column) throws IOException
	{
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String[] dataString = br.readLine().split(",");
		fr.close();
		
		double data = Double.parseDouble(dataString[column]);
		return data;
	}

}
