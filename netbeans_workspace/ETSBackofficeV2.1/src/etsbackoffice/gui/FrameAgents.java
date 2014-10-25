package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.OfficeID;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;


/**
 *
 * @author Yusuf
 */
public class FrameAgents extends javax.swing.JFrame{
       
    List<Agent> agents = new ArrayList();
    Agent agent;
    AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    
    DefaultTableModel agentModel;
    DefaultTableModel oidModel;            
    
    public FrameAgents(java.awt.Frame parent) {
        initComponents();       
    }     
    

    @Action
    public void newAgent() throws Exception {       
       agent =  agentBo.getNewAgent();
       agent.getOfficeIDs().clear();
        FrameAgent frameAgent = new FrameAgent(this);
        frameAgent.setTitle("New Agent");
        
        if (frameAgent.showAgentDialog(agent)) {
            agentBo.setAgent(agent);
            Thread t = new Thread(new threadSaveAgent());
            t.start();
            try {
                t.join();                
                //new Thread(new threadLoadAllAgent()).start();
                new Thread(new threadSearchAgent(agent.getName(), 1)).start();
            } catch (InterruptedException e) {
                // Thread was interrupted
            }
            
        }
    }

    @Action
    public void editAgent() {
        if (tblAgent.getSelectedRow() != -1) {

            agent = agents.get(tblAgent.getSelectedRow());
            Thread t1 = new Thread(new threadLoadCompleteAgent(agent.getContactableId()));
            t1.start();            
            try {
                t1.join();
                FrameAgent frameAgent = new FrameAgent(this);
                frameAgent.setTitle("Edit Agent");
                if (frameAgent.showAgentDialog(agent)) {
                    Thread t = new Thread(new threadSaveAgent());//Thread to save agent
                    t.start();
                    try {
                        t.join();
                        //new Thread(new threadLoadAgentNameList()).start();
                        //new Thread(new threadLoadAllAgent()).start();
                    } catch (InterruptedException e) {
                        // Thread was interrupted
                    }
                }
            } catch (InterruptedException e) {
                // Thread was interrupted
            }
        }
    }

    @Action
    public void search() {
        String element="";
        int type=0;
        if (!txtName.getText().equals("")) {
            element = txtName.getText();
            type = 1;
        } else if (!txtPostCode.getText().equals("")) {
            element = txtPostCode.getText();
            type = 2;
        } else if (!txtOid.getText().equals("")) {
            element = txtOid.getText();
            type = 3;
        }else{
        element = "";
        type=0;
        }
        if (!element.isEmpty()) {
            new Thread(new threadSearchAgent(element, type)).start();
        }else{
        new Thread(new threadLoadAllAgent()).start();
        }
    }

    private void populateTblAgent(List<Agent> agents) {
        agentModel = (DefaultTableModel) tblAgent.getModel();
        agentModel.getDataVector().removeAllElements();
        tblAgent.repaint();
        if (agents.size() > 0) {
            for (int i = 0; i < agents.size(); i++) {
                agent = new Agent();
                agent.setOfficeIDs(null);//Avoiding lazy loading exception
                agent = agents.get(i);
                agentModel.insertRow(i, new Object[]{agent.getRef(), agent.getName(),
                            agent.getAddLine1(), agent.getPostCode(), agent.getTelNo(), agent.getEmail()});
            }
        }
    }

