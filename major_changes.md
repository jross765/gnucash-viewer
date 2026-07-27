# Major Changes 

## V. 1.2 &rarr; 1.3
* I18N: Added French language.

* Generalized rendering of unbalanced and/or tagged transactions.

  This is based on transaction (split) comments.

## V. 1.1 &rarr; 1.2
* Viewer can now be started with command line options:
    * With account ID: Will open new window with according account immediatly after start.
    * With transaction split ID (or alternatively: account ID and transaction ID): 
      Will open new window with according account immediatly after start (as above with 
      account-ID only), and will, additionally, mark the according transaction (split).

* Copy marked object's ID into clipboard 
  (context menu for accounts, transactions and splits).

* Fixed a few small bugs.

* A couple of minor improvements, both on the surface and under the hood.

## V. 1.0 &rarr; V. 1.1
* Re-defined scope:
  * Removed everything used for editing -- this is called a viewer, and it shall be one -- and *only* a viewer.
  * Removed some overly specific functionality
  * Removed plugin-stuff.

  Cf. section "Scope" below for details.

* Adapted to current state of GnuCash API

* I18N: 
  * Extracted all user-facing texts (in English) to properties files
  * Generated one set of properties files for German.

* Tool-tips for transaction splits.

* Some overall clean-up work (amongst others: introduced enums for table columns, which greatly enhances security and readability of the code).

## V. 1.0
"New" -- well, not really new, but copied from

`https://github.com/rbertoli/gnucash`
(commit ccd9867cde12b365da3e65ff8725693d6b4a80fb)

(Originally written by Marcus Wolschon, 
maintained by Roberto Bertolino for a while).

Version tag by current maintainer.
