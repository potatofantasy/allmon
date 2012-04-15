package com.allmon.server.analyser.visual;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AggregateScalarTimeSeriesDemo extends ApplicationFrame {

	public AggregateScalarTimeSeriesDemo(String s) {
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}

	private static XYDataset createDataset() {
		TimeSeries timeseries = createTimeSeries("Mem UsedPercent:");
//		TimeSeries timeseries1 = MovingAverage.createMovingAverage(timeseries, "30 day moving average", 30, 30);
		TimeSeries timeseries1 = createTimeSeries("Processes Total:");
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
		timeseriescollection.addSeries(timeseries);
		timeseriescollection.addSeries(timeseries1);
		return timeseriescollection;
	}

	private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
			new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
	
	private static TimeSeries createTimeSeries(String resourceName) {
		AggregateScalarSelector scalarSelector = (AggregateScalarSelector) appContext.getBean("scalarSelector");
		List<AggregateScalarData> list = scalarSelector.select(resourceName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // DB: 'YYYY-MM-DD HH24:MI'
		
		TimeSeries timeseries = new TimeSeries(resourceName);
		try {
			for (AggregateScalarData scalarData : list) {
				timeseries.add(new Minute(sdf.parse(scalarData.MI)), scalarData.AVG);
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return timeseries;
	}

	private static JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(
				"Time Series Demo 8", "Date", "Value", xydataset, true, true,
				false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		NumberAxis numberaxis = new NumberAxis(null);
		numberaxis.setAutoRangeIncludesZero(false);
		xyplot.setRangeAxis(1, numberaxis);
		java.util.List list = Arrays.asList(new Integer[] { new Integer(0), new Integer(1) });
		xyplot.mapDatasetToRangeAxes(0, list);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		xylineandshaperenderer.setAutoPopulateSeriesStroke(false);
		xylineandshaperenderer.setBaseStroke(new BasicStroke(1.5F, 1, 1));
		xylineandshaperenderer.setDrawSeriesLineAsPath(true);
		StandardXYToolTipGenerator standardxytooltipgenerator = new StandardXYToolTipGenerator(
				"{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00"));
		xylineandshaperenderer
				.setBaseToolTipGenerator(standardxytooltipgenerator);
		ChartUtilities.applyCurrentTheme(jfreechart);
		return jfreechart;
	}

	public static JPanel createDemoPanel() {
		JFreeChart jfreechart = createChart(createDataset());
		return new ChartPanel(jfreechart);
	}

	public static void main(String args[]) {
		AggregateScalarTimeSeriesDemo timeseriesdemo8 = new AggregateScalarTimeSeriesDemo(
				"Time Series Demo 8");
		timeseriesdemo8.pack();
		RefineryUtilities.centerFrameOnScreen(timeseriesdemo8);
		timeseriesdemo8.setVisible(true);
	}
}
