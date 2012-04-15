package com.allmon.server.analyser.visual;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecondsScalarTimeSeriesDemo extends ApplicationFrame {

	public SecondsScalarTimeSeriesDemo(String s) throws ParseException {
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}

	static int experimentNo = 1;
	static String from  = "2011-12-05 12:40:00";
	static String to =    "2011-12-05 23:00:00";
	
	private static XYDataset createDataset() throws ParseException {
		
//		TimeSeries timeseries = createTimeSeries("Processes Running:", from, to, 1, false);
//		TimeSeries timeseries = createTimeSeries("DiskQueue:", from, to, 1, false);
		TimeSeries timeseries = createTimeSeries("SLA1: 1$ per every extra second over 2sec execution", from, to, 1, false);
//		TimeSeries timeseries = createTimeSeries("Mem UsedPercent:", from, to, 1, false);
//		TimeSeries timeseries = createTimeSeries("Swap PageIn:", from, to, 1, false); // Swap Used:
//		TimeSeries timeseries = createTimeSeries("DiskReads:", from, to, 1, true);
//		TimeSeries timeseries1 = MovingAverage.createMovingAverage(timeseries, "30 day moving average", 30, 30);
		System.out.println("timeseries1...");
		TimeSeries timeseries1 = createTimeSeries("CPU User Time:", from, to, 100, false);
//		TimeSeries timeseries1 = createTimeSeries("DiskWrites:", from, to, 1, true);
//		TimeSeries timeseries1 = createTimeSeries("DiskServiceTime:", from, to, 1, true);
//		TimeSeries timeseries2 = createTimeSeries("Processes Total:", from, to, 1, false);
//		System.out.println("timeseries2...");
//		TimeSeries timeseries2 = createTimeSeries("DiskQueue:", from, to, 1, false);
		
//		System.out.println("timeseries3...volume");
//		TimeSeries timeseries3 = createTimeSeries("ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]", from, to, 1, false);
		
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
		timeseriescollection.addSeries(timeseries);
		timeseriescollection.addSeries(timeseries1);
//		timeseriescollection.addSeries(timeseries2);
//		timeseriescollection.addSeries(timeseries3);
		System.out.println("createDataset end.");
		return timeseriescollection;
	}

	private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
			new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
	
	public static TimeSeries createTimeSeries(String resourceName, String from, String to, double coef, boolean firstDerivative) throws ParseException {
		SecondsScalarSelector scalarSelector = (SecondsScalarSelector)appContext.getBean("secondsScalarSelector");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		List<SecondsScalarData> list = 
			scalarSelector.select(resourceName, sdf.parse(from), sdf.parse(to), firstDerivative);
		
		TimeSeries timeseries = new TimeSeries(resourceName);
		try {
			for (SecondsScalarData scalarData : list) {
				timeseries.add(new Second(sdf.parse(scalarData.SEC)), scalarData.AVG.doubleValue() * coef);
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return timeseries;
	}

	private static JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(
				"Experiment #" + experimentNo, "Time", "Value", xydataset, true, true, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		NumberAxis numberaxis1 = new NumberAxis("CPU%");
		numberaxis1.setAutoRangeIncludesZero(false);
		xyplot.setRangeAxis(1, numberaxis1);
		xyplot.mapDatasetToRangeAxis(1, 1);
			NumberAxis numberaxis2 = new NumberAxis("Queue length");
			numberaxis2.setAutoRangeIncludesZero(false);
			xyplot.setRangeAxis(2, numberaxis2);
			xyplot.mapDatasetToRangeAxis(2, 2);
//		List list = Arrays.asList(new Integer[] { 0, 1, 2 });
//		xyplot.mapDatasetToRangeAxes(0, list);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
		xylineandshaperenderer.setAutoPopulateSeriesStroke(false);
		xylineandshaperenderer.setBaseStroke(new BasicStroke(1.1F, 1, 1));
		xylineandshaperenderer.setDrawSeriesLineAsPath(true);
//		StandardXYToolTipGenerator standardxytooltipgenerator = new StandardXYToolTipGenerator(
//				"{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00"));
//		xylineandshaperenderer.setBaseToolTipGenerator(standardxytooltipgenerator);
		
		
		XYBarRenderer xybarrenderer = new XYBarRenderer(0.20000000000000001D);
//		xybarrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
		xyplot.setRenderer(3, xybarrenderer);
		ChartUtilities.applyCurrentTheme(jfreechart);
		xybarrenderer.setBarPainter(new StandardXYBarPainter());
		xybarrenderer.setShadowVisible(false);

		ChartUtilities.applyCurrentTheme(jfreechart);
		return jfreechart;
	}

	public static JPanel createDemoPanel() {
		JFreeChart jfreechart = null;
		try {
			jfreechart = createChart(createDataset());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ChartPanel(jfreechart);
	}

	public static void main(String args[]) {
		try {
			SecondsScalarTimeSeriesDemo timeseriesdemo8 = new SecondsScalarTimeSeriesDemo(
					"Adaptive Controller in ASM");
			timeseriesdemo8.pack();
			RefineryUtilities.centerFrameOnScreen(timeseriesdemo8);
		timeseriesdemo8.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
