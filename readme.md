Ming-Yi's Master Thesis Code:
-----

This is the original codes of the thesis: Resource Allocation for Federated Heterogeneous Edge and Fog Systems: A Matching Game Approach.

There are several files to process the mechanisms.

1. DataGenerator.java 

	In this code, it can generate the UEs' requests and servers' capacities. Also, it can generate the cost and valuation of the servers and requests, respectively. If there are no data in your computer, please run this file first.

2. Main.java
	
	In this code, the many-to-one matching algorithms without monetary exchange are implemented. The names of the four algorithm are as follows.

	+ deferred acceptance algorithm: This is the Intra-EFS offloading mechanism which is mentioned in the thesis.
	+ Random algorithm: Which is just an random algorithm.
	+ Boston mechanism: Which is another mechanism that is available to compare with the Intra-EFS offloading mechanism.
	+ Without intra-EFS offloading: Only the nearest server can serve the request

	Due to the different definition of the preference function, the result will be different. So there is a block that can change the preference function. For interested reader, please check the code.

3. Main2.java
	
	In this code, the many-to-one matching algorithms with monetary exchange are implemented. The four functions are similar to the above code, except there is an epsilon value be added.

4. PerformanceEvaluation.java & PerformanceEvaluation2.java

	These two files can evaluate performances of the algorithms.

5. ShowFigure.java & ShowFigure2.java

	These two files can show the figures of intra-EFS offloading and inter-EFS offloading, respectively.

6. ShowBarChart.java

	This file can output a bar chart figure.

7. ExtractPerformanceData.java & ExtractPerformanceData2.java

	These files are for extra the performance csv files to a chart friendly files, maybe it can be used for Matlab. (not yet tested)

8. Function.java 

	Some simple functions are implemented here. Like sorting and algo number to string.

9. UE.java & Server.java

	These two files are the defined classes of UE and Server.

9. Other files are download from the third party's library, and they are useful for testing for figures:

	ChartToPS.java
	HelloBarChart.java
	XYLineAndShapeRendererDemo.java // For generating the first figure.

------

If you want to run the codes and generate the figures, please run by the following procedures:

	For intra-EFS offloading:
	DataGenerator.java --> Main.java --> PerformanceEvaluation.java --> ShowFigure.java 

	For inter-EFS offloading:
	DataGenerator.java --> Main2.java --> PerformanceEvaluation2.java --> ShowFigure2.java

For Main.java, it will run approximately 1 hour to complete.
For Main2.java, it will run approximately 6~7 hours to complete.

It is recommend not to download the data from my online drive, since these codes can generate similar files.

The online data is here: https://drive.google.com/open?id=1dJth_8CF535q8hCZRty3w8rNaS-zRkc4

If you want to download, the online files include the input, output, and performance files, so that you can directly run the "ShowFigure.java" and "ShowFigure2.java" programs.

------

This code needs the third party library JFreeChart (jfreechart-1.0.19).
Please first download it from here: https://sourceforge.net/projects/jfreechart/files/
Add the two files to your java IDE: jcommon-1.0.23.jar, jfreechart-1.0.19.jar

Another third party library is still needed: http://java.freehep.org/vectorgraphics/

Ask me if you have any questions. mingyi621@gmail.com

And enjoy the codes^^