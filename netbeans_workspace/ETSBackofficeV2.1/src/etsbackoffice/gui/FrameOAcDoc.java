/*
 * FrameOtherInvMain.java
 *
 * Created on 23-Mar-2011, 18:31:48
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.domain.*;
import etsbackoffice.report.BackofficeReporting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameOAcDoc extends javax.swing.JFrame {

    /** Creates new form FrameOtherInvMain */
    private boolean submitNeeded;
    private boolean isEditable;
    DefaultComboBoxModel cmbSTitleModel;
    DefaultComboBoxModel cmbAdditionalSTitleModel;
    JComboBox cmbSTitle;
    JComboBox cmbAdditionalSTitle;
    private DefaultTableModel tblInvLineModel, tblAdditionalInvLineModel;
    private AcTransactionBo acTransactionBo = (AcTransactionBo) ETSBackofficeApp.getApplication().ctx.getBean("acTransactionBo");
    private OServiceBo oServiceBo = (OServiceBo) ETSBackofficeApp.getApplication().ctx.getBean("oServiceBo");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private OAccountingDocBo oAcDocBo = (OAccountingDocBo) ETSBackofficeApp.getApplication().ctx.getBean("oAcDocBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private CustomerBo customerBo = (CustomerBo) ETSBackofficeApp.getApplication().ctx.getBean("customerBo");
    private DateFormat df = new DateFormat();
    private List<OtherService> services = new ArrayList();
    private List<OtherService> oServices = new ArrayList();
    private List<OtherService> additionalServices = new ArrayList();
    private List<Customer> searchedCustomer = new ArrayList();
    private List<Agent> searchedAgent = new ArrayList();
    private OAccountingDocument oAcDoc;
    private List<OAccountingDocumentLine> lines = new ArrayList<OAccountingDocumentLine>();
    private List<OAccountingDocumentLine> additionalLines = new ArrayList<OAccountingDocumentLine>();

    public FrameOAcDoc(java.awt.Frame parent) {
        initComponents();
        initTblInvLine();
        initTblAdditionalInvLine();        
    }

    public void loadObjects(final OAccountingDocument oAcDoc) {
        this.oAcDoc = oAcDoc;
        isEditable = false;

        lines = new ArrayList(this.oAcDoc.getNormalService());
        additionalLines = new ArrayList(this.oAcDoc.getAdditionalService());

        if (!this.oAcDoc.getRelatedDocuments().isEmpty()) {
            for (OAccountingDocument o : this.oAcDoc.getRelatedDocuments()) {
                this.getLines().addAll(o.getoAccountingDocumentLines());
            }
        }
        if (oAcDoc.getoAcDocId() == 0) {
            PopulateCmbServiceTitle p = new PopulateCmbServiceTitle();
            p.execute();
        }
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                clearControlls();
                initControlls();
                populateDocumentHeader();
                populatetblInvLine();
                populatetblAdditionalInvLine();
                populateDocument();
                populateTblTransaction(oAcDoc.getAcTransactions());
            }
        });
    }

    private void initTblInvLine() {
        tblInvLineModel = (DefaultTableModel) tblInvLine.getModel();
        TableColumn sTitle, sChg, disc, vat, quantity;
        sTitle = tblInvLine.getColumnModel().getColumn(1);
        sChg = tblInvLine.getColumnModel().getColumn(3);
        disc = tblInvLine.getColumnModel().getColumn(4);
        quantity = tblInvLine.getColumnModel().getColumn(5);
        vat = tblInvLine.getColumnModel().getColumn(7);        

        JTextField jtf = new JTextField();
        jtf.setDocument(new CheckInput(CheckInput.FLOAT));
        sChg.setCellEditor(new DefaultCellEditor(jtf));
        disc.setCellEditor(new DefaultCellEditor(jtf));
        vat.setCellEditor(new DefaultCellEditor(jtf));
        quantity.setCellEditor(new DefaultCellEditor(jtf));

        cmbSTitle = new JComboBox();
        sTitle.setCellEditor(new DefaultCellEditor(cmbSTitle));
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

    private void searchContactable() {
        String searchElement = txtContactableSearch.getText().trim();

        if (!searchElement.isEmpty()) {
            if (rdoAgent.isSelected()) {
                searchedAgent = new ArrayList();
                searchedAgent = agentBo.searchAgents(searchElement);

                if (searchedAgent.size() == 1) {
                    this.oAcDoc.setAgent(searchedAgent.get(0));
                    this.oAcDoc.setCustomer(null);
                    populateTxtAgentDetails(this.oAcDoc.getAgent());
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
                        if (this.oAcDoc != null) {
                            this.oAcDoc.setAgent(searchedAgent.get(0));
                            this.oAcDoc.setCustomer(null);
                        }

                        populateTxtAgentDetails(this.oAcDoc.getAgent());
                        busyLabel.setText("");
                    }
                }
            } else if (rdoCustomer.isSelected()) {
                searchedCustomer = new ArrayList();
                searchedCustomer = customerBo.searchCustomerForeNameLike(searchElement);

                if (searchedCustomer.size() == 1) {
                    this.oAcDoc.setCustomer(searchedCustomer.get(0));
                    this.oAcDoc.setAgent(null);

                    populateTxtCustomerDetails(this.oAcDoc.getCustomer());
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

    private void populatetblInvLine() {
        tblInvLineModel = (DefaultTableModel) tblInvLine.getModel();
        tblInvLineModel.getDataVector().removeAllElements();
        tblInvLine.repaint();
        Iterator it = this.getLines().iterator();
        int row = 0;
        while (it.hasNext()) {
            OAccountingDocumentLine line = (OAccountingDocumentLine) it.next();

            tblInvLineModel.insertRow(row, new Object[]{row, line.getServiceTitle(),line.getRemark(),
                        line.getServiceCharge(), line.getDiscount(), line.getUnit(), line.getNetPayable(), line.getVat()});
            row++;

        }
        tblInvLineModel.addRow(new Object[]{"", "", "", "", "", "", "",""});

        tblInvLineModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getLastRow();
                    int column = e.getColumn();

                    if(column == 2){
                     String remark = tblInvLine.getValueAt(row, column).toString();                     
                      getLines().get(row).setRemark(remark);                     
                    }
                    
                    if (column == 3) {
                        String sCharge = tblInvLine.getValueAt(row, column).toString();
                        if (!sCharge.isEmpty() && getLines().size() > row) {
                            getLines().get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }
                    if (column == 4) {
                        String disc = tblInvLine.getValueAt(row, column).toString();
                        if (!disc.isEmpty() && getLines().size() > row) {
                            getLines().get(row).setDiscount(new BigDecimal(disc));
                        }
                    }
                    if (column == 5) {
                        String unit = tblInvLine.getValueAt(row, column).toString();
                        if (!unit.isEmpty() && getLines().size() > row) {
                            getLines().get(row).setUnit(Integer.valueOf(unit));
                        }
                    }
                    populatetblInvLine();
                    populateDocument();                    
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
            OAccountingDocumentLine line = (OAccountingDocumentLine) it.next();


            tblAdditionalInvLineModel.insertRow(row, new Object[]{line.getServiceTitle(),
                        line.getServiceCharge()});
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
                            additionalLines.get(row).setServiceCharge(new BigDecimal(sCharge));
                        }
                    }
                    /*if (column == 2) {
                    String disc = tblAdditionalInvLine.getValueAt(row, column).toString();
                    if (!disc.isEmpty() && additionalLines.size() > row) {                         
                    getLines().get(row).setDiscount(new BigDecimal(disc));                            
                    }
                    }*/
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
        txtReceived.setText(this.oAcDoc.getTotalTransactionAmount().toString());
    }

    private void populateDocument() {
        BigDecimal subTotal = new BigDecimal("0.00");
        BigDecimal vat = new BigDecimal("0.00");
        String oChgRmk = "";

        for (OAccountingDocumentLine l : this.getLines()) {
            subTotal = subTotal.add(l.getNetPayable());
            vat = vat.add(l.getVat());
        }
        for (OAccountingDocumentLine l : this.additionalLines) {
            subTotal = subTotal.add(l.getNetPayable());
            vat = vat.add(l.getVat());
        }

        txtSubTotal.setText(subTotal.toString());
        txtVat.setText(vat.toString());
        txtInvAmount.setText(subTotal.add(vat).toString());
        txtBalance.setText(subTotal.add(vat).subtract(this.oAcDoc.getTotalTransactionAmount()).toString());
    }

    private void populateDocumentHeader() {
        if (this.oAcDoc.getCustomer() != null) {
            populateTxtCustomerDetails(this.oAcDoc.getCustomer());
        } else if (this.oAcDoc.getAgent() != null) {
            populateTxtAgentDetails(this.oAcDoc.getAgent());
        } else {
            txtContactableDetails.setText("");
        }

        if (this.oAcDoc.getAcDocRef() != null) {
            datePicker.setDate(this.oAcDoc.getIssueDate());
            txtInvRef.setText(this.oAcDoc.getAcDocRefString());
            txtUser.setText(this.oAcDoc.getAcDocIssuedBy().getSurName() + "/" + this.oAcDoc.getAcDocIssuedBy().getForeName());
        }
        cmbTerms.setSelectedItem(this.oAcDoc.getTerms());
        if (this.oAcDoc.getRemark() != null) {
            txtInvRemark.setText(this.oAcDoc.getRemark());
        }
    }
    private ActionListener cmbSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                OAccountingDocumentLine line = new OAccountingDocumentLine();
                line.setServiceTitle(oServices.get(cmbSTitleIndex).getServiceTitle());
                //line.setRemark("");//empty string just to get rid of null string in reporting
                line.setServiceCharge(oServices.get(cmbSTitleIndex).getServiceCharge());
                line.setServiceCost(oServices.get(cmbSTitleIndex).getServiceCost());
                line.setVatRate(oServices.get(cmbSTitleIndex).getVatRate());
                line.setUnit(1);
                line.setOtherService(oServices.get(cmbSTitleIndex));
                
                lines.add(line);
                populatetblInvLine();
                populateDocument();
            }
        }
    };
    private ActionListener cmbAdditionalSTitleListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int row = tblAdditionalInvLine.getSelectedRow();
            int cmbSTitleIndex = cb.getSelectedIndex();

            if (row != -1) {
                OAccountingDocumentLine line = new OAccountingDocumentLine();
                line.setServiceTitle(additionalServices.get(cmbSTitleIndex).getServiceTitle());
                line.setServiceCharge(additionalServices.get(cmbSTitleIndex).getFinalServiceCharge(new BigDecimal(txtInvAmount.getText())));
                line.setServiceCost(additionalServices.get(cmbSTitleIndex).getFinalServiceCost(new BigDecimal(txtInvAmount.getText())));
                line.setVatRate(additionalServices.get(cmbSTitleIndex).getVatRate());
                line.setUnit(1);
                line.setOtherService(additionalServices.get(cmbSTitleIndex));

                additionalLines.add(line);
                populatetblAdditionalInvLine();
                populateDocument();
            }
        }
    };
    private ActionListener cmbSearchResultListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (cmbSearchResult.getItemCount() > 0) {
                String[] data = cmbSearchResult.getSelectedItem().toString().split("-");
                long id = Long.parseLong(data[2]);

                if (rdoAgent.isSelected()) {
                    for (int i = 0; i < searchedAgent.size(); i++) {
                        if (searchedAgent.get(i).getContactableId() == id) {
                            //agent = searchedAgent.get(i);
                            oAcDoc.setAgent(searchedAgent.get(i));
                            oAcDoc.setCustomer(null);
                            populateTxtAgentDetails(oAcDoc.getAgent());

                        }
                    }
                } else if (rdoCustomer.isSelected()) {
                    for (int i = 0; i < searchedCustomer.size(); i++) {
                        if (searchedCustomer.get(i).getContactableId() == id) {
                            //customer = searchedCustomer.get(i);
                            oAcDoc.setCustomer(searchedCustomer.get(i));
                            oAcDoc.setAgent(null);
                            populateTxtCustomerDetails(oAcDoc.getCustomer());
                        }
                    }
                }
            }
        }
    };
    private ActionListener radioAgent = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (oAcDoc != null) {

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

    private void createNewInvoice() {
        if (this.oAcDoc != null) {
            oAcDoc.setIssueDate(datePicker.getDate());
            oAcDoc.setAcDoctype(1);
            if (cmbTerms.getSelectedIndex() > 0) {
                oAcDoc.setTerms((String) cmbTerms.getSelectedItem());
            }
            oAcDoc.setRemark(txtInvRemark.getText().trim());

            oAcDoc.setAcDocIssuedBy(AuthenticationBo.getLoggedOnUser());
            oAcDoc.setActive(true);

            oAcDoc.setAcDocRef(oAcDocBo.generateAcDocRef());
            if (oAcDoc.getTerms() == null) {
                JOptionPane.showMessageDialog(null, "Select Terms", "New Invoice", JOptionPane.WARNING_MESSAGE);
            } else if (oAcDoc.getCustomer() == null && oAcDoc.getAgent() == null) {
                JOptionPane.showMessageDialog(null, "Select Invoice Recipient", "New Invoice", JOptionPane.WARNING_MESSAGE);
            } else {
                initAcDocInLine(oAcDoc, this.lines);
                initAcDocInLine(oAcDoc, this.additionalLines);
                oAcDoc.setoAccountingDocumentLines(new LinkedHashSet(this.getLines()));
                oAcDoc.getoAccountingDocumentLines().addAll(new LinkedHashSet(this.additionalLines));
                oAcDoc.addAcStatement(accountsBo.newAccountsTransactionFromSOAcDoc(oAcDoc));
                new Thread(new threadSaveAcDoc(oAcDoc)).start();
            }
        }
    }

    private void issueCNote() {

        OAccountingDocument cNote = new OAccountingDocument();
        List<OAccountingDocumentLine> ls = new ArrayList();
        for (OAccountingDocumentLine l : this.getLines()) {
            if (l.getoAccountingDocument() == null && l.getNetPayable().compareTo(new BigDecimal("0.00")) < 0) {
                ls.add(l);
            }
        }

        for (OAccountingDocumentLine l : this.additionalLines) {
            if (l.getoAccountingDocument() == null && l.getNetPayable().compareTo(new BigDecimal("0.00")) < 0) {
                ls.add(l);
            }
        }

        cNote.setIssueDate(null);
        cNote.setAcDocRef(oAcDoc.getAcDocRef());
        cNote.setIssueDate(new java.util.Date());
        cNote.setAcDocIssuedBy(AuthenticationBo.getLoggedOnUser());
        cNote.setActive(true);
        cNote.setAgent(oAcDoc.getAgent());
        cNote.setCustomer(oAcDoc.getCustomer());
        cNote.setAcDoctype(2);
        initAcDocInLine(cNote, ls);
        cNote.setoAccountingDocumentLines(new LinkedHashSet(ls));
        cNote.setoAccountingDocument(oAcDoc);
        cNote.addAcStatement(accountsBo.newAccountsTransactionFromSOAcDoc(cNote));
        new Thread(new threadSaveAcDoc(cNote)).start();
    }

    private void returnItem(OAccountingDocumentLine l) {
        OAccountingDocumentLine returnedLine = new OAccountingDocumentLine();
        returnedLine.setServiceTitle(l.getServiceTitle());
        returnedLine.setServiceCharge(l.getServiceCharge().negate());
        returnedLine.setServiceCost(l.getServiceCost().negate());
        returnedLine.setUnit(l.getUnit());
        returnedLine.setVatRate(l.getVatRate());
        returnedLine.setOtherService(l.getOtherService());
        getLines().add(getLines().size(), returnedLine);
    }

    private void initAcDocInLine(OAccountingDocument o, List<OAccountingDocumentLine> ls) {
        for (OAccountingDocumentLine l : ls) {
            if (l.getoAcDocLineId() == 0) {
                l.setoAccountingDocument(o);
            }
        }
    }

    private void clearControlls() {
        txtInvRemark.setText("");
        txtInvRef.setText("");
        txtUser.setText("");
        cmbTerms.setSelectedIndex(-1);
        txtContactableDetails.setText("");
        txtVat.setText("");
        txtContactableSearch.setText("");
        txtSubTotal.setText("");
        txtInvAmount.setText("");
        txtReceived.setText("");
        txtBalance.setText("");
    }

    private void initControlls() {
        if (this.oAcDoc.isActive() == false) {
            lblVoid.setVisible(true);
        } else {
            lblVoid.setVisible(false);
        }

        if (this.oAcDoc.getIssueDate() != null && isEditable == false) {

            btnCreateInvoice.setEnabled(false);
            rdoAgent.setEnabled(false);
            rdoCustomer.setEnabled(false);
            lblSearchClient.setEnabled(false);

            txtContactableSearch.setEnabled(false);
            btnContactableSearch.setEnabled(false);
            tblInvLine.setEditable(false);
            tblAdditionalInvLine.setEditable(false);
            //txtVat.setEditable(false);
            datePicker.setEnabled(false);
            cmbTerms.setEnabled(false);

            btnIssueCNote.setEnabled(false);
            btnReturnItem.setEnabled(true);
            btnReturnAdditionalItem.setEnabled(true);
            txtInvRemark.setEditable(false);
        } else {
            //populateCmbSTitle();
            lblVoid.setVisible(false);
            rdoAgent.setEnabled(true);
            rdoCustomer.setEnabled(true);
            lblSearchClient.setEnabled(true);
            txtContactableSearch.setEnabled(true);
            btnContactableSearch.setEnabled(true);
            tblInvLine.setEditable(true);
            tblAdditionalInvLine.setEditable(true);
            //txtVat.setEditable(true);
            datePicker.setEnabled(true);
            cmbTerms.setEnabled(true);

            btnReturnItem.setEnabled(false);
            btnReturnAdditionalItem.setEnabled(false);


            datePicker.setDate(new java.util.Date());
            cmbTerms.setSelectedIndex(0);
            txtInvRemark.setEditable(true);
        }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnCreateInvoice = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnPOfficeCopy = new javax.swing.JButton();
        btnEmail = new javax.swing.JButton();
        btnIssueCNote = new javax.swing.JButton();
        btnTransWizard = new javax.swing.JButton();
        btnPPreview = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusMessageLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtInvAmount = new javax.swing.JTextField();
        lblReceived = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtReceived = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtVat = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
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
        pnlMain = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        pnlInvHeader = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtInvRef = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        cmbTerms = new javax.swing.JComboBox();
        jToolBar2 = new javax.swing.JToolBar();
        btnRemoveLine = new javax.swing.JButton();
        btnReturnItem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jToolBar3 = new javax.swing.JToolBar();
        btnRemoveLine1 = new javax.swing.JButton();
        btnReturnAdditionalItem = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAdditionalInvLine = new org.jdesktop.swingx.JXTable();
        jLabel2 = new javax.swing.JLabel();
        lblVoid = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtInvRemark = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInvLine = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransaction = new org.jdesktop.swingx.JXTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameOAcDoc.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(950, 580));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnCreateInvoice.setBackground(resourceMap.getColor("btnCreateInvoice.background")); // NOI18N
        btnCreateInvoice.setFont(resourceMap.getFont("btnCreateInvoice.font")); // NOI18N
        btnCreateInvoice.setIcon(resourceMap.getIcon("btnCreateInvoice.icon")); // NOI18N
        btnCreateInvoice.setText(resourceMap.getString("btnCreateInvoice.text")); // NOI18N
        btnCreateInvoice.setToolTipText(resourceMap.getString("btnCreateInvoice.toolTipText")); // NOI18N
        btnCreateInvoice.setFocusable(false);
        btnCreateInvoice.setName("btnCreateInvoice"); // NOI18N
        btnCreateInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateInvoiceActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCreateInvoice);

        btnPrint.setFont(resourceMap.getFont("btnCreateInvoice.font")); // NOI18N
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

        btnEmail.setFont(resourceMap.getFont("btnCreateInvoice.font")); // NOI18N
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

        btnIssueCNote.setFont(resourceMap.getFont("btnIssueCNote.font")); // NOI18N
        btnIssueCNote.setIcon(resourceMap.getIcon("btnIssueCNote.icon")); // NOI18N
        btnIssueCNote.setText(resourceMap.getString("btnIssueCNote.text")); // NOI18N
        btnIssueCNote.setEnabled(false);
        btnIssueCNote.setFocusable(false);
        btnIssueCNote.setName("btnIssueCNote"); // NOI18N
        btnIssueCNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIssueCNoteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnIssueCNote);

        btnTransWizard.setFont(resourceMap.getFont("btnTransWizard.font")); // NOI18N
        btnTransWizard.setIcon(resourceMap.getIcon("btnTransWizard.icon")); // NOI18N
        btnTransWizard.setText(resourceMap.getString("btnTransWizard.text")); // NOI18N
        btnTransWizard.setFocusable(false);
        btnTransWizard.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnTransWizard.setName("btnTransWizard"); // NOI18N
        btnTransWizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransWizardActionPerformed(evt);
            }
        });
        jToolBar1.add(btnTransWizard);

        btnPPreview.setFont(resourceMap.getFont("btnPPreview.font")); // NOI18N
        btnPPreview.setText(resourceMap.getString("btnPPreview.text")); // NOI18N
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

        btnEdit.setFont(resourceMap.getFont("btnEdit.font")); // NOI18N
        btnEdit.setIcon(resourceMap.getIcon("btnEdit.icon")); // NOI18N
        btnEdit.setText(resourceMap.getString("btnEdit.text")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnUpdate.setFont(resourceMap.getFont("btnUpdate.font")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setText(resourceMap.getString("btnUpdate.text")); // NOI18N
        btnUpdate.setEnabled(false);
        btnUpdate.setFocusable(false);
        btnUpdate.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jToolBar1.add(btnUpdate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jToolBar1, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel4, gridBagConstraints);

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(jLabel8, gridBagConstraints);

        txtSubTotal.setEditable(false);
        txtSubTotal.setFont(resourceMap.getFont("txtSubTotal.font")); // NOI18N
        txtSubTotal.setText(resourceMap.getString("txtSubTotal.text")); // NOI18N
        txtSubTotal.setName("txtSubTotal"); // NOI18N
        txtSubTotal.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        jPanel9.add(txtSubTotal, gridBagConstraints);

        txtInvAmount.setBackground(resourceMap.getColor("txtInvAmount.background")); // NOI18N
        txtInvAmount.setEditable(false);
        txtInvAmount.setFont(resourceMap.getFont("txtInvAmount.font")); // NOI18N
        txtInvAmount.setForeground(resourceMap.getColor("txtInvAmount.foreground")); // NOI18N
        txtInvAmount.setText(resourceMap.getString("txtInvAmount.text")); // NOI18N
        txtInvAmount.setName("txtInvAmount"); // NOI18N
        txtInvAmount.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtInvAmount, gridBagConstraints);

        lblReceived.setFont(resourceMap.getFont("lblReceived.font")); // NOI18N
        lblReceived.setForeground(resourceMap.getColor("lblReceived.foreground")); // NOI18N
        lblReceived.setText(resourceMap.getString("lblReceived.text")); // NOI18N
        lblReceived.setName("lblReceived"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(lblReceived, gridBagConstraints);

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel12, gridBagConstraints);

        txtReceived.setBackground(resourceMap.getColor("txtReceived.background")); // NOI18N
        txtReceived.setEditable(false);
        txtReceived.setFont(resourceMap.getFont("txtReceived.font")); // NOI18N
        txtReceived.setForeground(resourceMap.getColor("txtReceived.foreground")); // NOI18N
        txtReceived.setText(resourceMap.getString("txtReceived.text")); // NOI18N
        txtReceived.setName("txtReceived"); // NOI18N
        txtReceived.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtReceived, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        jPanel9.add(jSeparator3, gridBagConstraints);

        jSeparator4.setName("jSeparator4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel9.add(jSeparator4, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel13.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel13, gridBagConstraints);

        txtBalance.setBackground(resourceMap.getColor("txtBalance.background")); // NOI18N
        txtBalance.setEditable(false);
        txtBalance.setFont(resourceMap.getFont("txtBalance.font")); // NOI18N
        txtBalance.setText(resourceMap.getString("txtBalance.text")); // NOI18N
        txtBalance.setName("txtBalance"); // NOI18N
        txtBalance.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(jLabel14, gridBagConstraints);

        txtVat.setBackground(resourceMap.getColor("txtVat.background")); // NOI18N
        txtVat.setFont(resourceMap.getFont("txtVat.font")); // NOI18N
        txtVat.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtVat.setText(resourceMap.getString("txtVat.text")); // NOI18N
        txtVat.setName("txtVat"); // NOI18N
        txtVat.setPreferredSize(new java.awt.Dimension(0, 16));
        txtVat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVatFocusGained(evt);
            }
        });
        txtVat.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtVatInputMethodTextChanged(evt);
            }
        });
        txtVat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtVatKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVatKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(txtVat, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel9.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 2, 0);
        jPanel1.add(jPanel9, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel7.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel7.border.titleFont"), resourceMap.getColor("jPanel7.border.titleColor"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridBagLayout());

        cmbSearchResult.setEnabled(false);
        cmbSearchResult.setName("cmbSearchResult"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
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
        txtContactableDetails.setMinimumSize(new java.awt.Dimension(104, 30));
        txtContactableDetails.setName("txtContactableDetails"); // NOI18N
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

        lblContactibleDetails.setText(resourceMap.getString("lblContactibleDetails.text")); // NOI18N
        lblContactibleDetails.setName("lblContactibleDetails"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(lblContactibleDetails, gridBagConstraints);

        txtContactableSearch.setBackground(resourceMap.getColor("txtContactableSearch.background")); // NOI18N
        txtContactableSearch.setColumns(20);
        txtContactableSearch.setText(resourceMap.getString("txtContactableSearch.text")); // NOI18N
        txtContactableSearch.setToolTipText(resourceMap.getString("txtContactableSearch.toolTipText")); // NOI18N
        txtContactableSearch.setEnabled(false);
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
        btnContactableSearch.setEnabled(false);
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

        buttonGroup1.add(rdoAgent);
        rdoAgent.setText(resourceMap.getString("rdoAgent.text")); // NOI18N
        rdoAgent.setEnabled(false);
        rdoAgent.setName("rdoAgent"); // NOI18N
        rdoAgent.addActionListener(radioAgent);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPanel7.add(rdoAgent, gridBagConstraints);

        buttonGroup1.add(rdoCustomer);
        rdoCustomer.setText(resourceMap.getString("rdoCustomer.text")); // NOI18N
        rdoCustomer.setEnabled(false);
        rdoCustomer.setName("rdoCustomer"); // NOI18N
        rdoCustomer.addActionListener(radioCustomer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(rdoCustomer, gridBagConstraints);

        lblSearchClient.setText(resourceMap.getString("lblSearchClient.text")); // NOI18N
        lblSearchClient.setEnabled(false);
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel1, gridBagConstraints);

        pnlMain.setBackground(resourceMap.getColor("pnlMain.background")); // NOI18N
        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMain.setName("pnlMain"); // NOI18N
        pnlMain.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(jLabel3, gridBagConstraints);

        pnlInvHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlInvHeader.setName("pnlInvHeader"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtInvRef.setBackground(resourceMap.getColor("txtUser.background")); // NOI18N
        txtInvRef.setEditable(false);
        txtInvRef.setFont(resourceMap.getFont("txtInvRef.font")); // NOI18N
        txtInvRef.setText(resourceMap.getString("txtInvRef.text")); // NOI18N
        txtInvRef.setName("txtInvRef"); // NOI18N

        txtUser.setBackground(resourceMap.getColor("txtUser.background")); // NOI18N
        txtUser.setEditable(false);
        txtUser.setFont(resourceMap.getFont("txtUser.font")); // NOI18N
        txtUser.setText(resourceMap.getString("txtUser.text")); // NOI18N
        txtUser.setName("txtUser"); // NOI18N

        datePicker.setDate(new java.util.Date());
        datePicker.setFont(resourceMap.getFont("datePicker.font")); // NOI18N
        datePicker.setName("datePicker"); // NOI18N

        cmbTerms.setFont(resourceMap.getFont("cmbTerms.font")); // NOI18N
        cmbTerms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "CIA", "COD", "CWO", "Net monthly account", "Net 7", "Net 10", "Net 30", "Net 60", "Net 90" }));
        cmbTerms.setName("cmbTerms"); // NOI18N

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
                    .addComponent(txtUser, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtInvRef, javax.swing.GroupLayout.Alignment.LEADING)
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(pnlInvHeader, gridBagConstraints);

        jToolBar2.setBackground(resourceMap.getColor("jToolBar2.background")); // NOI18N
        jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(1);
        jToolBar2.setRollover(true);
        jToolBar2.setToolTipText(resourceMap.getString("jToolBar2.toolTipText")); // NOI18N
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnRemoveLine.setFont(resourceMap.getFont("btnRemoveLine.font")); // NOI18N
        btnRemoveLine.setIcon(resourceMap.getIcon("btnRemoveLine.icon")); // NOI18N
        btnRemoveLine.setText(resourceMap.getString("btnRemoveLine.text")); // NOI18N
        btnRemoveLine.setToolTipText(resourceMap.getString("btnRemoveLine.toolTipText")); // NOI18N
        btnRemoveLine.setFocusable(false);
        btnRemoveLine.setName("btnRemoveLine"); // NOI18N
        btnRemoveLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveLineActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveLine);

        btnReturnItem.setFont(resourceMap.getFont("btnReturnItem.font")); // NOI18N
        btnReturnItem.setIcon(resourceMap.getIcon("btnReturnItem.icon")); // NOI18N
        btnReturnItem.setText(resourceMap.getString("btnReturnItem.text")); // NOI18N
        btnReturnItem.setToolTipText(resourceMap.getString("btnReturnItem.toolTipText")); // NOI18N
        btnReturnItem.setFocusable(false);
        btnReturnItem.setName("btnReturnItem"); // NOI18N
        btnReturnItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnItemActionPerformed(evt);
            }
        });
        jToolBar2.add(btnReturnItem);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        pnlMain.add(jToolBar2, gridBagConstraints);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        pnlMain.add(jLabel1, gridBagConstraints);

        jToolBar3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(1);
        jToolBar3.setRollover(true);
        jToolBar3.setToolTipText(resourceMap.getString("jToolBar3.toolTipText")); // NOI18N
        jToolBar3.setName("jToolBar3"); // NOI18N

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

        btnReturnAdditionalItem.setFont(resourceMap.getFont("btnReturnAdditionalItem.font")); // NOI18N
        btnReturnAdditionalItem.setIcon(resourceMap.getIcon("btnReturnAdditionalItem.icon")); // NOI18N
        btnReturnAdditionalItem.setToolTipText(resourceMap.getString("btnReturnAdditionalItem.toolTipText")); // NOI18N
        btnReturnAdditionalItem.setFocusable(false);
        btnReturnAdditionalItem.setName("btnReturnAdditionalItem"); // NOI18N
        btnReturnAdditionalItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnAdditionalItemActionPerformed(evt);
            }
        });
        jToolBar3.add(btnReturnAdditionalItem);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        pnlMain.add(jToolBar3, gridBagConstraints);

        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(152, 80));

        tblAdditionalInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Service Title", "Charge"
            }
        ));
        tblAdditionalInvLine.setFont(resourceMap.getFont("tblAdditionalInvLine.font")); // NOI18N
        tblAdditionalInvLine.setName("tblAdditionalInvLine"); // NOI18N
        jScrollPane3.setViewportView(tblAdditionalInvLine);
        tblAdditionalInvLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title0")); // NOI18N
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMinWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setMaxWidth(80);
        tblAdditionalInvLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblAdditionalInvLine.columnModel.title1")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        pnlMain.add(jScrollPane3, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
        pnlMain.add(jLabel2, gridBagConstraints);

        lblVoid.setFont(resourceMap.getFont("lblVoid.font")); // NOI18N
        lblVoid.setForeground(resourceMap.getColor("lblVoid.foreground")); // NOI18N
        lblVoid.setText(resourceMap.getString("lblVoid.text")); // NOI18N
        lblVoid.setName("lblVoid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        pnlMain.add(lblVoid, gridBagConstraints);

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 121));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jLabel9, gridBagConstraints);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtInvRemark.setColumns(20);
        txtInvRemark.setFont(resourceMap.getFont("txtInvRemark.font")); // NOI18N
        txtInvRemark.setLineWrap(true);
        txtInvRemark.setRows(5);
        txtInvRemark.setToolTipText(resourceMap.getString("txtInvRemark.toolTipText")); // NOI18N
        txtInvRemark.setName("txtInvRemark"); // NOI18N
        jScrollPane5.setViewportView(txtInvRemark);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMain.add(jPanel2, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(527, 200));

        tblInvLine.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "Service Title", "LineRemark", "Gross Charge", "Disc", "Qty", "Net Payable", "VAT"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvLine.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblInvLine.setFont(resourceMap.getFont("tblInvLine.font")); // NOI18N
        tblInvLine.setName("tblInvLine"); // NOI18N
        tblInvLine.setSortable(false);
        tblInvLine.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblInvLine);
        tblInvLine.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title0")); // NOI18N
        tblInvLine.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title1")); // NOI18N
        tblInvLine.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title7")); // NOI18N
        tblInvLine.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title2")); // NOI18N
        tblInvLine.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title3")); // NOI18N
        tblInvLine.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title4")); // NOI18N
        tblInvLine.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title5")); // NOI18N
        tblInvLine.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblInvLine.columnModel.title6")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        pnlMain.add(jScrollPane2, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(302, 150));

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "T.Date", "T.Type", "T.Amount", "Remark"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaction.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblTransaction.setEditable(false);
        tblTransaction.setFont(resourceMap.getFont("tblTransaction.font")); // NOI18N
        tblTransaction.setName("tblTransaction"); // NOI18N
        tblTransaction.setSortable(false);
        tblTransaction.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTransaction);
        tblTransaction.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title0")); // NOI18N
        tblTransaction.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title1")); // NOI18N
        tblTransaction.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title2")); // NOI18N
        tblTransaction.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTransaction.columnModel.title3")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        pnlMain.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlMain, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLineActionPerformed
        int row = tblInvLine.getSelectedRow();
        if (row != -1) {
            OAccountingDocumentLine l = this.lines.get(row);
            
            if (l.getoAcDocLineId() == 0) {
                this.lines.remove(l);
            } else {
                oAcDocBo.deleteOAcDocLine(l);
            }
            LoadAcDoc acDoc = new LoadAcDoc(this.oAcDoc.getoAcDocId());
            acDoc.execute();
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveLineActionPerformed

    private void btnCreateInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateInvoiceActionPerformed
        createNewInvoice();
    }//GEN-LAST:event_btnCreateInvoiceActionPerformed

    private void txtVatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVatKeyReleased
}//GEN-LAST:event_txtVatKeyReleased

    private void txtVatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVatKeyPressed
}//GEN-LAST:event_txtVatKeyPressed

    private void txtVatInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtVatInputMethodTextChanged
        // TODO add your handling code here:
}//GEN-LAST:event_txtVatInputMethodTextChanged

    private void txtVatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVatFocusGained
        txtVat.selectAll();
}//GEN-LAST:event_txtVatFocusGained

    private void btnContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            Thread t = new Thread(new threadSearchContactable());
            t.start();
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
}//GEN-LAST:event_btnContactableSearchActionPerformed

    private void txtContactableSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContactableSearchFocusGained
        txtContactableSearch.selectAll();
}//GEN-LAST:event_txtContactableSearchFocusGained

    private void txtContactableSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactableSearchActionPerformed
        if (txtContactableSearch.getText().length() > 0) {
            new Thread(new threadSearchContactable()).start();
        } else {
            busyLabel.setText("Warning: No keyword...");
        }
}//GEN-LAST:event_txtContactableSearchActionPerformed

    private void btnReturnItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnItemActionPerformed
        int row = tblInvLine.getSelectedRow();
        if (row != -1 && row != tblInvLine.getRowCount() - 1) {
            returnItem(this.getLines().get(row));
            populatetblInvLine();
            populateDocument();
            btnIssueCNote.setEnabled(true);
            tblInvLine.setEditable(true);
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Return", "Item Return", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnReturnItemActionPerformed

    private void btnIssueCNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIssueCNoteActionPerformed
        issueCNote();
    }//GEN-LAST:event_btnIssueCNoteActionPerformed

    private void btnTransWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransWizardActionPerformed
        ControllFrame cf = new ControllFrame();
        List<OAccountingDocument> oAcDocs = new ArrayList();
        oAcDocs = oAcDocBo.findAcDocByRef(oAcDoc.getAcDocRef());

        if (!oAcDocs.isEmpty()) {
            DlgOTransaction frameOTransaction = new DlgOTransaction(this);
            if (frameOTransaction.showTransactionDialog(oAcDocs)) {
                new Thread(new threadLoadCompleteAcDoc(oAcDoc.getAcDocRef())).start();
            } else {
                //new Thread (new threadLoadCompleteAcDoc()).start();
                frameOTransaction = null;
            }
        }
    }//GEN-LAST:event_btnTransWizardActionPerformed

    private void btnPPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPPreviewActionPerformed

        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());
        oAcDocBo.setoAcDoc(this.oAcDoc);
        oAcDocBo.setOfficeCopy(true);

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(oAcDocBo);
        rptAcDoc.viewOAcDoc(invObject);
    }//GEN-LAST:event_btnPPreviewActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());
        oAcDocBo.setoAcDoc(this.oAcDoc);
        oAcDocBo.setOfficeCopy(false);

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(oAcDocBo);
        rptAcDoc.printOAcDoc(invObject);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());
        oAcDocBo.setoAcDoc(this.oAcDoc);
        oAcDocBo.setOfficeCopy(false);

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(oAcDocBo);

        String emailTo = null;

        if (oAcDoc.getAgent() != null) {
            emailTo = oAcDoc.getAgent().getEmail();
        } else if (oAcDoc.getCustomer().getEmail() != null) {
            emailTo = oAcDoc.getCustomer().getEmail();
        }
        if (emailTo != null) {
            rptAcDoc.emailOAcDoc(emailTo, invObject);
        } else {
            JOptionPane.showMessageDialog(null, "No email address found!!!", "Email", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnPOfficeCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPOfficeCopyActionPerformed
        oAcDocBo.setmAgent(AuthenticationBo.getmAgent());
        oAcDocBo.setoAcDoc(this.oAcDoc);
        oAcDocBo.setOfficeCopy(true);
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();
        invObject.add(oAcDocBo);
        rptAcDoc.printOAcDoc(invObject);
    }//GEN-LAST:event_btnPOfficeCopyActionPerformed

    private void btnRemoveLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLine1ActionPerformed
        int row = tblAdditionalInvLine.getSelectedRow();
        if (row != -1) {
            OAccountingDocumentLine l = this.additionalLines.get(row);
            
            if (l.getoAcDocLineId() == 0) {
                this.lines.remove(l);
            } else {
                oAcDocBo.deleteOAcDocLine(l);
            }
            LoadAcDoc acDoc = new LoadAcDoc(this.oAcDoc.getoAcDocId());
            acDoc.execute();
        } else {
            JOptionPane.showMessageDialog(null, "Select Item to Remove", "New Invoice", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveLine1ActionPerformed

    private void btnReturnAdditionalItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnAdditionalItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnReturnAdditionalItemActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (isEditable == false) {
            PopulateCmbServiceTitle p = new PopulateCmbServiceTitle();
            p.execute();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    isEditable = true;
                    btnUpdate.setEnabled(true);
                    initControlls();
                }
            });
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        btnUpdate.setEnabled(false);
        this.oAcDoc.setIssueDate(datePicker.getDate());
        this.oAcDoc.setAcDoctype(1);
            if (cmbTerms.getSelectedIndex() > 0) {
                oAcDoc.setTerms((String) cmbTerms.getSelectedItem());
            }
        this.oAcDoc.setRemark(txtInvRemark.getText().trim());
            
        initAcDocInLine(oAcDoc, this.lines);
        initAcDocInLine(oAcDoc, this.additionalLines);

        for (OAccountingDocumentLine l : this.lines) {
            if (l.getoAcDocLineId() == 0) {
                this.oAcDoc.addLine(l);
            }
        }
        for (OAccountingDocumentLine l : this.additionalLines) {
            if (l.getoAcDocLineId() == 0) {
                this.oAcDoc.addLine(l);
            }
        }

        new Thread(new threadSaveAcDoc(this.oAcDoc)).start();
    }//GEN-LAST:event_btnUpdateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                //new FrameOAcDoc().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContactableSearch;
    private javax.swing.JButton btnCreateInvoice;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnIssueCNote;
    private javax.swing.JButton btnPOfficeCopy;
    private javax.swing.JButton btnPPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRemoveLine;
    private javax.swing.JButton btnRemoveLine1;
    private javax.swing.JButton btnReturnAdditionalItem;
    private javax.swing.JButton btnReturnItem;
    private javax.swing.JButton btnTransWizard;
    private javax.swing.JButton btnUpdate;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.ButtonGroup buttonGroup1;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblContactibleDetails;
    private javax.swing.JLabel lblReceived;
    private javax.swing.JLabel lblSearchClient;
    private javax.swing.JLabel lblVoid;
    private javax.swing.JPanel pnlInvHeader;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAgent;
    private javax.swing.JRadioButton rdoCustomer;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblAdditionalInvLine;
    private org.jdesktop.swingx.JXTable tblInvLine;
    private org.jdesktop.swingx.JXTable tblTransaction;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextArea txtContactableDetails;
    private javax.swing.JTextField txtContactableSearch;
    private javax.swing.JTextField txtInvAmount;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextArea txtInvRemark;
    private javax.swing.JTextField txtReceived;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVat;
    // End of variables declaration//GEN-END:variables
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @return the lines
     */
    public List<OAccountingDocumentLine> getLines() {
        return lines;
    }

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

            services = oServiceBo.loadAllServices();
            for (OtherService s : services) {
                if (s.getServiceType() == 1) {
                    oServices.add(s);
                } else if (s.getServiceType() == 2) {
                    additionalServices.add(s);
                }
            }
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

        OAccountingDocument oAcDoc;

        public threadSaveAcDoc(OAccountingDocument oAcDoc) {
            this.oAcDoc = oAcDoc;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Creating new invoice...");

            oAcDocBo.setoAcDoc(oAcDoc);
            oAcDocBo.saveOrUpdateOAcDoc();

            statusMessageLabel.setText("Loading new invoice...");
            oAcDoc = oAcDocBo.getoAcDoc();
            loadObjects(oAcDoc);
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadLoadCompleteAcDoc implements Runnable {

        private long acDocId;
        int invRef;

        public threadLoadCompleteAcDoc(int invRef) {
            this.invRef = invRef;
        }

        public void run() {

            //progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Searching...");

            oAcDoc = oAcDocBo.findCompleteAcDocByByRef(invRef);

            lines = new ArrayList(oAcDoc.getNormalService());
            additionalLines = new ArrayList(oAcDoc.getAdditionalService());

            populateDocumentHeader();
            populatetblInvLine();
            populateDocument();
            populateTblTransaction(oAcDoc.getAcTransactions());
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Search Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class PopulateCmbServiceTitle extends SwingWorker<List<OtherService>, Void> {

        public PopulateCmbServiceTitle() {
            oServices.clear();
            additionalServices.clear();
        };
        
        @Override
        protected List<OtherService> doInBackground() throws Exception {
            List<OtherService> services = oServiceBo.loadAllServices();
            return services;
        }

        @Override
        protected void done() {
            try {
                services = get();
                for (OtherService s : services) {
                    if (s.getServiceType() == 1) {
                        oServices.add(s);
                    } else if (s.getServiceType() == 2) {
                        additionalServices.add(s);
                    }
                }

                List cmbElement = new ArrayList();
                List cmbAdditionalElement = new ArrayList();

                for (OtherService o : oServices) {
                    cmbElement.add(o.getServiceTitle());
                }
                for (OtherService o : additionalServices) {
                    cmbAdditionalElement.add(o.getServiceTitle());
                }
                cmbSTitleModel = new DefaultComboBoxModel(cmbElement.toArray());
                cmbSTitle.setModel(cmbSTitleModel);
                cmbSTitle.addActionListener(cmbSTitleListener);

                cmbAdditionalSTitleModel = new DefaultComboBoxModel(cmbAdditionalElement.toArray());
                cmbAdditionalSTitle.setModel(cmbAdditionalSTitleModel);
                cmbAdditionalSTitle.addActionListener(cmbAdditionalSTitleListener);
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
   private class LoadAcDoc extends SwingWorker<OAccountingDocument, Void> {

        private long id;

        public LoadAcDoc(long id) {
            this.id = id;
        }

       @Override
       protected OAccountingDocument doInBackground() throws Exception {
           OAccountingDocument fullAcDoc = null;
           fullAcDoc = oAcDocBo.findCompleteAcDocById(id);

           return fullAcDoc;
       }

        @Override
        protected void done() {
            try {
                oAcDoc = get();                
            } catch (InterruptedException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrameSAcDocHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadObjects(oAcDoc);            
        }
    }          
}