    private void populateTblOID(List<OfficeID> officeIDs) {

        oidModel = (DefaultTableModel) tblOfficeID.getModel();
        oidModel.getDataVector().removeAllElements();
        oidModel.setRowCount(6);
        tblOfficeID.repaint();

        List<OfficeID> aOfficeID = new ArrayList();
        List<OfficeID> gOfficeID = new ArrayList();
        List<OfficeID> sOfficeID = new ArrayList();
        List<OfficeID> wOfficeID = new ArrayList();

        for (int i = 0; i < officeIDs.size(); i++) {
            if (officeIDs.get(i).getGds().getGdsId() == 1) {
                aOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 2) {
                gOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 3) {
                sOfficeID.add(officeIDs.get(i));
            } else if (officeIDs.get(i).getGds().getGdsId() == 4) {
                wOfficeID.add(officeIDs.get(i));
            }
        }
        for (int col = 0; col < 4; col++) {
            if (col == 0) {
                for (int row = 0; row < aOfficeID.size(); row++) {
                    oidModel.setValueAt(aOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 1) {
                for (int row = 0; row < gOfficeID.size(); row++) {
                    oidModel.setValueAt(gOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 2) {
                for (int row = 0; row < sOfficeID.size(); row++) {
                    oidModel.setValueAt(sOfficeID.get(row).getOfficeID(), row, col);
                }
            } else if (col == 3) {
                for (int row = 0; row < wOfficeID.size(); row++) {
                    oidModel.setValueAt(wOfficeID.get(row).getOfficeID(), row, col);
                }
            }
        }
    }

    private void populateAgentDetails() {        
        txtTelNo1.setText(agent.getTelNo1());
        txtMobile.setText(agent.getMobile());
        txtEmail.setText(agent.getEmail());
        txtFax.setText(agent.getFax());
        txtWeb.setText(agent.getWeb());
        txtIATA.setText(agent.getIata());
        txtATOL.setText(agent.getAtol());

        txtCreatedBy.setText(agent.getCreatedBy().getSurName());
        txtCreatedOn.setText(String.valueOf(agent.getCreatedOn()));
        if (agent.getAddLine1() != null) {
            txtAddress.setText(agent.getAddLine1() + '\n');
        }
        if (agent.getAddLine2() != null) {
            txtAddress.append(agent.getAddLine2() + '\n');
        }
        if (agent.getCity() != null) {
            txtAddress.append(agent.getCity() + '\n');
        }
        if (agent.getProvince() != null) {
            txtAddress.append(agent.getProvince() + '\n');
        }
        if (agent.getPostCode() != null) {
            txtAddress.append(agent.getPostCode() + '\n');
        }
        txtRemark.setText(agent.getRemark());
        populateTblOID(agent.getOfficeIDs());
        /*Thread t2 = new Thread(new thread2(agent));
            t2.start();

            try {
                t2.join();
                agent.setOfficeIDs(officeIDs);
                populateTblOID(agent.getOfficeIDs());
            } catch (InterruptedException e) {
                // Thread was interrupted
            }*/
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();
        btnNewAgent = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        splitPane1 = new javax.swing.JSplitPane();
        searchPanel = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblOID = new javax.swing.JLabel();
        lblPostCode = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        pagePanel = new javax.swing.JPanel();
        lblTotalRecord = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        lblTotalRecord1 = new javax.swing.JLabel();
        lblCurrentRecord = new javax.swing.JLabel();
        lblCurrentRecord1 = new javax.swing.JLabel();
        txtPostCode = new javax.swing.JTextField();
        txtOid = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        lblAddress = new javax.swing.JLabel();
        lblTelNo1 = new javax.swing.JLabel();
        lblMobile = new javax.swing.JLabel();
        txtTelNo1 = new javax.swing.JTextField();
        txtMobile = new javax.swing.JTextField();
        lblFax = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblIATA = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtRemark = new javax.swing.JTextArea();
        txtFax = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtIATA = new javax.swing.JTextField();
        lblATOL = new javax.swing.JLabel();
        lblWeb = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        lblCreatedBy = new javax.swing.JLabel();
        txtATOL = new javax.swing.JTextField();
        txtWeb = new javax.swing.JTextField();
        txtCreatedOn = new javax.swing.JTextField();
        txtCreatedBy = new javax.swing.JTextField();
        lblCreatedOn = new javax.swing.JLabel();
        JScrollpane = new javax.swing.JScrollPane();
        tblOfficeID = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAgent = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameAgents.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 550));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.GridBagLayout());

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        statusPanel.add(statusMessageLabel, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        statusPanel.add(progressBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(statusPanel, gridBagConstraints);

        toolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N
        toolBar.setPreferredSize(new java.awt.Dimension(51, 35));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameAgents.class, this);
        btnNewAgent.setAction(actionMap.get("newAgent")); // NOI18N
        btnNewAgent.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        btnNewAgent.setIcon(resourceMap.getIcon("btnNewAgent.icon")); // NOI18N
        btnNewAgent.setText(resourceMap.getString("btnNewAgent.text")); // NOI18N
        btnNewAgent.setFocusable(false);
        btnNewAgent.setMinimumSize(new java.awt.Dimension(89, 35));
        btnNewAgent.setName("btnNewAgent"); // NOI18N
        toolBar.add(btnNewAgent);

        jButton1.setAction(actionMap.get("editAgent")); // NOI18N
        jButton1.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        toolBar.add(jButton1);

        jButton3.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setName("jButton3"); // NOI18N
        toolBar.add(jButton3);

        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setName("jButton4"); // NOI18N
        toolBar.add(jButton4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        getContentPane().add(toolBar, gridBagConstraints);

        splitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitPane1.setDividerLocation(230);
        splitPane1.setDividerSize(4);
        splitPane1.setForeground(resourceMap.getColor("splitPane1.foreground")); // NOI18N
        splitPane1.setName("splitPane1"); // NOI18N

        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("searchPanel.border.title"))); // NOI18N
        searchPanel.setName("searchPanel"); // NOI18N
        searchPanel.setLayout(new java.awt.GridBagLayout());

        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        searchPanel.add(lblName, gridBagConstraints);

        lblOID.setText(resourceMap.getString("lblOID.text")); // NOI18N
        lblOID.setName("lblOID"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        searchPanel.add(lblOID, gridBagConstraints);

        lblPostCode.setText(resourceMap.getString("lblPostCode.text")); // NOI18N
        lblPostCode.setName("lblPostCode"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        searchPanel.add(lblPostCode, gridBagConstraints);

        btnSearch.setAction(actionMap.get("search")); // NOI18N
        btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 13, 0);
        searchPanel.add(btnSearch, gridBagConstraints);

        pagePanel.setBackground(resourceMap.getColor("pagePanel.background")); // NOI18N
        pagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("pagePanel.border.lineColor"))); // NOI18N
        pagePanel.setName("pagePanel"); // NOI18N
        pagePanel.setPreferredSize(new java.awt.Dimension(388, 20));
        pagePanel.setLayout(new java.awt.GridBagLayout());

        lblTotalRecord.setForeground(resourceMap.getColor("lblTotalRecord.foreground")); // NOI18N
        lblTotalRecord.setText(resourceMap.getString("lblTotalRecord.text")); // NOI18N
        lblTotalRecord.setName("lblTotalRecord"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        pagePanel.add(lblTotalRecord, gridBagConstraints);

        btnNext.setText(resourceMap.getString("btnNext.text")); // NOI18N
        btnNext.setName("btnNext"); // NOI18N
        btnNext.setPreferredSize(new java.awt.Dimension(41, 20));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pagePanel.add(btnNext, gridBagConstraints);

        btnPrevious.setText(resourceMap.getString("btnPrevious.text")); // NOI18N
        btnPrevious.setName("btnPrevious"); // NOI18N
        btnPrevious.setPreferredSize(new java.awt.Dimension(41, 20));
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pagePanel.add(btnPrevious, gridBagConstraints);

        lblTotalRecord1.setForeground(resourceMap.getColor("lblTotalRecord1.foreground")); // NOI18N
        lblTotalRecord1.setText(resourceMap.getString("lblTotalRecord1.text")); // NOI18N
        lblTotalRecord1.setName("lblTotalRecord1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        pagePanel.add(lblTotalRecord1, gridBagConstraints);

        lblCurrentRecord.setForeground(resourceMap.getColor("lblCurrentRecord.foreground")); // NOI18N
        lblCurrentRecord.setText(resourceMap.getString("lblCurrentRecord.text")); // NOI18N
        lblCurrentRecord.setName("lblCurrentRecord"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        pagePanel.add(lblCurrentRecord, gridBagConstraints);

        lblCurrentRecord1.setForeground(resourceMap.getColor("lblCurrentRecord1.foreground")); // NOI18N
        lblCurrentRecord1.setText(resourceMap.getString("lblCurrentRecord1.text")); // NOI18N
        lblCurrentRecord1.setName("lblCurrentRecord1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        pagePanel.add(lblCurrentRecord1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        searchPanel.add(pagePanel, gridBagConstraints);

        txtPostCode.setText(resourceMap.getString("txtPostCode.text")); // NOI18N
        txtPostCode.setName("txtPostCode"); // NOI18N
        txtPostCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPostCodeKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        searchPanel.add(txtPostCode, gridBagConstraints);

        txtOid.setText(resourceMap.getString("txtOid.text")); // NOI18N
        txtOid.setName("txtOid"); // NOI18N
        txtOid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtOidKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        searchPanel.add(txtOid, gridBagConstraints);

        txtName.setText(resourceMap.getString("txtName.text")); // NOI18N
        txtName.setName("txtName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        searchPanel.add(txtName, gridBagConstraints);

        splitPane1.setLeftComponent(searchPanel);

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerSize(4);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setLastDividerLocation(200);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setPreferredSize(new java.awt.Dimension(749, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 300));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblAddress.setText(resourceMap.getString("lblAddress.text")); // NOI18N
        lblAddress.setName("lblAddress"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblAddress, gridBagConstraints);

        lblTelNo1.setText(resourceMap.getString("lblTelNo1.text")); // NOI18N
        lblTelNo1.setName("lblTelNo1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblTelNo1, gridBagConstraints);

        lblMobile.setText(resourceMap.getString("lblMobile.text")); // NOI18N
        lblMobile.setName("lblMobile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblMobile, gridBagConstraints);

        txtTelNo1.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtTelNo1.setEditable(false);
        txtTelNo1.setText(resourceMap.getString("txtTelNo1.text")); // NOI18N
        txtTelNo1.setName("txtTelNo1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtTelNo1, gridBagConstraints);

        txtMobile.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtMobile.setEditable(false);
        txtMobile.setText(resourceMap.getString("txtMobile.text")); // NOI18N
        txtMobile.setName("txtMobile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtMobile, gridBagConstraints);

        lblFax.setText(resourceMap.getString("lblFax.text")); // NOI18N
        lblFax.setName("lblFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblFax, gridBagConstraints);

        lblEmail.setText(resourceMap.getString("lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblEmail, gridBagConstraints);

        lblIATA.setText(resourceMap.getString("lblIATA.text")); // NOI18N
        lblIATA.setName("lblIATA"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblIATA, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtAddress.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtAddress.setColumns(20);
        txtAddress.setEditable(false);
        txtAddress.setRows(5);
        txtAddress.setName("txtAddress"); // NOI18N
        jScrollPane2.setViewportView(txtAddress);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtRemark.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtRemark.setColumns(20);
        txtRemark.setEditable(false);
        txtRemark.setLineWrap(true);
        txtRemark.setRows(5);
        txtRemark.setName("txtRemark"); // NOI18N
        jScrollPane3.setViewportView(txtRemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        txtFax.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtFax.setEditable(false);
        txtFax.setText(resourceMap.getString("txtFax.text")); // NOI18N
        txtFax.setName("txtFax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtFax, gridBagConstraints);

        txtEmail.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtEmail.setEditable(false);
        txtEmail.setText(resourceMap.getString("txtEmail.text")); // NOI18N
        txtEmail.setName("txtEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtEmail, gridBagConstraints);

        txtIATA.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtIATA.setEditable(false);
        txtIATA.setText(resourceMap.getString("txtIATA.text")); // NOI18N
        txtIATA.setName("txtIATA"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtIATA, gridBagConstraints);

        lblATOL.setText(resourceMap.getString("lblATOL.text")); // NOI18N
        lblATOL.setName("lblATOL"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblATOL, gridBagConstraints);

        lblWeb.setText(resourceMap.getString("lblWeb.text")); // NOI18N
        lblWeb.setName("lblWeb"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblWeb, gridBagConstraints);

        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblRemark, gridBagConstraints);

        lblCreatedBy.setText(resourceMap.getString("lblCreatedBy.text")); // NOI18N
        lblCreatedBy.setName("lblCreatedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblCreatedBy, gridBagConstraints);

        txtATOL.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtATOL.setEditable(false);
        txtATOL.setText(resourceMap.getString("txtATOL.text")); // NOI18N
        txtATOL.setName("txtATOL"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtATOL, gridBagConstraints);

        txtWeb.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtWeb.setEditable(false);
        txtWeb.setText(resourceMap.getString("txtWeb.text")); // NOI18N
        txtWeb.setName("txtWeb"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtWeb, gridBagConstraints);

        txtCreatedOn.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtCreatedOn.setEditable(false);
        txtCreatedOn.setText(resourceMap.getString("txtCreatedOn.text")); // NOI18N
        txtCreatedOn.setName("txtCreatedOn"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtCreatedOn, gridBagConstraints);

        txtCreatedBy.setBackground(resourceMap.getColor("txtCreatedBy.background")); // NOI18N
        txtCreatedBy.setEditable(false);
        txtCreatedBy.setText(resourceMap.getString("txtCreatedBy.text")); // NOI18N
        txtCreatedBy.setName("txtCreatedBy"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtCreatedBy, gridBagConstraints);

        lblCreatedOn.setText(resourceMap.getString("lblCreatedOn.text")); // NOI18N
        lblCreatedOn.setName("lblCreatedOn"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(lblCreatedOn, gridBagConstraints);

        JScrollpane.setName("JScrollpane"); // NOI18N

        tblOfficeID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Amadeus", "Galileo", "Worldspan", "Sabre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOfficeID.setName("tblOfficeID"); // NOI18N
        JScrollpane.setViewportView(tblOfficeID);
        tblOfficeID.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblOfficeID.columnModel.title0")); // NOI18N
        tblOfficeID.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblOfficeID.columnModel.title1")); // NOI18N
        tblOfficeID.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblOfficeID.columnModel.title2")); // NOI18N
        tblOfficeID.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblOfficeID.columnModel.title3")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(JScrollpane, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblAgent.setAutoCreateRowSorter(true);
        tblAgent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Address", "PostCode", "TelNo", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAgent.setName("tblAgent"); // NOI18N
        tblAgent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAgentMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAgent);
        tblAgent.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title0")); // NOI18N
        tblAgent.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title1")); // NOI18N
        tblAgent.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title2")); // NOI18N
        tblAgent.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title3")); // NOI18N
        tblAgent.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title4")); // NOI18N
        tblAgent.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblAgent.columnModel.title5")); // NOI18N

        jSplitPane1.setTopComponent(jScrollPane1);

        splitPane1.setRightComponent(jSplitPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(splitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
}//GEN-LAST:event_btnNextActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
}//GEN-LAST:event_btnPreviousActionPerformed

    private void tblAgentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAgentMouseClicked
        if (evt.getClickCount() == 2) {
            agent = agents.get(tblAgent.getSelectedRow());                       
            new Thread(new threadLoadCompleteAgent(agent.getContactableId())).start();
        }
    }//GEN-LAST:event_tblAgentMouseClicked

    private void txtPostCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPostCodeKeyPressed
        //cmbName.setSelectedIndex(-1);
        //txtOid.setText("");
    }//GEN-LAST:event_txtPostCodeKeyPressed

    private void txtOidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOidKeyPressed
        //cmbName.setSelectedIndex(-1);
       // txtPostCode.setText("");
    }//GEN-LAST:event_txtOidKeyPressed
    /**
     * @param args the command line arguments
     */
   /* public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {

    public void run() {
    new FrameAgents().setVisible(true);
    }
    });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollpane;
    private javax.swing.JButton btnNewAgent;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblATOL;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblCreatedBy;
    private javax.swing.JLabel lblCreatedOn;
    private javax.swing.JLabel lblCurrentRecord;
    private javax.swing.JLabel lblCurrentRecord1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFax;
    private javax.swing.JLabel lblIATA;
    private javax.swing.JLabel lblMobile;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOID;
    private javax.swing.JLabel lblPostCode;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblTelNo1;
    private javax.swing.JLabel lblTotalRecord;
    private javax.swing.JLabel lblTotalRecord1;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JPanel pagePanel;
    private static javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JSplitPane splitPane1;
    private static javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private static javax.swing.JTable tblAgent;
    private static javax.swing.JTable tblOfficeID;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField txtATOL;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCreatedBy;
    private javax.swing.JTextField txtCreatedOn;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtIATA;
    private javax.swing.JTextField txtMobile;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOid;
    private javax.swing.JTextField txtPostCode;
    private javax.swing.JTextArea txtRemark;
    private javax.swing.JTextField txtTelNo1;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables
    

    //The thread
    private long startTime;
    private long stopTime;
    private float elapsedTime;

   private class threadSaveAgent implements Runnable {

        public threadSaveAgent() {
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Saving Agent...");
            agentBo.save(agent);
            
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }

    
    
        public class threadLoadAllAgent implements Runnable {
        
        public threadLoadAllAgent() {           
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Agents...");
            agents = agentBo.loadAll();
            statusMessageLabel.setText("Populating Agents...");
            populateTblAgent(agents);
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
        
       private class threadSearchAgent implements Runnable {

        String element;
        int type;

        public threadSearchAgent(String element, int type) {
            this.element = element;
            this.type = type;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Searching Agent...");
            agents = agentBo.findAgent(element, type);
            if(agents.size()>0){
            statusMessageLabel.setText("Populating Agent...");
            populateTblAgent(agents);
            }else{
            statusMessageLabel.setText("No Agent found...");
            }
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
       private class threadLoadCompleteAgent implements Runnable {

        private long id;

        public threadLoadCompleteAgent(Long id) {
            this.id = id;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Loading Agent details...");
            agent = agentBo.findCompleteAgentByID(id);

            statusMessageLabel.setText("Populating Agent details...");
            populateAgentDetails(); 
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
        }
    }
}
