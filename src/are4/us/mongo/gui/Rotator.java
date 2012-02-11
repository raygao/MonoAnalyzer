/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.plot.PiePlot3D;
import javax.swing.Timer;

/**
 *
 * @author raygao2000
 */
class Rotator extends Timer implements ActionListener {

    /**
     * The plot.
     */
    private PiePlot3D plot;
    /**
     * The angle.
     */
    private int angle = 270;

    /**
     * Constructor.
     *
     * @param plot the plot.
     */
    Rotator(final PiePlot3D plot) {
        super(100, null);
        this.plot = plot;
        addActionListener(this);
    }

    /**
     * Modifies the starting angle.
     *
     * @param event the action event.
     */
    public void actionPerformed(final ActionEvent event) {
        this.plot.setStartAngle(this.angle);
        this.angle = this.angle + 1;
        if (this.angle == 360) {
            this.angle = 0;
        }
    }

    public void startRotate() {
        this.start();
    }

    public void stopRotate() {
        this.stop();

    }
}