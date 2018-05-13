// This code is originated from http://www.csie.ntnu.edu.tw/~swanky/jfreechart/BarChart.htm
// This code is for create Bar Chart.

import java.awt.*;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.*;

public class HelloBarChart extends JFrame{
   public HelloBarChart(){
      CategoryDataset dataset = createDataset();
      JFreeChart chart = createChart(dataset);
      chart = customizeChart(chart);
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new Dimension(500, 270));
      getContentPane().add(chartPanel);

      pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   public static void main(String[] args){
      new HelloBarChart();
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
         "Hello Bar Chart", // chart title
         "Category", // domain axis label
         "Value", // range axis label
         dataset, // data
         PlotOrientation.VERTICAL, // orientation
         true, // include legend
         true, // tooltips?
         false // URLs?
         );
      return chart;
   }

   private JFreeChart customizeChart(final JFreeChart chart){
      return chart;
   }
}