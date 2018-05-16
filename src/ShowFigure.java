import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ShowFigure extends ApplicationFrame
{

	public static void main(String[] args) throws IOException
	{
		int whichColumnIndex = 3;  // input 2~10
		ShowFigure demo = new ShowFigure(whichColumnIndex);
            
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	
	public ShowFigure(int c) throws IOException
	{
        super(columnIndextoString(c)[0]);
        XYDataset dataset = readFile(c);
        JFreeChart chart = ChartFactory.createXYLineChart(
        	columnIndextoString(c)[0],	// Title
        	columnIndextoString(c)[1],  // X
        	columnIndextoString(c)[2], 	// Y
            dataset,
            PlotOrientation.VERTICAL,
            true,
            false,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(3, new Color(0xBB, 0xBB, 0xBB)); 
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true); 
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, true); 
        renderer.setSeriesLinesVisible(3, true);
        renderer.setSeriesShapesVisible(3, true); 
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(null);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        setContentPane(chartPanel);

    }
	
	public static XYDataset readFile(int c) throws IOException
    {
    	XYSeries series0 = new XYSeries("DA");
    	XYSeries series1 = new XYSeries("Random");
    	XYSeries series2 = new XYSeries("Boston");
    	XYSeries series3 = new XYSeries("WOIntra");
    	
    	int[] UERange = { 50, 1000 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int metricIndex = c; 			
		
    	for(int algo = 0; algo <= 3; algo++)
    	{
    		for(int server = serverRange[0]; server <= serverRange[1]; server = server + serverInterval)
			{
    			for(int UE = UERange[0]; UE <= UERange[1]; UE = UE + UEInterval)
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
    	
    				String filePath = "performance/" + algoString + "/" + "UE" + UE + "-" + "server" + server + ".csv"; 
    				FileReader fr = new FileReader(filePath);
    				BufferedReader br = new BufferedReader(fr);
    				
    				String[] field = br.readLine().split(",");
    				switch(algo)
    				{
    					case 0:
    						series0.add(Double.parseDouble(field[0]),Double.parseDouble(field[metricIndex]));
    						break;
    					case 1:
    						series1.add(Double.parseDouble(field[0]),Double.parseDouble(field[metricIndex]));
    						break;
    					case 2:
    						series2.add(Double.parseDouble(field[0]),Double.parseDouble(field[metricIndex]));
    						break;
    					case 3:
    						series3.add(Double.parseDouble(field[0]),Double.parseDouble(field[metricIndex]));
    						break;
    					default:
    						break;
    				}
    				br.close();		
    			}
			}
    	}
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	dataset.addSeries(series0);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        return dataset;
    }
	public static String[] columnIndextoString(int c)
	{
		String[] result = {"Title", "X", "Y"};
		switch(c)
		{
			case 2:
				result[0] = "Average of Preferences of all UEs";
				result[1] = "Numbers of UEs";
				result[2] = "Preference Count";
				break;
			case 3:
				result[0] = "Standard Deviation of Preferences of all UEs";
				result[1] = "Numbers of UEs";
				result[2] = "Standard Deviation";
				break;
			case 4:
				result[0] = "Balance Index of Servers";
				result[1] = "Numbers of UEs";
				result[2] = "Balance Index";
				break;
			case 5:
				result[0] = "Average Latency of Accepted UEs";
				result[1] = "Numbers of UEs";
				result[2] = "Average Latency";
				break;
			case 6:
				result[0] = "Average Number of Served UEs of Each Server";
				result[1] = "Numbers of UEs";
				result[2] = "Number of Served UEs";
				break;
			case 7:
				result[0] = "Standard Deviation of served Latency";
				result[1] = "Numbers of UEs";
				result[2] = "Standard Deviation";
				break;
			case 8:
				result[0] = "Percentage of Outsourcing";
				result[1] = "Numbers of UEs";
				result[2] = "Percentage";
				break;
			case 9:
				result[0] = "Average of Preferences of Accepted UEs";
				result[1] = "Numbers of UEs";
				result[2] = "Preference Count";
				break;
			case 10:
				result[0] = "Standard Deviation of Preferences of Accepted UEs";
				result[1] = "Numbers of UEs";
				result[2] = "Standard Deviation";
			default:
				break;
		}
		return result;
	}
	
}
