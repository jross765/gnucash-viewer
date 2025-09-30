package org.gnucash.viewer.actions;

import javax.swing.Action;

import org.gnucash.api.read.GnuCashTransactionSplit;

/**
 * Action that can be executed on a {@link GnuCashTransactionSplit}
 */
public interface TransactionSplitAction extends Action {

    /**
     * Set the split this action works on.
     * @param aSplit the split to work.
     */
    void setSplit(final GnuCashTransactionSplit aSplit);
}
