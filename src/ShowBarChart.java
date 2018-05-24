// This code is originated from http://www.csie.ntnu.edu.tw/~swanky/jfreechart/BarChart.htm
// This code is for create Bar Chart.

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.*;

public class ShowBarChart extends JFrame{
   public ShowBarChart() throws IOException
   {
//      CategoryDataset dataset = createDataset();
	  CategoryDataset dataset = readFile();
      JFreeChart chart = createChart(dataset);
      chart = customizeChart(chart);
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new Dimension(500, 270));
      getContentPane().add(chartPanel);

      pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   public static void main(String[] args) throws IOException
   {
      new ShowBarChart();
   }

   private CategoryDataset createDataset(){
      // row keys...
      String series1 = "First";
      String series2 = "Second";
      String series3 = "Third";

      // column keys...
      String category1 = "Category 1";
      String category2 = "Category 2";
      String category3 = "Category 3";
      String category4 = "Category 4";
      String category5 = "Category 5";

      // create the dataset...
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();

      dataset.addValue(1.5, series1, category1);
      dataset.addValue(4.2, series1, category2);
      dataset.addValue(3.0, series1, category3);
      dataset.addValue(5.0, series1, category4);
      dataset.addValue(5.0, series1, category5);

      dataset.addValue(5.5, series2, category1);
      dataset.addValue(7.8, series2, category2);
      dataset.addValue(6.0, series2, category3);
      dataset.addValue(8.0, series2, category4);
      dataset.addValue(4.0, series2, category5);

      dataset.addValue(4.0, series3, category1);
      dataset.addValue(3.0, series3, category2);
      dataset.addValue(2.0, series3, category3);
      dataset.addValue(3.0, series3, category4);
      dataset.addValue(6.0, series3, category5);

      return dataset;
   }

   private JFreeChart createChart(final CategoryDataset dataset){
      JFreeChart chart = ChartFactory.createBarChart(
         "Preference Distribution", // chart title
         "Preference Count", // domain axis label
         "Number of UE", // range axis label
         dataset, // data
         PlotOrientation.VERTICAL, // orientation
         true, // include legend
         true, // tooltips?
         false // URLs?
         );
      return chart;
   }

   private JFreeChart customizeChart(final JFreeChart chart){
	   CategoryPlot plot = (CategoryPlot) chart.getPlot();
	   BarRenderer renderer = (BarRenderer) plot.getRenderer();
	   renderer.setBarPainter(new StandardBarPainter());
	   plot.setBackgroundPaint(null);
	   renderer.setSeriesPaint(3, new Color(0xBB, 0xBB, 0xBB)); 
      return chart;
   }
   
   public CategoryDataset readFile() throws IOException
   {
	   int UE = 500;
	   int server = 10;
	   
//	   String whichChart = "BarChart";
	   String whichChart = "LatencyBarChart";
	   
	   DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	   // row keys...
	   String series0 = "DA";
	   String series1 = "Random";
	   String series2 = "Boston";
	   String series3 = "WOIntra";

	   // column keys...
	   String[] category = new String[server+1];
	   for(int i = 0; i < category.length; i++)
	   {
		   category[i] = String.valueOf(i);
	   }
	   
	   for(int algo = 0; algo <= 3; algo++)
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
   	
   			String filePath = "performance/" + algoString + "/" + "UE" + UE + "-" + "server" + server + "-" + whichChart + ".csv"; 
   			FileReader fr = new FileReader(filePath);
   			BufferedReader br = new BufferedReader(fr);
   				
   			String[] field = br.readLine().split(",");
   		
   			for(int i = 2; i < field.length; i++)
   			{
   				if(whichChart.equals("BarChart"))
   					dataset.addValue(Double.parseDouble(field[i]), algoString, String.valueOf(i-2+1));
   				if(whichChart.equalsIgnoreCase("LatencyBarChart"))
   					dataset.addValue(Double.parseDouble(field[i]), algoString, String.valueOf((i-2)*10));
   			}
   			br.close();		
	   }
	   return dataset;
   }
}