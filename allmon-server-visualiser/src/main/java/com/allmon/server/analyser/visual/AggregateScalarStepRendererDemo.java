package com.allmon.server.analyser.visual;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AggregateScalarStepRendererDemo extends ApplicationFrame {

	public AggregateScalarStepRendererDemo(String s) {
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 300));
		setContentPane(jpanel);
	}

	private static JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createXYLineChart(
				"XYStepRendererDemo2", "X", "Y", xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		ValueAxis valueaxis = xyplot.getRangeAxis();
		valueaxis.setUpperMargin(0.14999999999999999D);
		XYStepRenderer xysteprenderer = new XYStepRenderer();
		xysteprenderer.setSeriesStroke(0, new BasicStroke(1.0F));
		xysteprenderer.setSeriesStroke(1, new BasicStroke(1.0F));
		xysteprenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		xysteprenderer.setDefaultEntityRadius(6);
		// xysteprenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		// xysteprenderer.setBaseItemLabelsVisible(true);
		// xysteprenderer.setBaseItemLabelFont(new Font("Dialog", 1, 14));
		xyplot.setRenderer(xysteprenderer);
		return jfreechart;
	}

	private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
			new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });

	private static XYDataset createDataset() {
		AggregateScalarSelector scalarSelector = (AggregateScalarSelector) appContext
				.getBean("scalarSelector");
		List<AggregateScalarData> list = scalarSelector.select("Mem ActualUsed:");

		XYSeries xyseries = new XYSeries("Series 1");

		int i = 0;
		for (AggregateScalarData scalarData : list) {
			xyseries.add(i++, scalarData.AVG);
		}

		// xyseries.add(1.0D, 3D);
		// xyseries.add(2D, 4D);
		// xyseries.add(3D, 2D);
		// xyseries.add(6D, 3D);
		XYSeries xyseries1 = new XYSeries("Series 2");
		xyseries1.add(1.0D, 7D);
		xyseries1.add(2D, 6D);
		xyseries1.add(3D, 9D);
		xyseries1.add(4D, 5D);
		xyseries1.add(6D, 4D);
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		xyseriescollection.addSeries(xyseries);
		xyseriescollection.addSeries(xyseries1);
		return xyseriescollection;
	}

	public static JPanel createDemoPanel() {
		JFreeChart jfreechart = createChart(createDataset());
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setMouseWheelEnabled(true);
		return chartpanel;
	}

	public static void main(String args[]) {
		AggregateScalarStepRendererDemo xysteprendererdemo2 = new AggregateScalarStepRendererDemo(
				"JFreeChart: XYStepRendererDemo2.java");
		xysteprendererdemo2.pack();
		RefineryUtilities.centerFrameOnScreen(xysteprendererdemo2);
		xysteprendererdemo2.setVisible(true);
	}
}
