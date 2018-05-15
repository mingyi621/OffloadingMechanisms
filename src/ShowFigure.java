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
		ShowFigure demo = new ShowFigure("Figure");
            
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	
	public ShowFigure(String title) throws IOException
	{
        super(title);
        XYDataset dataset = readFile();
        JFreeChart chart = ChartFactory.createXYLineChart(
            title,
            "UE",  		// x
            "Y", 	// y
            dataset,
            PlotOrientation.VERTICAL,
            true,
            false,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
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
	
	public static XYDataset readFile() throws IOException
    {
    	XYSeries series0 = new XYSeries("DA");
    	XYSeries series1 = new XYSeries("Random");
    	XYSeries series2 = new XYSeries("Boston");
    	XYSeries series3 = new XYSeries("WOIntra");
    	
    	int[] UERange = { 50, 500 };	// Both inclusion
		int UEInterval = 50;
		int[] serverRange = { 10, 10 }; // Both inclusion
		int serverInterval = 10;
		int metricIndex = 2; // 2: average 
		
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
	
}
