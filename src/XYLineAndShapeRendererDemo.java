// This code is from http://www.java2s.com/Code/Java/Chart/JFreeChartXYLineAndShapeRendererDemo.htm
// This code is to create a figure for line and shape.

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

/**
 * A simple demonstration of the {@link XYLineAndShapeRenderer} class.
 */
public class XYLineAndShapeRendererDemo extends ApplicationFrame {

    /**
     * Constructs the demo application.
     *
     * @param title  the frame title.
     */
    public XYLineAndShapeRendererDemo(String title, List<Point2D> UECoordinate, List<Point2D> serverCoordinate) {

        super(title);
        XYDataset dataset = createSampleDataset(UECoordinate, serverCoordinate);
        JFreeChart chart = ChartFactory.createXYLineChart(
            //title,
        		"",
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            false,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true); 
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(null);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        setContentPane(chartPanel);

        outputEPS(chart);
    }
    public void outputEPS(JFreeChart chart)
	{

		
		try{
				Properties p = new Properties();
				VectorGraphics g = new PSGraphics2D(new File("area.eps"), new Dimension(500, 375)); 
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
    
    /**
     * Creates a sample dataset.
     * 
     * @return A dataset.
     */
    private XYDataset createSampleDataset(List<Point2D> UECoordinate, List<Point2D> serverCoordinate) {
    	XYSeries series1 = new XYSeries("Server");
        for(int i = 0; i < serverCoordinate.size(); i++)
        {
        	series1.add(serverCoordinate.get(i).getX(), serverCoordinate.get(i).getY());
        }
    	XYSeries series2 = new XYSeries("UE");
        for(int i = 0; i < UECoordinate.size(); i++)
        {
        	series2.add(UECoordinate.get(i).getX(), UECoordinate.get(i).getY());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
//    public static void main(final String[] args) {
//
//        final XYLineAndShapeRendererDemo demo = new XYLineAndShapeRendererDemo(
//            "XYLineAndShapeRenderer Demo"
//        );
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//
//    }
    
    public static void showRenderer(List<Point2D> UECoordinate, List<Point2D> serverCoordinate)
    {
    	XYLineAndShapeRendererDemo demo = new XYLineAndShapeRendererDemo(
                "XYLineAndShapeRenderer Demo", UECoordinate, serverCoordinate);
            
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setVisible(true);
    }

}
