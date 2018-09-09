import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ps.PSGraphics2D;
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

public class ShowFigure3 extends ApplicationFrame
{

	public static void main(String[] args) throws IOException
	{
		int whichColumnIndex = 6;  // input 2~10 for intra, 5,6,7,11 for inter
		
		ShowFigure3 demo = new ShowFigure3(whichColumnIndex);
            
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	public void outputEPS(JFreeChart chart, int c)
	{
		String st = "intra";	
		try{
				Properties p = new Properties();
				VectorGraphics g = new PSGraphics2D(new File( st + c + ".eps"), new Dimension(500, 375)); 
				g.setBackground(null);
				Rectangle2D r2d = new Rectangle2D.Double(0, 0, 500, 375);
				g.startExport(); 
				chart.draw(g, r2d);
				chart.setBackgroundPaint(null);
				g.endExport();
		} catch (Exception iox) {
				iox.printStackTrace();
		}
	}
	public JFreeChart createChart(XYDataset dataset, int column)
	{
		return ChartFactory.createXYLineChart(
				"",
//	        	columnIndextoString(column)[0],		// Title
	        	columnIndextoString(column)[1],  	// X
	        	columnIndextoString(column)[2], 	// Y
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            false,
	            false
	        );
	}
	public ShowFigure3(int c) throws IOException
	{
        super(columnIndextoString(c)[0]);
        XYDataset dataset = readFile(c);
        JFreeChart chart = createChart(dataset, c);
        chart.setBackgroundPaint(null);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(3, new Color(0xBB, 0xBB, 0xBB));
 	   	renderer.setSeriesPaint(4, new Color(0xFF, 0x8C, 0x00)); 
 	   	renderer.setSeriesPaint(5, new Color(0x00, 0x8C, 0xFF)); 
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true); 
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, true); 
        renderer.setSeriesLinesVisible(3, true);
        renderer.setSeriesShapesVisible(3, true); 
        renderer.setSeriesLinesVisible(4, true);
        renderer.setSeriesShapesVisible(4, true); 
        renderer.setSeriesLinesVisible(5, true);
        renderer.setSeriesShapesVisible(5, true); 
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(null);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setBackground(null);
        setContentPane(chartPanel);
        
        outputEPS(chart, c);

    }	
	public static XYDataset readFile(int c) throws IOException
    {
		XYSeries series0 = new XYSeries("INTRA");  // for intra;
    	XYSeries series1 = new XYSeries("Random");
    	XYSeries series2 = new XYSeries("Boston");
    	XYSeries series3 = new XYSeries("WOIntra");
		XYSeries series4 = new XYSeries("INTRA-Latency");
		XYSeries series5 = new XYSeries("CHA");
			
    	int[] UERange = { 100, 100 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int metricIndex = c; 	
		
		// for running CPU capacity as X-axis
		int[] theNumberOfXAxisToBeCompared = {1, 3};
		int xAxisInterval = 1;
			
		int algoNumber = 5;
			
 		for(int algo = 0; algo <= algoNumber; algo++)  		
    	{
    		for(int server = serverRange[0]; server <= serverRange[1]; server = server + serverInterval)
			{
    			for(int UE = UERange[0]; UE <= UERange[1]; UE = UE + UEInterval)
    			{
    				for(int xAxis = theNumberOfXAxisToBeCompared[0]; xAxis <= theNumberOfXAxisToBeCompared[1]; xAxis += xAxisInterval)
    				{				
    					String algoString = Function.algoNumberToAlgoStream(algo);
    					
    					String filePath = "performance/" + "intra/" + algoString + "/" + "UE" + UE + "-" + "server" + server + "-" + xAxis + ".csv"; 
    				
    					FileReader fr = new FileReader(filePath);
    					BufferedReader br = new BufferedReader(fr);
    				
    					String[] field = br.readLine().split(",");
    					switch(algo)
    					{
    						case 0:
    							series0.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						case 1:
    							series1.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						case 2:
    							series2.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						case 3:
    							series3.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						case 4:
    							series4.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						case 5:
    							series5.add(xAxis,Double.parseDouble(field[metricIndex]));
    							break;
    						default:
    							break;
    					}
    					br.close();		
    				}
    			}
			}
    	}
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	dataset.addSeries(series0);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
//        dataset.addSeries(series4);
        dataset.addSeries(series5);
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
				result[1] = "Numbers of UEs"; 	//Modify the X-Axis Name here.
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
				break;
				
			// For inter
			case 11:
				result[0] = "Average Revenue of Each Server";
				result[1] = "Numbers of UEs";
				result[2] = "Revenue";
				break;
			default:
				break;
		}
		return result;
	}	
}
