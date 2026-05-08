package org.gnucash.viewer;

import java.util.Locale;

import org.apache.commons.numbers.fraction.BigFraction;
import org.gnucash.api.read.impl.GnuCashAccountImpl;

public class GUIServices
{
	
    // ---------------------------------------------------------------
	// Redundant wrappers for convenience.
	
	public static String formatBalance(GnuCashAccountImpl acct, BigFraction blnc) {
		return GnuCashAccountImpl.formatBalanceRat( acct, blnc );
	}
	
	public static String formatBalance(GnuCashAccountImpl acct, BigFraction blnc, Locale lcl) {
		return GnuCashAccountImpl.formatBalanceRat( acct, blnc, lcl );
	}
	
}
