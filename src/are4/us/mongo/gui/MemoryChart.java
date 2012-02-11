/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.gui;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import are4.us.mongo.core.MongoServerWrapper;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author raygao2000
 */
public class MemoryChart extends JPanel {

    class DataGenerator extends Timer
            implements ActionListener {

        public void actionPerformed(ActionEvent actionevent) {
            DB admin_db = mws.getMongo().getDB("admin");
            long s1 = Long.parseLong(((BasicDBObject) mws.getServerStats(admin_db).get("mem")).get("virtual").toString());
            long s2 = Long.parseLong(((BasicDBObject) mws.getServerStats(admin_db).get("mem")).get("resident").toString());


            addTotalObservation(s2); //time series #2
            addFreeObservation(s1); //time series #1
        }

        DataGenerator(int i) {
            super(i, null);
            addActionListener(this);
        }
    }

    public MemoryChart(MongoServerWrapper mmw) {
        this(30000);
        this.mws = mmw;
    }

    public MemoryChart(int i) {
        super(new BorderLayout());
        total = new TimeSeries("Virtual Memory", org.jfree.data.time.Millisecond.class);
        total.setMaximumItemAge(i);
        free = new TimeSeries("Resident Memory", org.jfree.data.time.Millisecond.class);
        free.setMaximumItemAge(i);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(total); //time series #1
        timeseriescollection.addSeries(free); //time series #2
        DateAxis dateaxis = new DateAxis("Time");
        NumberAxis numberaxis = new NumberAxis("Memory");
        dateaxis.setTickLabelFont(new Font("SansSerif", 0, 12));
        numberaxis.setTickLabelFont(new Font("SansSerif", 0, 12));
        dateaxis.setLabelFont(new Font("SansSerif", 0, 14));
        numberaxis.setLabelFont(new Font("SansSerif", 0, 14));
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer.setSeriesPaint(0, Color.red);
        xylineandshaperenderer.setSeriesPaint(1, Color.green);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(3F, 0, 2));
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(3F, 0, 2));
        XYPlot xyplot = new XYPlot(timeseriescollection, dateaxis, numberaxis, xylineandshaperenderer);
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        dateaxis.setAutoRange(true);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setTickLabelsVisible(true);
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        JFreeChart jfreechart = new JFreeChart("Mongo DB Memory Usage", new Font("SansSerif", 1, 24), xyplot, true);
        jfreechart.setBackgroundPaint(Color.white);
        ChartPanel chartpanel = new ChartPanel(jfreechart, true);
        chartpanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
        add(chartpanel);
    }

    private void addTotalObservation(double d) {
        total.add(new Millisecond(), d);
    }

    private void addFreeObservation(double d) {
        free.add(new Millisecond(), d);
    }

    public static void main(String args[]) {
        JFrame jframe = new JFrame("Memory Usage Demo");
        MemoryChart memoryusagedemo = new MemoryChart(30000);
        jframe.getContentPane().add(memoryusagedemo, "Center");
        jframe.setBounds(200, 120, 600, 280);
        jframe.setVisible(true);
        (memoryusagedemo.new DataGenerator(100)).start();
        jframe.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent) {
                System.exit(0);
            }
        });
    }

    public void startTimer() {
        if (!dg.isRunning()) {
            dg.start();
        }
    }

    public void stopTimer() {
        if ((dg != null) && (dg.isRunning())) {
            dg.stop();
        }
    }
    private TimeSeries total;
    private TimeSeries free;
    private DataGenerator dg = new DataGenerator(100);
    public MongoServerWrapper mws;
}