/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.gui;

import are4.us.mongo.object.MongoServerConfigFactory;
import are4.us.mongo.object.MongoServerConfig;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import are4.us.mongo.core.MongoServerWrapper;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

/**
 *
 * @author raygao2000
 */
public class DetailedServerView extends javax.swing.JFrame {

    private MongoServerWrapper mcls;
    private MemoryChart memoryChart;
    private Rotator rrotator;

    /**
     * Creates new form DSView
     */
    public DetailedServerView() throws java.net.UnknownHostException {
        initComponents();
        MongoServerWrapper mcls = new MongoServerWrapper();
        mcls.printDetails();
    }

    public DetailedServerView(String server_alias) {
        initComponents();
        MongoServerConfig mserver = MongoServerConfigFactory.getMongoServerConfig(server_alias);
        try {
            MongoServerWrapper mcls = new MongoServerWrapper(mserver);
            mcls.admin_auth(mserver.getUsername(), mserver.getPassword().toCharArray());
            mcls.printDetails();
        } catch (Exception e) {
        }

    }

    public DetailedServerView(MongoServerWrapper ms) {
        initComponents();
        mcls = ms;

        //Making JTree of the Mongo Server info
        int tree_width = 100;
        int tree_height = 200;
        JTree mongoTree = constructTree(mcls);
        mongoTree.setMinimumSize(new Dimension(tree_width, tree_height));
        mongoTree.setVisible(true);

//        adding to a box layout
        Box b = new Box(BoxLayout.PAGE_AXIS);
        b.add(mongoTree);

        //create a JScrollpane to wrap the box.
        JScrollPane mongoScrollPane = new JScrollPane(mongoTree);
        mongoScrollPane.setLayout(new ScrollPaneLayout());
        mongoScrollPane.add(b);

        //revalidate scroll pane
        mongoScrollPane.revalidate();
        mongoScrollPane.repaint();

        //add it to left pane
        overviewPanel.setMinimumSize(new Dimension(tree_width + 150, tree_height + 100));
        overviewPanel.setLayout(new FlowLayout());
        overviewPanel.add(mongoScrollPane);

        overviewPanel.revalidate();
        overviewPanel.repaint();
    }

    public JTree constructTree(MongoServerWrapper mcls) {
        try {
            //create root
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(mcls.getMongo().toString());
            HashMap<String, DB> dbs = mcls.getDBs();
            for (DB db : dbs.values()) {
                // adding DB & <child collection size> to server root
                DefaultMutableTreeNode dbnode = new DefaultMutableTreeNode(db.getName());
                root.add(dbnode);

                //get db size;
                System.out.println(db.getName() + " size: " + mcls.getDBSize(db));
                for (DBCollection dbcollection : mcls.getCollections(db).values()) {
                    //mcls.getServerStats(db);
                    // adding collection to DB node
                    DefaultMutableTreeNode collection_node = new DefaultMutableTreeNode(dbcollection.getName());
                    dbnode.add(collection_node);

                    System.out.println(dbcollection.getName() + " size: " + mcls.getCollectionSize(dbcollection)
                            + " ratio: " + String.format("%2.04f", mcls.calculateCollectionToDBRatio(dbcollection)) + "%");
                    //showing count of Documents
                    DefaultMutableTreeNode collection_count = new DefaultMutableTreeNode("Document Count: " + dbcollection.getCount());
                    collection_node.add(collection_count);

                    // showing count of Indexes
                    DefaultMutableTreeNode index_count = new DefaultMutableTreeNode("Index Count: " + dbcollection.getIndexInfo().size());
                    collection_node.add(index_count);

                    // build collection index, otherwise will get null pointer later on.
                    mcls.getCollectionIndexes(dbcollection);
                }
                System.out.println("");
            }
            JTree tree = new JTree(root);
            tree.addTreeSelectionListener(updateDetailPaneListener());
            return tree;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private TreeSelectionListener updateDetailPaneListener() {
        return new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                JTree treeSource = (JTree) tse.getSource();
                TreePath path = treeSource.getSelectionPath();
                String node_name = extractNodeName(path);
                switch (path.getPathCount()) {
                    case 1: {
                        doUpdate("root", path, component_name_label, component_detail_info_label);
                        graphicsPanel.removeAll();
                        graphicsPanel.revalidate();
                        graphicsPanel.repaint();
                    }
                    break;// show root;
                    case 2: {
                        doUpdate("db", path, component_name_label, component_detail_info_label);
                        graphicsPanel.removeAll();
                        ChartPanel chartPanel = new ChartPanel(buildChart(path));                        
                        graphicsPanel.add(chartPanel);
                        
                        //add start & stop rotation
                        JCheckBox chartControl = new JCheckBox("Start/Stop");
                        chartControl.setSelected(true);

                        chartControl.addItemListener(chartControlListener());

                        Box b = new Box(BoxLayout.PAGE_AXIS);
                        b.add(chartControl);
                        graphicsPanel.add(b);


                        graphicsPanel.setLayout(new GridLayout());
                        graphicsPanel.revalidate();
                        graphicsPanel.repaint();
                    }
                    break;//show DB
                    case 3: {
                        doUpdate("collection", path, component_name_label, component_detail_info_label);
                        graphicsPanel.removeAll();
                        graphicsPanel.revalidate();
                        graphicsPanel.repaint();
                    }
                    break;//show collection
                    case 4: {
                        doUpdate("leaf", path, component_name_label, component_detail_info_label);
                        graphicsPanel.removeAll();
                        graphicsPanel.revalidate();
                        graphicsPanel.repaint();
                    }
                    break;//show leaf
                    default: {
                        doUpdate("leaf", path, component_name_label, component_detail_info_label);
                        graphicsPanel.removeAll();
                        graphicsPanel.revalidate();
                        graphicsPanel.repaint();
                    }
                    break;//show leaf
                }

            }
        };
    }

