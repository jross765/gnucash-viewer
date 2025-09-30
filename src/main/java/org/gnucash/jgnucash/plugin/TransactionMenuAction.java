package org.gnucash.jgnucash.plugin;

import java.io.IOException;
import java.util.Collection;

import org.gnucash.api.write.GnuCashWritableTransactionSplit;


/**
 * Plugins of this kind apear in the context-menu for selected transactions}.
 */
public interface TransactionMenuAction {

    /**
     * Handle the selected transactions.
     * @param transactions the selection
     * @throws IOException on comunication-issues
     */
    void handleSelectedTransactions(final Collection<GnuCashWritableTransactionSplit> transactions) throws IOException;
}
