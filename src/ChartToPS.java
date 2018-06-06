import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.Properties;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ps.*;
import org.freehep.graphicsio.swf.SWFGraphics2D;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

public class ChartToPS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		      JFreeChart chart = getXYChart();
		      Properties p = new Properties();
	         VectorGraphics g = new PSGraphics2D(new File("Output.eps"), new Dimension(400,300)); 
	         Rectangle2D r2d = new Rectangle2D.Double(0, 0, 400, 300);
	         g.startExport(); 
	         chart.draw(g, r2d);
	         g.endExport();
		} catch (Exception iox) {
			iox.printStackTrace();
		}
	}

	
   /**
     * Gets an example XY chart
     * @return an XY chart
     */
    public static JFreeChart getXYChart() {
    	XYSeries series = new XYSeries("XYGraph");
    	series.add(1, 5);
    	series.add(2, 7);
    	series.add(3, 3);
    	series.add(4, 5);
    	series.add(5, 4);
    	series.add(6, 5);
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	dataset.addSeries(series);
    	return ChartFactory.createXYLineChart(
    			"XY Chart", "X-axis", "Y-axis", dataset,
				PlotOrientation.VERTICAL, true, true, false);
    }

}