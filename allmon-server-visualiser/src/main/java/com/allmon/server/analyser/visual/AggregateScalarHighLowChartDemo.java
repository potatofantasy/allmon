package com.allmon.server.analyser.visual;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AggregateScalarHighLowChartDemo extends ApplicationFrame {

	private static final Calendar calendar = Calendar.getInstance();

	public AggregateScalarHighLowChartDemo(String s) throws ParseException {
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}

	private static Date createDate(int i, int j, int k, int l, int i1) {
		calendar.clear();
		calendar.set(i, j - 1, k, l, i1);
		return calendar.getTime();
	}


	private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
			new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
	
	public static OHLCDataset createDataset() throws ParseException {
		AggregateScalarSelector scalarSelector = (AggregateScalarSelector) appContext.getBean("scalarSelector");
		List<AggregateScalarData> list = scalarSelector.select("Mem ActualUsed:");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // DB: 'YYYY-MM-DD HH24:MI'
		
		Date date[] = new Date[list.size()];
		double high[] = new double[list.size()];
		double low[] = new double[list.size()];
		double open[] = new double[list.size()];
		double close[] = new double[list.size()];
		double volume[] = new double[list.size()];
		
		int index = 0;
		for (AggregateScalarData scalarData : list) {
			date[index] = sdf.parse(scalarData.MI);
			open[index] = close[index] = scalarData.AVG.doubleValue();
			high[index] = scalarData.MAX.doubleValue();
			low[index] = scalarData.MIN.doubleValue();
			volume[index] = 0.0; //scalarData.COUNT.doubleValue();
			index++;
		}
		
		return new DefaultHighLowDataset("Series 1", date, high, low, open, close, volume);
	}

	private static JFreeChart createChart(OHLCDataset ohlcdataset) {
		JFreeChart jfreechart = ChartFactory.createHighLowChart(
				"HighLowChartDemo2", "Time", "Value", ohlcdataset, true);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setNumberFormatOverride(new DecimalFormat("$0.00"));
		org.jfree.data.xy.XYDataset xydataset = MovingAverage
				.createMovingAverage(ohlcdataset, "-MAVG", 0xf731400L, 0L);
		xyplot.setDataset(1, xydataset);
		xyplot.setRenderer(1, new StandardXYItemRenderer());
		return jfreechart;
	}

	public static JPanel createDemoPanel() throws ParseException {
		JFreeChart jfreechart = createChart(createDataset());
		return new ChartPanel(jfreechart);
	}

	public static void main(String args[]) throws ParseException {
		AggregateScalarHighLowChartDemo highlowchartdemo2 = new AggregateScalarHighLowChartDemo(
				"JFreeChart: HighLowChartDemo2.java");
		highlowchartdemo2.pack();
		RefineryUtilities.centerFrameOnScreen(highlowchartdemo2);
		highlowchartdemo2.setVisible(true);
	}

}
