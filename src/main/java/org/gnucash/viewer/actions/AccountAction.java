package org.gnucash.viewer.actions;

import javax.swing.Action;

import org.gnucash.api.read.GnuCashAccount;

/**
 * Action that can be executed on a {@link GnuCashAccount}
 */
public interface AccountAction extends Action {

    // Set the account this action works on.
    void setAccount(final GnuCashAccount anAccount);
}
