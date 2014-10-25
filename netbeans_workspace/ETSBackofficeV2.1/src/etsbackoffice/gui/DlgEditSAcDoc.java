/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * dlgEditSAcDoc.java
 *
 * Created on 16-Apr-2011, 01:51:34
 */

package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.BackofficeReporting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
public class DlgEditSAcDoc extends javax.swing.JDialog {

   private boolean saveNeeded;
   private boolean close;
    DefaultComboBoxModel cmbAdditionalSTitleModel;
    JComboBox cmbAdditionalSTitle;
    private DefaultTableModel tblAdditionalInvLineModel,tblTicketModel;
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private AccountingDocBo acDocBo = (AccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("acDocBo");
    PNRBo pnrBo = (PNRBo) ETSBackofficeApp.getApplication().ctx.getBean("pnrBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private DateFormat df = new DateFormat();

    private List<OtherService> additionalServices = new ArrayList();
    private List<Customer> searchedCustomer = new ArrayList();
    private List<Agent> searchedAgent = new ArrayList();

    private AccountingDocument acDoc;
    private List<AccountingDocumentLine> ticketLines = new ArrayList<AccountingDocumentLine>();
    private List<AccountingDocumentLine> additionalLines = new ArrayList<AccountingDocumentLine>();
    private List<Ticket> tickets = new ArrayList<Ticket>();
    
    public DlgEditSAcDoc(java.awt.Frame parent) {
        super(parent, "Invoice", true);
        initComponents();
    }

    @Action
    public void Close() {
        close = true;
        dispose();
    }
    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
        }
        if (this.saveNeeded == true) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    public boolean showDialog(AccountingDocument acDoc) {
        this.acDoc = acDoc;
        //initControlls();


        initTblAdditionalInvLine();
        initTblTicket();
        populateCmbSTitle();

        populateDocumentHeader();
        populateDocument();

        populateTblTicket();
        populatetblAdditionalInvLine();

        populateTblTransaction(this.acDoc.getAcTransactions());
        setSaveNeeded(false);
        setLocationRelativeTo(this);
        setVisible(true);
        toFront();                

        close = false;        

        if (close) {
            acDoc = this.acDoc;
        }
        return close;
    }


    private void initTblAdditionalInvLine() {
        tblAdditionalInvLineModel = (DefaultTableModel) tblAdditionalInvLine.getModel();
        TableColumn sTitle, sChg;
        sTitle = tblAdditionalInvLine.getColumnModel().getColumn(0);
        sChg = tblAdditionalInvLine.getColumnModel().getColumn(1);


        JTextField jtf1 = new JTextField();
        jtf1.setDocument(new CheckInput(CheckInput.FLOAT));
        sChg.setCellEditor(new DefaultCellEditor(jtf1));


        cmbAdditionalSTitle = new JComboBox();
        sTitle.setCellEditor(new DefaultCellEditor(cmbAdditionalSTitle));
    }

    private void initTblTicket() {
        tblTicketModel = (DefaultTableModel) tblTicket.getModel();
        TableColumn gFare, disc, atol;
        gFare = tblTicket.getColumnModel().getColumn(4);
        disc = tblTicket.getColumnModel().getColumn(5);
        atol = tblTicket.getColumnModel().getColumn(6);

        JTextField jtf2 = new JTextField();
        jtf2.setDocument(new CheckInput(CheckInput.FLOAT));
        gFare.setCellEditor(new DefaultCellEditor(jtf2));
        disc.setCellEditor(new DefaultCellEditor(jtf2));
        atol.setCellEditor(new DefaultCellEditor(jtf2));
    }

    private void populateCmbSTitle() {
        Thread t = new Thread(new threadLoadAllServices());
        t.start();
        try {
            t.join();
            List cmbAdditionalElement = new ArrayList();

            for (OtherService o : this.additionalServices) {
              cmbAdditionalElement.add(o.getServiceTitle());
            }

            cmbAdditionalSTitleModel = new DefaultComboBoxModel(cmbAdditionalElement.toArray());
            cmbAdditionalSTitle.setModel(cmbAdditionalSTitleModel);
            cmbAdditionalSTitle.addActionListener(cmbAdditionalSTitleListener);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgEditSAcDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void searchContactable() {
        String searchElement = txtContactableSearch.getText().trim();

        if (!searchElement.isEmpty()) {
            if (rdoAgent.isSelected()) {
                searchedAgent = new ArrayList();
                searchedAgent = agentBo.searchAgents(searchElement);

                if (searchedAgent.size() == 1) {
                    this.acDoc.getPnr().setAgent(searchedAgent.get(0));
                    this.acDoc.getPnr().setCustomer(null);
                    setSaveNeeded(true);
                    populateTxtAgentDetails(this.acDoc.getPnr().getAgent());
                    busyLabel.setText("");
                } else if (searchedAgent.size() > 1) {
                    busyLabel.setText("Multiple Client Found");
                    cmbSearchResult.setEnabled(true);
                    List cmbSearchResultElement = new ArrayList();
                    for (int i = 0; i < searchedAgent.size(); i++) {
                        cmbSearchResultElement.add(searchedAgent.get(i).getName() + "-" + searchedAgent.get(i).getPostCode()
                                + "-" + searchedAgent.get(i).getContactableId());
                    }

                    Collections.sort(cmbSearchResultElement);
                    DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                    cmbSearchResult.setModel(cmbSearchResultModel);
                } else {//No Agent found using officeID now perform a search by name like
                    searchedAgent = agentBo.searchByAgtNameLike(searchElement);
                    if (searchedAgent.isEmpty()) {
                        busyLabel.setText("No agent found...");
                        //newAgent();
                    } else if (searchedAgent.size() > 1) {
                        busyLabel.setText("Multiple Client Found");
                        cmbSearchResult.setEnabled(true);
                        List cmbSearchResultElement = new ArrayList();
                        for (int i = 0; i < searchedAgent.size(); i++) {
                            cmbSearchResultElement.add(searchedAgent.get(i).getName() + "-" + searchedAgent.get(i).getPostCode()
                                    + "-" + searchedAgent.get(i).getContactableId());
                        }

                        Collections.sort(cmbSearchResultElement);
                        DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                        cmbSearchResult.setModel(cmbSearchResultModel);
                    } else if (searchedAgent.size() == 1) {
                        if (this.acDoc.getPnr() != null) {
                            this.acDoc.getPnr().setAgent(searchedAgent.get(0));
                            this.acDoc.getPnr().setCustomer(null);
                        }
                        setSaveNeeded(true);
                        populateTxtAgentDetails(this.acDoc.getPnr().getAgent());
                        busyLabel.setText("");
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                searchedCustomer = new ArrayList();
                searchedCustomer = customerBo.searchCustomerForeNameLike(searchElement);

                if (searchedCustomer.size() == 1) {
                    this.acDoc.getPnr().setCustomer(searchedCustomer.get(0));
                    this.acDoc.getPnr().setAgent(null);
                    setSaveNeeded(true);
                    populateTxtCustomerDetails(this.acDoc.getPnr().getCustomer());
                    busyLabel.setText("");

                } else if (searchedCustomer.size() > 1) {
                    busyLabel.setText("Multiple Client Found");
                    cmbSearchResult.setEnabled(true);
                    List cmbSearchResultElement = new ArrayList();

                    for (int i = 0; i < searchedCustomer.size(); i++) {
                        cmbSearchResultElement.add(searchedCustomer.get(i).getSurName() + "/"
                                + searchedCustomer.get(i).getForeName() + "-" + searchedCustomer.get(i).getPostCode()
                                + "-" + searchedCustomer.get(i).getContactableId());
                    }

                    Collections.sort(cmbSearchResultElement);
                    DefaultComboBoxModel cmbSearchResultModel = new DefaultComboBoxModel(cmbSearchResultElement.toArray());
                    cmbSearchResult.setModel(cmbSearchResultModel);
                } else {
                    busyLabel.setText("No result, add new client...");
                    //newCustomer();
                }
            }
        } else {
            busyLabel.setText("No keyword to search");
        }
    }

    private void populateTxtAgentDetails(Agent agent) {
       txtContactableDetails.setText(agent.getFullAddressCRSeperated());   
    }

    private void populateTxtCustomerDetails(Customer customer) {
       txtContactableDetails.setText(customer.getFullAddressCRSeperated());    
    }

    private void populateTblTicket() {
        tblTicketModel = (DefaultTableModel) tblTicket.getModel();
        tblTicketModel.getDataVector().removeAllElements();
        tblTicket.repaint();

        Iterator it = this.tickets.iterator();
        int row = 0;
        while (it.hasNext()) {
            Ticket ticket = (Ticket) it.next();

            tblTicketModel.insertRow(row, new Object[]{ticket.getFullPaxNameWithPaxNo(),
                        ticket.getFullTicketNo(), ticket.getTktStatusString(),
                        ticket.getDocIssuedate(), ticket.getGrossFare(),
                        ticket.getDiscount(), ticket.getAtolChg(), ticket.getNetPayble()});
            row++;

        }
        tblTicketModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 4) {
                        String newGFare = tblTicket.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (tickets.get(row).getTktStatus() == 4) {
                            tickets.get(row).setGrossFare(new BigDecimal(newGFare).negate());
                        } else {
                            tickets.get(row).setGrossFare(new BigDecimal(newGFare));
                        }
                    }

                    if (column == 5) {
                        String newDisc = tblTicket.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (tickets.get(row).getTktStatus() == 4) {
                            tickets.get(row).setDiscount(new BigDecimal(newDisc).negate());
                        } else {
                            tickets.get(row).setDiscount(new BigDecimal(newDisc));
                        }
                    }

                    if (column == 6) {
                        String newAtol = tblTicket.getValueAt(row, column).toString().replaceAll("[^.0-9]", "");
                        if (tickets.get(row).getTktStatus() == 4) {
                            tickets.get(row).setAtolChg(new BigDecimal(newAtol).negate());
                        } else {
                            tickets.get(row).setAtolChg(new BigDecimal(newAtol));
                        }
                    }
                    populateTblTicket();
                    populateDocument();
                    setSaveNeeded(true);
                }
            }
        });
    }

