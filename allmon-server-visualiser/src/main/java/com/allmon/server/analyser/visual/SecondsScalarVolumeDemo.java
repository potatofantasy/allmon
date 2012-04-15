// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package com.allmon.server.analyser.visual;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SecondsScalarVolumeDemo extends ApplicationFrame {

	public SecondsScalarVolumeDemo(String s) {
		super(s);
		JFreeChart jfreechart = null;
		try {
			jfreechart = createChart();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChartPanel chartpanel = new ChartPanel(jfreechart, true, true, true,
				false, true);
		chartpanel.setPreferredSize(new Dimension(700, 400));
		setContentPane(chartpanel);
	}


	static int experimentNo = 1;
	static String from  = "2011-12-05 19:30:00";
	static String to =    "2011-12-05 22:00:00";
	
	private static JFreeChart createChart() throws ParseException {
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
		timeseriescollection.addSeries(
				SecondsScalarTimeSeriesDemo.createTimeSeries("CPU User Time:", from, to, 100, false));
		timeseriescollection.addSeries(
				SecondsScalarTimeSeriesDemo.createTimeSeries("DiskQueue:", from, to, 1, false));
		XYDataset xydataset = timeseriescollection;
		String s = "Experiment #" + experimentNo;
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(s, "Date",
				"CPU% / Queue Length", xydataset, true, true, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setLowerMargin(0.40000000000000002D);
		DecimalFormat decimalformat = new DecimalFormat("00.00");
		numberaxis.setNumberFormatOverride(decimalformat);
		XYItemRenderer xyitemrenderer = xyplot.getRenderer();
		xyitemrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				"{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
		NumberAxis numberaxis1 = new NumberAxis("SLA Penalty Value");
		numberaxis1.setUpperMargin(1.0D);
		xyplot.setRangeAxis(1, numberaxis1);
		TimeSeriesCollection timeseriescollection1 = new TimeSeriesCollection();
//		timeseriescollection1.addSeries(
//				SecondsScalarTimeSeriesDemo.createTimeSeries("ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]", from, to, 1, false));
		timeseriescollection1.addSeries(
				SecondsScalarTimeSeriesDemo.createTimeSeries("SLA1: 1$ per every extra second over 2sec execution", from, to, 1, false));
		xyplot.setDataset(1, timeseriescollection1);
		xyplot.setRangeAxis(1, numberaxis1);
		xyplot.mapDatasetToRangeAxis(1, 1);
		XYBarRenderer xybarrenderer = new XYBarRenderer(0.20000000000000001D);
		xybarrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				"{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"),
				new DecimalFormat("0,000.00")));
		xyplot.setRenderer(1, xybarrenderer);
		ChartUtilities.applyCurrentTheme(jfreechart);
		xybarrenderer.setBarPainter(new StandardXYBarPainter());
		xybarrenderer.setShadowVisible(false);
		return jfreechart;
	}

	public static JPanel createDemoPanel() throws ParseException {
		JFreeChart jfreechart = createChart();
		return new ChartPanel(jfreechart);
	}

	public static void main(String args[]) {
		SecondsScalarVolumeDemo pricevolumedemo1 = new SecondsScalarVolumeDemo(
				"Adaptive Controller in ASM");
		pricevolumedemo1.pack();
		RefineryUtilities.centerFrameOnScreen(pricevolumedemo1);
		pricevolumedemo1.setVisible(true);
	}
}
