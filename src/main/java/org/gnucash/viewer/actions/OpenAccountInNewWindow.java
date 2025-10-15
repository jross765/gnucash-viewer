package org.gnucash.viewer.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.viewer.panels.TransactionsPanel;

/*
 * Action to open an account in a new tab.
 */
public class OpenAccountInNewWindow implements AccountAction,
        TransactionSplitAction {

    // The account we open.
    private GnuCashAccount myAccount;

    private final Map<String, Object> myAddedTags = new HashMap<String, Object>();

    private final PropertyChangeSupport myPropertyChangeSupport = new PropertyChangeSupport(this);

    // Optional: the transaction to highlight
    private GnuCashTransaction myTransaction;

    /*
     * Initialize.
     */
    public OpenAccountInNewWindow() {
        this.putValue(Action.NAME, Messages_OpenAccountInNewWindow.getString("OpenAccountInNewWindow.1"));
        this.putValue(Action.LONG_DESCRIPTION, Messages_OpenAccountInNewWindow.getString("OpenAccountInNewWindow.2"));
        this.putValue(Action.SHORT_DESCRIPTION, Messages_OpenAccountInNewWindow.getString("OpenAccountInNewWindow.3"));
    }

    public OpenAccountInNewWindow(final GnuCashTransactionSplit aSplit) {
        this();
        setSplit(aSplit);
    }

    public OpenAccountInNewWindow(final GnuCashAccount anAccount) {
        this();
        setAccount(anAccount);
    }

    @Override
    public void setAccount(final GnuCashAccount anAccount) {
        myAccount = anAccount;
        myTransaction = null;
    }

    @Override
    public void setSplit(final GnuCashTransactionSplit aSplit) {
        myAccount = aSplit.getAccount();
        myTransaction = aSplit.getTransaction();
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener aListener) {
        myPropertyChangeSupport.addPropertyChangeListener(aListener);
    }

    @Override
    public Object getValue(final String aKey) {
        return myAddedTags.get(aKey);
    }

    @Override
    public boolean isEnabled() {
        return myAccount != null;
    }

    @Override
    public void putValue(final String aKey, final Object aValue) {
        myAddedTags.put(aKey, aValue);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener aListener) {
        myPropertyChangeSupport.removePropertyChangeListener(aListener);
    }

    @Override
    public void setEnabled(final boolean aB) {
    }

    @Override
    public void actionPerformed(final ActionEvent aE) {
                TransactionsPanel newPanel = new TransactionsPanel();
        newPanel.setAccount(myAccount);
        if (myTransaction != null) {
            newPanel.setTransaction(myTransaction);
        }
        JFrame newFrame = new JFrame(myAccount.getName());
        newFrame.getContentPane().add(newPanel);
        newFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        newFrame.pack();
        newFrame.setVisible(true);
    }

}
