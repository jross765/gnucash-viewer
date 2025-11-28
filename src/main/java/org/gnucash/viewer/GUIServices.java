package org.gnucash.viewer;

import java.util.Locale;

import org.gnucash.api.read.impl.GnuCashAccountImpl;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GUIServices
{
	
    // ---------------------------------------------------------------
	// Redundant wrappers for convenience.
	
	public static String formatBalance(GnuCashAccountImpl acct, FixedPointNumber blnc) {
		return GnuCashAccountImpl.formatBalance( acct, blnc );
	}
	
	public static String formatBalance(GnuCashAccountImpl acct, FixedPointNumber blnc, Locale lcl) {
		return GnuCashAccountImpl.formatBalance( acct, blnc, lcl );
	}
	
}
