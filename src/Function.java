import java.util.List;

public class Function 
{
	// Modified from https://gist.github.com/codelance/4186238
	public static void mergeSort(double[] list, double[] index) {
		if (list.length > 1) {
			// Merge sort the first half
			//Using Divide and conquer we split the list in half
			//and process recursively until the list is sorted.
			//Split the first first half
			double[] firstHalf = new double[list.length / 2]; // runs n times
			double[] indexFirstHalf = new double[list.length / 2];
			System.arraycopy(list, 0, firstHalf, 0, list.length / 2); //runs n times
			System.arraycopy(index, 0, indexFirstHalf, 0, list.length / 2);
			mergeSort(firstHalf, indexFirstHalf); //runs n times
		      
			// Split the second half
			double secondHalfLength = list.length - list.length / 2; //runs n times
			double[] secondHalf = new double[(int)secondHalfLength]; //runs n times
			double[] indexSecondHalf = new double[(int)secondHalfLength];
			
			System.arraycopy(list, list.length / 2, secondHalf, 0, (int)secondHalfLength); //runs n times
			System.arraycopy(index, list.length / 2, indexSecondHalf, 0, (int)secondHalfLength);
			
			mergeSort(secondHalf, indexSecondHalf); //runs n times

			// Merge firstHalf with secondHalf
			//Now that the list has been split to a smaller size
			//then we can try to merge it 
			double[][] mix = merge(firstHalf, secondHalf, indexFirstHalf, indexSecondHalf); //runs n times
			double[] temp = mix[0];
			double[] indexTemp = mix[1];
			System.arraycopy(temp, 0, list, 0, temp.length); //runs n times
			System.arraycopy(indexTemp, 0, index, 0, temp.length);
		}
	}

	/** Merge two sorted lists */
	private static double[][] merge(double[] list1, double[] list2, double[] index1, double[] index2) {
		double[] temp = new double[list1.length + list2.length];
		double[] indexTemp = new double[list1.length + list2.length];

		int current1 = 0; // Current index in list1
		int current2 = 0; // Current index in list2
		int current3 = 0; // Current index in temp
		    
		//Loop the elements
		while (current1 < list1.length && current2 < list2.length) { //Runs n - 1
			//Compare the elements in each list and add to the merge list
			if (list1[current1] < list2[current2])
			{
				temp[current3] = list1[current1];
				indexTemp[current3++] = index1[current1++];
			}
			else
			{
				temp[current3] = list2[current2];
				indexTemp[current3++] = index2[current2++];
			}
		}
		//Copy the rest of elements into the list
		while (current1 < list1.length)
		{
			temp[current3] = list1[current1];
			indexTemp[current3++] = index1[current1++];
		}
		//Copy the rest of elements into the list
		while (current2 < list2.length)
		{
			temp[current3] = list2[current2];
			indexTemp[current3++] = index2[current2++];
		}
		
		double[][] mix = new double[2][];
		mix[0] = temp;
		mix[1] = indexTemp;
		return mix;
	}

	// calculate standard deviation
	public static double calculateSD(List<Double> array)
	{
		double total = 0;
		double mean = 0;
		double numerator = 0;
		for(int i = 0; i < array.size(); i++)
		{
			total = total + array.get(i).doubleValue();
		}
		mean = total / array.size();
		
		for(int i = 0; i < array.size(); i++)
		{
			numerator = numerator + Math.pow((array.get(i).doubleValue() - mean), 2);
		}
		return Math.sqrt(numerator / (array.size()));	
	}
	// calculate minimum of an array
	public static int minimum(int[] array)
	{
		int minimum;
		if(array.length > 0)
		{
			minimum = array[0];
			for(int i = 0; i < array.length; i++)
			{
				if(array[i] < minimum)
					minimum = array[i];
			}
		}
		else 
			minimum = -1;
		return minimum;
		
	}
	public static String algoNumberToAlgoStream(int algo)
	{
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
		return algoString;
	}
}