    private void populatetblAdditionalInvLine() {
        tblAdditionalInvLineModel = (DefaultTableModel) tblAdditionalInvLine.getModel();
        tblAdditionalInvLineModel.getDataVector().removeAllElements();
        tblAdditionalInvLine.repaint();
        Iterator it = this.additionalLines.iterator();
        int row = 0;
        while (it.hasNext()) {
            AccountingDocumentLine line = (AccountingDocumentLine) it.next();


            tblAdditionalInvLineModel.insertRow(row, new Object[]{line.getRemark(),
                        line.getAmount()});
            row++;

        }

        tblAdditionalInvLineModel.addRow(new Object[]{"", ""});

        tblAdditionalInvLineModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if (column == 1) {
                        String sCharge = tblAdditionalInvLine.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && additionalLines.size() > row) {
                            additionalLines.get(row).setAmount(new BigDecimal(sCharge));
                            setSaveNeeded(true);
                        }
                    }

                    populatetblAdditionalInvLine();
                    populateDocument();
                }
            }
        });
    }

    private void populateTblTransaction(Set<AcTransaction> transactions) {
        DefaultTableModel transModel = (DefaultTableModel) tblTransaction.getModel();
        transModel.getDataVector().removeAllElements();

        for (AcTransaction t : transactions) {
            int row = 0;
            transModel.insertRow(row, new Object[]{df.dateForGui(t.getTransDate()), t.getTransTypeString(), t.getTransAmount(), t.getTransRef()});
            row++;
        }
        txtTransBalance.setText(this.acDoc.getTotalTransactionAmount().toString());
    }

    private void populateDocument() {
        tickets = new ArrayList(acDoc.getTickets());
        additionalLines = new ArrayList(acDoc.getAdditionalServiceLine());
        ticketLines = new ArrayList(acDoc.getTicketLine());

        BigDecimal tktSubTotal = new BigDecimal("0.00");
        BigDecimal oSubTotal = new BigDecimal("0.00");
        BigDecimal transBalance = new BigDecimal("0.00");

        tktSubTotal = this.acDoc.getTktdSubTotal();
        oSubTotal = this.acDoc.getAdditionalServiceSubTotal();
        transBalance = this.acDoc.getTotalTransactionAmount();

        txtTSubTotal.setText(tktSubTotal.toString());
        txtOSubTotal.setText(oSubTotal.toString());
        txtInvAmount.setText(tktSubTotal.add(oSubTotal).toString());
        txtTransBalance.setText(this.acDoc.getTotalTransactionAmount().toString());
        txtBalance.setText(tktSubTotal.add(oSubTotal).subtract(transBalance).toString());
    }

    private void populateDocumentHeader() {
        if (this.acDoc.getPnr().getCustomer() != null) {
            populateTxtCustomerDetails(this.acDoc.getPnr().getCustomer());
        } else if (this.acDoc.getPnr().getAgent() != null) {
            populateTxtAgentDetails(this.acDoc.getPnr().getAgent());
        }

        if (this.acDoc.getAcDocRef() != null) {
            datePicker.setDate(this.acDoc.getIssueDate());
            txtInvRef.setText(this.acDoc.getAcDocRefString());
            txtUser.setText(this.acDoc.getAcDocIssuedByName());
        }
        cmbTerms.setSelectedItem(this.acDoc.getTerms());
    }

    private ActionListener cmbSearchResultListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                String[] data = cmbSearchResult.getSelectedItem().toString().split("-");
                long id = Long.parseLong(data[2]);

                if (rdoAgent.isSelected()) {
                    for (int i = 0; i < searchedAgent.size(); i++) {
                        if (searchedAgent.get(i).getContactableId() == id) {
                            //agent = searchedAgent.get(i);
                            acDoc.getPnr().setAgent(searchedAgent.get(i));
                            acDoc.getPnr().setCustomer(null);
                            populateTxtAgentDetails(acDoc.getPnr().getAgent());
                            setSaveNeeded(true);
                        }
                    }
                } else if (rdoCustomer.isSelected()) {
                    for (int i = 0; i < searchedCustomer.size(); i++) {
                        if (searchedCustomer.get(i).getContactableId() == id) {
                            //customer = searchedCustomer.get(i);
                            acDoc.getPnr().setCustomer(searchedCustomer.get(i));
                            acDoc.getPnr().setAgent(null);
                            populateTxtCustomerDetails(acDoc.getPnr().getCustomer());
                            setSaveNeeded(true);
                        }
                    }
                }
            }
        }
    };

    private ActionListener cmbAdditionalSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblAdditionalInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                if (row > additionalLines.size() - 1) {// Line does not exist, so we enter a new line
                    AccountingDocumentLine line = new AccountingDocumentLine();
                    Services s = new Services();
                    s.setServiceTitle(additionalServices.get(cmbSTitleIndex).getServiceTitle());
                    s.setServiceCharge(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(acDoc.getTotalDocumentedAmount()));
                    s.setOtherService(additionalServices.get(cmbSTitleIndex));
                    s.setPnr(acDoc.getPnr());
                    
                    //additionalLines.add(line);
                    acDoc.addLine(line);
                } else { //Line exists, so we update line

                    for (AccountingDocumentLine l : acDoc.getAccountingDocumentLines()) {
                        if (l.getAcDocLineId() == additionalLines.get(row).getAcDocLineId()) {
                            l.setRemark(additionalServices.get(cmbSTitleIndex).getServiceTitle());
                            l.setAmount(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(acDoc.getTotalDocumentedAmount()));
                            //l.setOtherService(additionalServices.get(cmbSTitleIndex));
                            l.setType(2);
                            l.setAccountingDocument(acDoc);
                        }
                    }
                }
                setSaveNeeded(true);
                populateDocument();
                populatetblAdditionalInvLine();
            }
        }
    };

    private ActionListener radioAgent = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (acDoc != null) {

                btnContactableSearch.setEnabled(true);
                if (cmbSearchResult.getItemCount() > 0) {
                    cmbSearchResult.removeAllItems();
                }
                txtContactableDetails.setText(null);
            }
        }
    };
    private ActionListener radioCustomer = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                cmbSearchResult.removeAllItems();
            }
            txtContactableDetails.setText(null);
        }
    };

    private void save() {        
        this.acDoc.setAcDocModifiedBy(AuthenticationBo.getLoggedOnUser());
        this.acDoc.setLastModifiedDate(new java.util.Date());
        acDoc.updateAccounts();
        PNR pnr = new PNR();
        Set<AccountingDocument> acDocs = new LinkedHashSet();
        pnr = acDoc.getPnr();
        acDocs.add(acDoc);
        pnr.setAccountingDocuments(acDocs);
        pnr.setTickets(acDoc.getTickets());
        pnrBo.setPnr(pnr);
        pnrBo.savePnr();

        new Thread(new threadLoadAcDoc(this.acDoc.getAcDocId())).start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JToggleButton();
        btnPrint = new javax.swing.JButton();
        btnPOfficeCopy = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnPPreview = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        pnlInvHeader = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtInvRef = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        cmbTerms = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAdditionalInvLine = new org.jdesktop.swingx.JXTable();
        jToolBar3 = new javax.swing.JToolBar();
        btnRemoveLine1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTicket = new org.jdesktop.swingx.JXTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtTSubTotal = new javax.swing.JTextField();
        txtInvAmount = new javax.swing.JTextField();
        lblReceived = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTransBalance = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtOSubTotal = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        cmbSearchResult = new javax.swing.JComboBox();
        cmbSearchResult.addActionListener(cmbSearchResultListener);
        AutoCompleteDecorator.decorate(cmbSearchResult);
        jScrollPane4 = new javax.swing.JScrollPane();
        txtContactableDetails = new javax.swing.JTextArea();
        lblContactibleDetails = new javax.swing.JLabel();
        txtContactableSearch = new javax.swing.JTextField();
        btnContactableSearch = new javax.swing.JButton();
        rdoAgent = new javax.swing.JRadioButton();
        rdoCustomer = new javax.swing.JRadioButton();
        lblSearchClient = new javax.swing.JLabel();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgEditSAcDoc.class);
        btnSave.setFont(resourceMap.getFont("btnSave.font")); // NOI18N
        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnPrint.setFont(resourceMap.getFont("btnPrint.font")); // NOI18N
        btnPrint.setIcon(resourceMap.getIcon("btnPrint.icon")); // NOI18N
        btnPrint.setText(resourceMap.getString("btnPrint.text")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnPOfficeCopy.setFont(resourceMap.getFont("btnPOfficeCopy.font")); // NOI18N
        btnPOfficeCopy.setIcon(resourceMap.getIcon("btnPOfficeCopy.icon")); // NOI18N
        btnPOfficeCopy.setText(resourceMap.getString("btnPOfficeCopy.text")); // NOI18N
        btnPOfficeCopy.setFocusable(false);
        btnPOfficeCopy.setName("btnPOfficeCopy"); // NOI18N
        btnPOfficeCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPOfficeCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPOfficeCopy);

        btnEmail.setFont(resourceMap.getFont("btnEmail.font")); // NOI18N
        btnEmail.setIcon(resourceMap.getIcon("btnEmail.icon")); // NOI18N
        btnEmail.setText(resourceMap.getString("btnEmail.text")); // NOI18N
        btnEmail.setFocusable(false);
        btnEmail.setName("btnEmail"); // NOI18N
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEmail);

        btnPPreview.setFont(resourceMap.getFont("btnPPreview.font")); // NOI18N
        btnPPreview.setText(resourceMap.getString("btnPPreview.text")); // NOI18N
        btnPPreview.setEnabled(false);
        btnPPreview.setFocusable(false);
        btnPPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPPreview.setName("btnPPreview"); // NOI18N
        btnPPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPPreviewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPPreview);

        jPanel6.setBackground(resourceMap.getColor("jPanel6.background")); // NOI18N
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        pnlInvHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlInvHeader.setName("pnlInvHeader"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtInvRef.setEditable(false);
        txtInvRef.setFont(resourceMap.getFont("txtInvRef.font")); // NOI18N
        txtInvRef.setName("txtInvRef"); // NOI18N
        txtInvRef.setPreferredSize(new java.awt.Dimension(6, 18));

        txtUser.setEditable(false);
        txtUser.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        txtUser.setName("txtUser"); // NOI18N
        txtUser.setPreferredSize(new java.awt.Dimension(6, 18));

        datePicker.setDate(new java.util.Date());
        datePicker.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        datePicker.setName("datePicker"); // NOI18N
        datePicker.setPreferredSize(new java.awt.Dimension(132, 18));
        datePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datePickerActionPerformed(evt);
            }
        });

        cmbTerms.setFont(resourceMap.getFont("cmbTerms.font")); // NOI18N
        cmbTerms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CIA", "COD", "CWO", "Net monthly account", "Net 7", "Net 10", "Net 30", "Net 60", "Net 90" }));
        cmbTerms.setName("cmbTerms"); // NOI18N
        cmbTerms.setPreferredSize(new java.awt.Dimension(142, 18));
        cmbTerms.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTermsItemStateChanged(evt);
            }
        });
        cmbTerms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTermsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlInvHeaderLayout = new javax.swing.GroupLayout(pnlInvHeader);
        pnlInvHeader.setLayout(pnlInvHeaderLayout);
        pnlInvHeaderLayout.setHorizontalGroup(
            pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInvHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmbTerms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtInvRef, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlInvHeaderLayout.setVerticalGroup(
            pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInvHeaderLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtInvRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlInvHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbTerms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(pnlInvHeader, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 25));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 25));

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "T.Date", "T.Type", "T.Amount", "T.Ref"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
        tblTransaction.setMinimumSize(new java.awt.Dimension(300, 10));
        tblTransaction.setName("tblTransaction"); // NOI18N
        tblTransaction.setPreferredSize(new java.awt.Dimension(300, 25));
        tblTransaction.setSortable(false);
        tblTransaction.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTransaction);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(jScrollPane1, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel6.add(jLabel1, gridBagConstraints);

        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(250, 25));

        tblAdditionalInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "S.Title", "S.Charge"
            }
        ));
        tblAdditionalInvLine.setToolTipText(resourceMap.getString("tblAdditionalInvLine.toolTipText")); // NOI18N
        tblAdditionalInvLine.setName("tblAdditionalInvLine"); // NOI18N
        tblAdditionalInvLine.setPreferredSize(new java.awt.Dimension(250, 25));
        tblAdditionalInvLine.setSortable(false);
        tblAdditionalInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblAdditionalInvLine);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(jScrollPane3, gridBagConstraints);

        jToolBar3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(1);
        jToolBar3.setRollover(true);
        jToolBar3.setToolTipText(resourceMap.getString("jToolBar3.toolTipText")); // NOI18N
        jToolBar3.setName("jToolBar3"); // NOI18N
        jToolBar3.setPreferredSize(new java.awt.Dimension(35, 25));

        btnRemoveLine1.setFont(resourceMap.getFont("btnRemoveLine1.font")); // NOI18N
        btnRemoveLine1.setIcon(resourceMap.getIcon("btnRemoveLine1.icon")); // NOI18N
        btnRemoveLine1.setToolTipText(resourceMap.getString("btnRemoveLine1.toolTipText")); // NOI18N
        btnRemoveLine1.setFocusable(false);
        btnRemoveLine1.setName("btnRemoveLine1"); // NOI18N
        btnRemoveLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveLine1ActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveLine1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jToolBar3, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 4, 0);
        jPanel6.add(jLabel2, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(602, 100));

        tblTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "P.Name", "TicketNo", "Status", "Date", "GrossFare", "Com/Disc", "ATOL", "NetPayable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTicket.setFont(resourceMap.getFont("tblTicket.font")); // NOI18N
        tblTicket.setName("tblTicket"); // NOI18N
        tblTicket.setSortable(false);
        tblTicket.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTicket);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jScrollPane2, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel6.add(jLabel3, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel9.setBackground(resourceMap.getColor("jPanel9.background")); // NOI18N
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(jLabel8, gridBagConstraints);

        txtTSubTotal.setEditable(false);
        txtTSubTotal.setFont(resourceMap.getFont("txtTSubTotal.font")); // NOI18N
        txtTSubTotal.setText(resourceMap.getString("txtTSubTotal.text")); // NOI18N
        txtTSubTotal.setName("txtTSubTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(txtTSubTotal, gridBagConstraints);

        txtInvAmount.setBackground(resourceMap.getColor("txtInvAmount.background")); // NOI18N
        txtInvAmount.setEditable(false);
        txtInvAmount.setFont(resourceMap.getFont("txtInvAmount.font")); // NOI18N
        txtInvAmount.setForeground(resourceMap.getColor("txtInvAmount.foreground")); // NOI18N
        txtInvAmount.setText(resourceMap.getString("txtInvAmount.text")); // NOI18N
        txtInvAmount.setName("txtInvAmount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtInvAmount, gridBagConstraints);

        lblReceived.setFont(resourceMap.getFont("lblReceived.font")); // NOI18N
        lblReceived.setForeground(resourceMap.getColor("lblReceived.foreground")); // NOI18N
        lblReceived.setText(resourceMap.getString("lblReceived.text")); // NOI18N
        lblReceived.setName("lblReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(lblReceived, gridBagConstraints);

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel12, gridBagConstraints);

        txtTransBalance.setBackground(resourceMap.getColor("txtTransBalance.background")); // NOI18N
        txtTransBalance.setEditable(false);
        txtTransBalance.setFont(resourceMap.getFont("txtTransBalance.font")); // NOI18N
        txtTransBalance.setForeground(resourceMap.getColor("txtTransBalance.foreground")); // NOI18N
        txtTransBalance.setText(resourceMap.getString("txtTransBalance.text")); // NOI18N
        txtTransBalance.setName("txtTransBalance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtTransBalance, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 2);
        jPanel9.add(jSeparator3, gridBagConstraints);

        jSeparator4.setName("jSeparator4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        jPanel9.add(jSeparator4, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel13.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel13, gridBagConstraints);

        txtBalance.setBackground(resourceMap.getColor("txtBalance.background")); // NOI18N
        txtBalance.setEditable(false);
        txtBalance.setFont(resourceMap.getFont("txtBalance.font")); // NOI18N
        txtBalance.setText(resourceMap.getString("txtBalance.text")); // NOI18N
        txtBalance.setName("txtBalance"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 6, 2);
        jPanel9.add(txtBalance, gridBagConstraints);

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setForeground(resourceMap.getColor("jLabel14.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel14, gridBagConstraints);

        txtOSubTotal.setBackground(resourceMap.getColor("txtOSubTotal.background")); // NOI18N
        txtOSubTotal.setFont(resourceMap.getFont("txtOSubTotal.font")); // NOI18N
        txtOSubTotal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtOSubTotal.setText(resourceMap.getString("txtOSubTotal.text")); // NOI18N
        txtOSubTotal.setName("txtOSubTotal"); // NOI18N
        txtOSubTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOSubTotalFocusGained(evt);
            }
        });
        txtOSubTotal.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtOSubTotalInputMethodTextChanged(evt);
            }
        });
        txtOSubTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtOSubTotalKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOSubTotalKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtOSubTotal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 0);
        jPanel1.add(jPanel9, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel7.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel7.border.titleFont"), resourceMap.getColor("jPanel7.border.titleColor"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridBagLayout());

        cmbSearchResult.setName("cmbSearchResult"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(cmbSearchResult, gridBagConstraints);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        txtContactableDetails.setColumns(16);
        txtContactableDetails.setEditable(false);
        txtContactableDetails.setFont(resourceMap.getFont("txtContactableDetails.font")); // NOI18N
        txtContactableDetails.setLineWrap(true);
        txtContactableDetails.setRows(5);
        txtContactableDetails.setMinimumSize(new java.awt.Dimension(180, 100));
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
        txtContactableDetails.setPreferredSize(new java.awt.Dimension(180, 80));
        jScrollPane4.setViewportView(txtContactableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jScrollPane4, gridBagConstraints);

        lblContactibleDetails.setFont(resourceMap.getFont("lblContactibleDetails.font")); // NOI18N
        lblContactibleDetails.setText(resourceMap.getString("lblContactibleDetails.text")); // NOI18N
        lblContactibleDetails.setName("lblContactibleDetails"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(lblContactibleDetails, gridBagConstraints);

        txtContactableSearch.setColumns(20);
        txtContactableSearch.setToolTipText(resourceMap.getString("txtContactableSearch.toolTipText")); // NOI18N
        txtContactableSearch.setName("txtContactableSearch"); // NOI18N
        txtContactableSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContactableSearchActionPerformed(evt);
            }
        });
        txtContactableSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContactableSearchFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(txtContactableSearch, gridBagConstraints);

        btnContactableSearch.setFont(resourceMap.getFont("btnContactableSearch.font")); // NOI18N
        btnContactableSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/etsbackoffice/gui/resources/icons/search24.png"))); // NOI18N
        btnContactableSearch.setText(resourceMap.getString("btnContactableSearch.text")); // NOI18N
        btnContactableSearch.setName("btnContactableSearch"); // NOI18N
        btnContactableSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContactableSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(btnContactableSearch, gridBagConstraints);

        rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
        rdoAgent.setName("rdoAgent"); // NOI18N
        rdoAgent.addActionListener(radioAgent);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPanel7.add(rdoAgent, gridBagConstraints);

        rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
        rdoCustomer.setName("rdoCustomer"); // NOI18N
        rdoCustomer.addActionListener(radioCustomer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(rdoCustomer, gridBagConstraints);

        lblSearchClient.setText(resourceMap.getString("lblSearchClient.text")); // NOI18N
        lblSearchClient.setName("lblSearchClient"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(lblSearchClient, gridBagConstraints);

        busyLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        busyLabel.setFont(resourceMap.getFont("busyLabel.font")); // NOI18N
        busyLabel.setName("busyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(busyLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(progressBar, gridBagConstraints);

        statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel4.add(statusMessageLabel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 921, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 536, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1, 1, 1)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        new Thread(new threadSaveAcDoc()).start();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed

        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        // this.acDoc.setAccountingDocumentLines(new LinkedHashSet(this.ticketLines));
        // this.acDoc.getAccountingDocumentLines().addAll(new LinkedHashSet(this.additionalLines));
        acDocBo.setAcDoc(this.acDoc);
        acDocBo.setOfficeCopy(false);

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.printAcDoc(invObject);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnPOfficeCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPOfficeCopyActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        //this.acDoc.setAccountingDocumentLines(new LinkedHashSet(this.ticketLines));
        //  this.acDoc.getAccountingDocumentLines().addAll(new LinkedHashSet(this.additionalLines));
        acDocBo.setAcDoc(this.acDoc);
        acDocBo.setOfficeCopy(true);
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        rptAcDoc.printAcDoc(invObject);
}//GEN-LAST:event_btnPOfficeCopyActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        acDocBo.setmAgent(AuthenticationBo.getmAgent());
        // this.acDoc.setAccountingDocumentLines(new LinkedHashSet(this.ticketLines));
        // this.acDoc.getAccountingDocumentLines().addAll(new LinkedHashSet(this.additionalLines));
        acDocBo.setAcDoc(this.acDoc);
        acDocBo.setOfficeCopy(true);
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(acDocBo);
        String emailAddress="";
        if (this.acDoc.getPnr().getAgent() != null) {
            emailAddress = this.acDoc.getPnr().getAgent().getEmail();
        } else if (this.acDoc.getPnr().getCustomer() != null) {
            emailAddress = this.acDoc.getPnr().getCustomer().getEmail();
        }
        if(!emailAddress.isEmpty()){
            rptAcDoc.emailAcDoc(emailAddress,invObject);
        }else{
            JOptionPane.showMessageDialog(null, "No email address found !!!", "Email Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnEmailActionPerformed

    private void btnPPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPPreviewActionPerformed

}//GEN-LAST:event_btnPPreviewActionPerformed

    private void datePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datePickerActionPerformed
        this.acDoc.setIssueDate(datePicker.getDate());
        setSaveNeeded(true);
}//GEN-LAST:event_datePickerActionPerformed

    private void cmbTermsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTermsItemStateChanged

}//GEN-LAST:event_cmbTermsItemStateChanged

    private void cmbTermsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTermsActionPerformed
        this.acDoc.setTerms((String) cmbTerms.getSelectedItem());
        setSaveNeeded(true);
}//GEN-LAST:event_cmbTermsActionPerformed

    private void btnRemoveLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLine1ActionPerformed
        int row = tblAdditionalInvLine.getSelectedRow();
        if (row != -1) {
            if (row < this.additionalLines.size() && this.additionalLines.get(row).getAccountingDocument() != null) {
                acDocBo.removeLine(this.additionalLines.remove(row));
                //this.acDoc.getAccountingDocumentLines().remove(this.additionalLines.get(row));
                new Thread(new threadLoadAcDoc(acDoc.getAcDocId())).start();                
            }

        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "Modify Invoice", JOptionPane.WARNING_MESSAGE);
        }
}//GEN-LAST:event_btnRemoveLine1ActionPerformed

    private void txtOSubTotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOSubTotalFocusGained
        txtOSubTotal.selectAll();
}//GEN-LAST:event_txtOSubTotalFocusGained

    private void txtOSubTotalInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtOSubTotalInputMethodTextChanged
        // TODO add your handling code here:
}//GEN-LAST:event_txtOSubTotalInputMethodTextChanged

    private void txtOSubTotalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOSubTotalKeyPressed

}//GEN-LAST:event_txtOSubTotalKeyPressed

    private void txtOSubTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOSubTotalKeyReleased

}//GEN-LAST:event_txtOSubTotalKeyReleased

    private void txtContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            new Thread(new threadSearchContactable()).start();
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
}//GEN-LAST:event_txtContactableSearchActionPerformed

    private void txtContactableSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactableSearchFocusGained
        txtContactableSearch.selectAll();
}//GEN-LAST:event_txtContactableSearchFocusGained

    private void btnContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            Thread t = new Thread(new threadSearchContactable());
            t.start();
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
}//GEN-LAST:event_btnContactableSearchActionPerformed

    /**
    * @param args the command line arguments
    */
  /*  public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgEditSAcDoc dialog = new DlgEditSAcDoc(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContactableSearch;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnPOfficeCopy;
    private javax.swing.JButton btnPPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRemoveLine1;
    private javax.swing.JToggleButton btnSave;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JComboBox cmbSearchResult;
    private javax.swing.JComboBox cmbTerms;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblContactibleDetails;
    private javax.swing.JLabel lblReceived;
    private javax.swing.JLabel lblSearchClient;
    private javax.swing.JPanel pnlInvHeader;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblAdditionalInvLine;
    private org.jdesktop.swingx.JXTable tblTicket;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtContactableSearch;
    private javax.swing.JTextField txtInvAmount;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtOSubTotal;
    private javax.swing.JTextField txtTSubTotal;
    private javax.swing.JTextField txtTransBalance;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadLoadAllServices implements Runnable {

        public threadLoadAllServices() {
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading Services...");

            additionalServices = oServiceBo.loadAdditionalServices();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSearchContactable implements Runnable {

        public threadSearchContactable() {
        }

        public void run() {
            progressBar = new JProgressBar();
            busyLabel.setBusy(true);
            btnContactableSearch.setEnabled(false);
            progressBar.setIndeterminate(true); //Set value
            progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();

            statusMessageLabel.setText("Searching client...");
            searchContactable();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            progressBar.repaint(); //Refresh graphics
            btnContactableSearch.setEnabled(true);
            busyLabel.setBusy(false);
        }
    }

    private class threadSaveAcDoc implements Runnable {

        AccountingDocument acDoc;

        public threadSaveAcDoc() {

        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving invoice...");

            save();
            //acDocBo.setAcDoc(acDoc);
            //acDocBo.saveAcDoc();


        }
    }

    private class threadLoadAcDoc implements Runnable {

        private long acDocId;

        public threadLoadAcDoc(long acDocId) {
            this.acDocId = acDocId;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            acDocBo.findAcDocById(acDocId);
            acDoc = acDocBo.getAccountingDocument();

            ticketLines = new ArrayList(acDoc.getTicketLine());
            additionalLines = new ArrayList(acDoc.getAdditionalServiceLine());

            populateDocumentHeader();
            populateTblTicket();
            populateDocument();
            populatetblAdditionalInvLine();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
}