    private ItemListener chartControlListener() {
        return new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ise) {
                int state = ise.getStateChange();
                if (state == ItemEvent.SELECTED)
                {
                    //start rotation
                    rrotator.startRotate();
                }
                else
                {
                    // stop rotation
                    rrotator.stopRotate();
                }   
            }
        }; 
    }

    // build a chart.
    public JFreeChart buildChart(TreePath path) {
        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        DB db = mcls.getMongo().getDB(extractNodeName(path));

        for (DBCollection dbcollection : mcls.getCollections(db).values()) {
            //calculate Collection storage to DB ratio
            float ratio = mcls.getCollectionSize(dbcollection);
            data.setValue(dbcollection.getName(), ratio);
        }

        // create a chart...
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Storage Situation of <" + db.getName() + ">",
                data,
                true, // legend?
                true, // tooltips?
                false // URLs?
                );

        //add 3d Plot, transparent chart
//        PiePlot3D plot = ( PiePlot3D )chart.getPlot();
//        plot.setForegroundAlpha( 0.6f );

        //chart.setBackgroundPaint(Color.yellow);
        final PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(270);
        plot.setDirection(Rotation.ANTICLOCKWISE);

        //see my Rotator class from 
        // http://www.hackchina.com/en/r/33921/PieChart3DDemo2.java__html
        Rotator rotator = new Rotator((PiePlot3D) chart.getPlot());
        rrotator = rotator;
        rrotator.startRotate();        

        return chart;
    }

    private String extractNodeName(TreePath path) {
        String node_name = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject().toString();
        return node_name;
    }

    private void doUpdate(String node_type, TreePath path, JLabel comp_name, JTextArea comp_detail) {
        if (node_type.equalsIgnoreCase("root")) {
            comp_name.setText(extractNodeName(path));
            comp_name.revalidate();
            comp_name.repaint();

            comp_detail.setText(mcls.getMongo().toString());
            comp_detail.setLineWrap(true);
            comp_detail.revalidate();
            comp_detail.repaint();

        } else if (node_type.equalsIgnoreCase("db")) {
            comp_name.setText(extractNodeName(path));
            comp_name.revalidate();
            comp_name.repaint();

            //comp_detail.setText(mcls.gethDBs().get(node_name).getStats().toString());
            comp_detail.setText(mcls.getMongo().getDB(extractNodeName(path)).getStats().toString());
            comp_detail.setLineWrap(true);
            comp_detail.revalidate();
            comp_detail.repaint();

        } else if (node_type.equalsIgnoreCase("collection")) {
            String nodename = extractNodeName(path).toString();
            comp_name.setText(nodename);
            comp_name.revalidate();
            comp_name.repaint();

            //prevent HashMap overwrite of data with the same name, hence put it in an extended name space
            String qualified_nodename = ((DefaultMutableTreeNode) path.getLastPathComponent()).getParent().toString()
                    + "/" + nodename;
            comp_detail.setText(mcls.dbcs.get(qualified_nodename).getStats().toString());
            comp_detail.setLineWrap(true);
            comp_name.revalidate();
            comp_name.repaint();

        } else {
            //prevent HashMap overwrite of data with the same name, hence put it in an extended name space
            String nodename = extractNodeName(path).toString();
            comp_name.setText(nodename);
            comp_name.revalidate();
            comp_name.repaint();

            comp_detail.setText("");
            comp_detail.setLineWrap(true);
            comp_detail.revalidate();
            comp_detail.repaint();

        }


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        detailsPanel = new javax.swing.JTabbedPane();
        infoPanel = new javax.swing.JPanel();
        component_name_label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        component_detail_info_label = new javax.swing.JTextArea();
        graphicsPanel = new javax.swing.JPanel();
        serverMemoryPanel = new javax.swing.JPanel();
        overviewPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1100, 630));

        jSplitPane2.setMinimumSize(new java.awt.Dimension(625, 400));

        detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detailed Information"));
        detailsPanel.setMinimumSize(new java.awt.Dimension(300, 400));

        component_name_label.setText("component name");

        component_detail_info_label.setColumns(20);
        component_detail_info_label.setRows(5);
        component_detail_info_label.setEnabled(false);
        jScrollPane1.setViewportView(component_detail_info_label);

        org.jdesktop.layout.GroupLayout graphicsPanelLayout = new org.jdesktop.layout.GroupLayout(graphicsPanel);
        graphicsPanel.setLayout(graphicsPanelLayout);
        graphicsPanelLayout.setHorizontalGroup(
            graphicsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 818, Short.MAX_VALUE)
        );
        graphicsPanelLayout.setVerticalGroup(
            graphicsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 352, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(infoPanelLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(graphicsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(component_name_label)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 757, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(infoPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(component_name_label)
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(graphicsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        detailsPanel.addTab("Detail", infoPanel);

        serverMemoryPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                serverMemoryPanelComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                serverMemoryPanelComponentHidden(evt);
            }
        });

        org.jdesktop.layout.GroupLayout serverMemoryPanelLayout = new org.jdesktop.layout.GroupLayout(serverMemoryPanel);
        serverMemoryPanel.setLayout(serverMemoryPanelLayout);
        serverMemoryPanelLayout.setHorizontalGroup(
            serverMemoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 862, Short.MAX_VALUE)
        );
        serverMemoryPanelLayout.setVerticalGroup(
            serverMemoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 552, Short.MAX_VALUE)
        );

        detailsPanel.addTab("Server Memory", serverMemoryPanel);

        jSplitPane2.setRightComponent(detailsPanel);

        overviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Overview"));
        overviewPanel.setMinimumSize(new java.awt.Dimension(150, 400));

        org.jdesktop.layout.GroupLayout overviewPanelLayout = new org.jdesktop.layout.GroupLayout(overviewPanel);
        overviewPanel.setLayout(overviewPanelLayout);
        overviewPanelLayout.setHorizontalGroup(
            overviewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 138, Short.MAX_VALUE)
        );
        overviewPanelLayout.setVerticalGroup(
            overviewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 598, Short.MAX_VALUE)
        );

        jSplitPane2.setLeftComponent(overviewPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serverMemoryPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_serverMemoryPanelComponentShown
        serverMemoryPanel.removeAll();
        serverMemoryPanel.setLayout(new FlowLayout());

        memoryChart = new MemoryChart(mcls);
        serverMemoryPanel.add(memoryChart, "Center");

        //(memoryChart. new DataGenerator(100)).start();
        memoryChart.startTimer();

        serverMemoryPanel.revalidate();
        serverMemoryPanel.repaint();
        //(memorychart. new MemoryChart.DataGenerator(100)).start();
    }//GEN-LAST:event_serverMemoryPanelComponentShown

    private void serverMemoryPanelComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_serverMemoryPanelComponentHidden

        if (memoryChart != null) {
            memoryChart.stopTimer();
        }
        serverMemoryPanel.removeAll();
        serverMemoryPanel.revalidate();
        serverMemoryPanel.repaint();

    }//GEN-LAST:event_serverMemoryPanelComponentHidden

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DetailedServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetailedServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetailedServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetailedServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    new DetailedServerView().setVisible(true);
                } catch (java.net.UnknownHostException e) {
                    //too error handling
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea component_detail_info_label;
    private javax.swing.JLabel component_name_label;
    private javax.swing.JTabbedPane detailsPanel;
    private javax.swing.JPanel graphicsPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JPanel serverMemoryPanel;
    // End of variables declaration//GEN-END:variables
}
