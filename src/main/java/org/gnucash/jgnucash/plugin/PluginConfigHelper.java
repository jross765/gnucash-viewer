package org.gnucash.jgnucash.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableFile;

/**
 * This class contains helper-methods to simplify the implementation
 * of plugins.
 */
public final class PluginConfigHelper {

	/**
	 * Helper-classes have no public constructor.
	 */
	private PluginConfigHelper() {
	}

	/**
	 * Find the first account that has a value for the given
	 * key in  it's user-defined properties.
	 *
	 * @param aModel the book to operate on
	 * @param aKey   the key to look for
	 * @return an account or null
	 */
	@SuppressWarnings("unchecked")
	public static GnuCashWritableAccount getAccountWithKey(final GnuCashWritableFile aModel, final String aKey) {
		Collection<? extends GnuCashWritableAccount> accounts = aModel.getWritableAccounts();
		for (GnuCashWritableAccount gnucashAccount : accounts) {
			if (gnucashAccount.getUserDefinedAttribute(aKey) != null) {
				return gnucashAccount;
			}
		}
		return null;
	}

	/**
	 * Find all accounts that have a value for the given
	 * key in their user-defined properties.
	 *
	 * @param aModel the book to operate on
	 * @param aKey   the key to look for
	 * @return an account or null
	 */
	@SuppressWarnings("unchecked")
	public static Collection<GnuCashWritableAccount> getAllAccountsWithKey(final GnuCashWritableFile aModel, final String aKey) {
		Collection<GnuCashWritableAccount> retval = new HashSet<GnuCashWritableAccount>();
		Collection<? extends GnuCashWritableAccount> accounts = aModel.getWritableAccounts();
		for (GnuCashWritableAccount gnucashAccount : accounts) {
			if (gnucashAccount.getUserDefinedAttribute(aKey) != null) {
				retval.add(gnucashAccount);
			}
		}
		return retval;
	}

	/**
	 * Either get the first account that has any value for the given
	 * key in it's userDefinedProperties or ask the user to select
	 * an account and add the key.
	 *
	 * @param aModel        the book to operate on
	 * @param aKey          the key to look for
	 * @param aDefaultValue the value to apply if an account needed to be selected
	 * @param aQuestion     the translated question to ask the user when selecting an account.
	 * @return the account
	 * @see #getAccountWithKey(GnuCashWritableFile, String)
	 */
	@SuppressWarnings("unchecked")
	public static GnuCashWritableAccount getOrConfigureAccountWithKey(final GnuCashWritableFile aModel,
			final String aKey,
			final String aDefaultValue,
			final String aQuestion) {
		GnuCashWritableAccount retval = getAccountWithKey(aModel, aKey);
		if (retval != null) {
			return retval;
		}

		final JDialog selectAccountDialog = new JDialog((JFrame) null, "select account");
		selectAccountDialog.getContentPane().setLayout(new BorderLayout());
		final JList folderListBox = new JList(new Vector(aModel.getWritableAccounts()));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent aE) {
				if (folderListBox.getSelectedIndices() != null) {
					if (folderListBox.getSelectedIndices().length == 1) {
						selectAccountDialog.setVisible(false);
					}
				}
			}

		});
		selectAccountDialog.getContentPane().add(new JLabel(aQuestion), BorderLayout.NORTH);
		selectAccountDialog.getContentPane().add(new JScrollPane(folderListBox), BorderLayout.CENTER);
		selectAccountDialog.getContentPane().add(okButton, BorderLayout.SOUTH);
		selectAccountDialog.setModal(true);
		selectAccountDialog.pack();
		selectAccountDialog.setVisible(true);
		retval = (GnuCashWritableAccount) folderListBox.getSelectedValue();
		retval.setUserDefinedAttribute(aKey, aDefaultValue);
		return retval;
	}

	/**
	 * Either get the value as defined on the root account in it's userDefinedProperties
	 * or ask the user to enter one and add the key.
	 *
	 * @param aRootAccount  the root-account to operate on
	 * @param aKey          the key to look for
	 * @param aDefaultValue the default value to present to the user
	 * @param aQuestion     the translated question to ask the user when selecting an account.
	 * @return the entered value (not empty)
	 * @see #getOrConfigureStringWithKey(GnuCashWritableAccount, String, String, String)
	 */
	@SuppressWarnings("unchecked")
	public static String getOrConfigureStringWithKey(final GnuCashWritableAccount aRootAccount,
			final String aKey,
			final String aDefaultValue,
			final String aQuestion) {
		if (aRootAccount == null) {
			throw new IllegalArgumentException("null root account given!");
		}
		String retval = aRootAccount.getUserDefinedAttribute(aKey);
		if (retval != null) {
			return retval;
		}

		final JDialog selectAccountDialog = new JDialog((JFrame) null, "missing value");
		selectAccountDialog.getContentPane().setLayout(new BorderLayout());
		final JTextField folderListBox = new JTextField(aDefaultValue);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent aE) {
				if (folderListBox.getText().trim().length() > 0) {
					selectAccountDialog.setVisible(false);
				}
			}

		});
		selectAccountDialog.getContentPane().add(new JLabel(aQuestion), BorderLayout.NORTH);
		selectAccountDialog.getContentPane().add(new JScrollPane(folderListBox), BorderLayout.CENTER);
		selectAccountDialog.getContentPane().add(okButton, BorderLayout.SOUTH);
		selectAccountDialog.setModal(true);
		selectAccountDialog.pack();
		selectAccountDialog.setVisible(true);
		retval = folderListBox.getText();
		aRootAccount.setUserDefinedAttribute(aKey, retval);
		return retval;
	}
}
