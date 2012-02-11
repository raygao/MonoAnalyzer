/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.gui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author raygao2000
 */
public class MongoServerIcon extends javax.swing.JButton {

    public MongoServerIcon() {
        super();
        ImageIcon db_icon = new ImageIcon(getClass().getResource("/resources/Hardware-database-icon.png"));
        this.setIcon(db_icon);
    }
}
