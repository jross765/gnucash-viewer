# Notes on the Module "Viewer"

## What Does It Do? 

A Swing-based GUI viewer for 
GnuCash 
XML-based files.

## Major Changes 
### V. 1.1
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

### V. 1.0
"New" -- well, not really, but:

Copied from https://github.com/rbertoli/gnucash
(commit ccd9867cde12b365da3e65ff8725693d6b4a80fb)

(Originally written by Marcus Wolschon, 
maintained by Roberto Bertolino for a while).

Version tag by current maintainer.

## Planned

* Starting GUI with a specific account's or transaction's ID (and the viewer showing the according panel immediately).

  Analogously with all other entities that may be supported in future (cf. below).

* Copy marked object's ID into clipboard (context menu).

* Marking / rendering transactions by more general / flexible rules; extracting stuff like the "TODO" word into config files.

* Re-iterating tables and models -- I guess it would be better to handle transactions and transaction splits in completely separate classes (both in package `models` and `panels`) rather than in one class.

* Introducing detailed-view panels for each supported entity.

* Possibly (!) supporting additional entities:

  * Commodities
  * Customers / vendors / employees / jobs
  * Customer invoices / vendor bills / employee vouchers
  * Prices (low priority)

## Scope
The current author/maintainer has made some important decisions on this module's scope, design and design/future development in this project
(cf. commits db120fec10f199f8af0b9788c4c113c9a1134344 and db120fec10f199f8af0b9788c4c113c9a1134344):

### General
   
* I will separate the viewer from the file editor GUI.

  Just as on the source code level, where the code for the viewer and editor were already separated, I will do the same on the module/JAR file level.
  This module will do just what it says it does: viewing (and not editing!) GnuCash files (= the package `viewer`).

    The editor package (`jgnucash`) is being deleted from this
  module and can/will *possibly* re-emerge in another module
   -- which is not part of this project (yet).

  Writing and maintaining a read-write GUI is *far* more demanding
  a task than writing/maintaining a read-only viewer. Neither do
  I have the time for the former nor the know-how nor do I have the
  need for it in my business' daily activities.

  But if someone else has the need for it, knows how and wants to
  do it: Feel free to open another repo/module, containing the
  code of the former package `jgnucash` and building on this module's
  foundation.

* The viewer shall remain simple.
  
  The point of this module is *not* to have "a simple Java-based
  GnuCash GUI" (quoting the original author), but rather a sort
  of "gimmick"/technical study/demonstration of what you can do
  with that; an add-on that is nice to have (as opposed to the
  other modules, where e.t. is supposed to be/is actually being
  used in running a serious business' daily activities).
  
  A real-world use case that I see for this viewer (for me
  and others) is: Calling it from a script with user interactiion
  (checks by a human being necessary); you call the viewer with a
  transaction ID, say, and -- bam! -- the viewer shows that transaction
  right away with all its details. Similar with the other entities.

  Apart from that: Honestly, I don't see the need/the real value
  in re-writing the GnuCash GUI in Java. The whole point of this
  project is to have a Java lib and tools for automation and bulk
  editing (thus, typically with a CLI-based tool and not with a GUI)
  and/or for things that you cannot do (or only painstakingly do)
  with the standard GUI. Things that you already can do with the
  standard GUI (such as HBCI/FinTS online banking or CSV import)
  should be done with it, possibly with some preparation steps
  with specialized tools *outside* of this project.

  (To be fair: The original author started this project in 2007
  or so, and back then, GnuCash was not as advanced as it is today.
  I can imagine that, in the olden days, there actually was the need
  to write certain things on your own that today you would typically
  do with the standard GUI.)

* No plugins for the viewer.

  For one: Keep things simple (cf. above). Apart from that:
  The plugins written by the original author are nearly all:

  * either editor/import plugins (thus, not read-only);
  * or too specifically tailored to his particular needs;
  * or obsolete.

  Now, I get it: Why not keep the plugin *architecture* (as opposed 
to this or that specific plugin) and just get rid of the obsolete 
plugins/write new ones; possibly in another module? Nothing wrong 
with that approach, in theory. In practice, however: The only 
read-only plugins that I could possibly use in the viewer are:
  * the simple search plugin
  * [ possibly also the tax-reporting panel (which, interestingly
    enough, is not a plugin). ]

  Plugins for a read-write editor GUI? Absolutely, yes! But from now on,
  this is outside of this module's scope.
  Plugins for a read-only viewer GUI? Seems oversized to me.
  In this regard, I am following the original author's approach.

As interesting a project it would be to completely re-write 
GnuCash
in Java: I have other things to do, and therefore I will keep things
simple and thus, I will hold my horses and limit the scope accordingly.

### Special Case: Tax-Report Panel
I deleted tax report panel and its example XML config file.
    
**Rationale**:

* It is outside of this module's scope:

  This is a simple file *viewer* (cf. the lengthy reasoning above).
  Tax computations / non-trivial views are definitely out of scope.
The only aggregations supported are the ones defined by the account hierarchy.
    
* IT architecture:

  I would rather have the business logic of this code in a special package
  in the module "API Extensions" than here in a GUI panel's class.
  As a general rule of thumb, a GUI shall contain no business logic and rather be "stupid".
    
* Too specifically tailored to the original author's needs:

  * Config file: Granted, a good part of the business logic lies in the XML config file, which in turn is provided as a mere *example*; However, the implementation suggests this file being compiled into the JAR file rather than loaded from the GUI (which would make more sense here).

    Apart from that: The XML file only makes sense when using the German standard SKR03/04 chart of accounts -- and even within this context, it's seems very special / arbitrary to me.

  * Java code: Admittedly, I have not reviewed the code of the tax report panel very thoroughly, but my current half-solid assessment is: Too specific.

**Notes**:

* I am neither an accountant nor a tax advisor. However, I happen to be the
  owner-manager of a German company myself, just as the orig. author is, and so I know a thing or two about what you *typically* (might) need to prepare your tax statement.

  Seen through this lens, I would deem the tax report panel, as originally implemented, too specific / not enough of general interest to the "general public", i.e. typical user of this project's libs.

  That does not mean, of course, that the code is badly written or that one should not write such code when one needs it. But it just does not match the criteria for this module.

* I already had removed other parts of the code in the early stages of my work on the API -- there, the orig. author also had left some overly specific and partially obsolete code, which obviously made some sense in his business' context, but not in this project, which takes a broader approach (cf. notes for V. 1.1 of the API module).
    
* Again: Even if one day it turns out that I was wrong with that judgment ("too specific"): I would still maintain: Not in this module, but rather in "API Extensions" and/or "API Examples". And Git preserves all the history...

## Known Issues

Veeery slow -- it takes some 30 s or so to load a larger real-life file (not the viewer itself, in fact, but the underlying API). 

This, in the current maintainer's opinion, is not so important for CLI based tools (cf. module "Tools"), and only partly relevant for a GUI (it takes long to load a file, but once it is loaded, e.t. runs fast and smoothly); but the above-mentioned calls for specific accounts / transactions (planned) would only partially make sense in a real-world scenario.
