package org.gnucash.viewer.actions;

import javax.swing.Action;

import org.gnucash.api.read.GnuCashTransactionSplit;

/*
 * Action that can be executed on a GnuCashTransactionSplit
 */
public interface TransactionSplitAction extends Action {

    // Set the split this action works on.
    void setSplit(final GnuCashTransactionSplit aSplit);
}
