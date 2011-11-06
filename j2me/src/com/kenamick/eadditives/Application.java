/*
 * File: Application.java
 * Author: p.petrov
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kenamick.eadditives;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import com.sun.perseus.model.Font;

public class Application implements CommandListener, ItemCommandListener, Runnable {
	
	private static Thread _thread;
	private static boolean _isLoaded = false;	
	private static boolean _isPaused = false;
	private static String _tmpString = null,
						_tmpString2 = null;
	private static StringBuffer _tmpStringBuffer = new StringBuffer(32);
	private static StringItem _si = null;
	private static Spacer	_sp = null;
//	private static long _splashTimeout = 0;
	
	private static int _state = Defs.STATE_LOADING; // default
	
	private static Command _cmdOpen;
	private static Command _cmdUpdate;
	private static Command _cmdAbout;
	private static Command _cmdGroups;
    private static Command _cmdQuit;
    private static Command _cmdBack;
    private static Command _cmdWeb;
//    private static Command _cmdNext;
//    private static Command _cmdSearch;
    private static Command _cmdSearchItem;
    private static Command _cmdOptions;
    private static Command _cmdDownload;
    private static Command _cmdViewHistory;
    
    private static Form _frmSplash;
    private static Image _imgLogoBig = null;
    private static Image _imgLogoSmall = null;    
    
    private static Form _frmSearch;
    private static TextField _tSearch;
    private static String _lastSearch = "";
    //private static long _lastSearchStartTime = -1;
    private static int _itemsCount = 0;
    private static int _searchMode = Defs.SEARCH_MODE_NUMBER;
    private static ImageItem _iiSearchHint = null;
    private static ImageItem _iiSearchNotFound = null;
    
    private static Form _frmDetails;
    private static Image[] _imgIcons = null;
    
    private static Form _frmGroups;
    private static List _listGroups;
    
    private static Form _frmAbout;
    
    private static Alert _alertUpdateOk;
    private static Alert _alertUpdateError;
    private static Vector csvValues = new Vector();
    private static String _updateUrl = Defs.WEB_DEFAULT_DOWNLOAD_URL;
    
    private static Form _frmOptions;
    private static ChoiceGroup _chSearchType;
	
	public Application() {
		this.setState(Defs.STATE_LOADING);
	}
	
	private void init() {
		
		// --- commands
		_cmdQuit = new Command(l10n.tx("quit"), Command.EXIT, 2);
		_cmdBack = new Command(l10n.tx("back"), Command.BACK, 2);
//		_cmdNext = new Command(l10n.tx("next"), Command.OK, 1);		
		_cmdWeb = new Command(l10n.tx("web"), Command.SCREEN, 2);
		_cmdGroups = new Command(l10n.tx("show_additive_groups"), Command.SCREEN, 20);
		_cmdUpdate = new Command(l10n.tx("update"), Command.SCREEN, 40);
		_cmdAbout = new Command(l10n.tx("about"), Command.SCREEN, 50);
		_cmdOptions = new Command(l10n.tx("opts"), Command.HELP, 33);
		_cmdOpen = new Command(l10n.tx("open"), Command.ITEM, 1);
//		_cmdSearch = new Command(l10n.tx("search"), Command.SCREEN, 0); //Command.ITEM, 1);
		_cmdSearchItem = new Command(l10n.tx("search"), Command.ITEM, 0);
		_cmdDownload = new Command(l10n.tx("download"), Command.SCREEN, 2);
		_cmdViewHistory = new Command(l10n.tx("vwchanges"), Command.SCREEN, 2);
		
		// --- options 
		_frmOptions = new Form(l10n.tx("opts_title"));
		_frmOptions.addCommand(_cmdBack);
		_frmOptions.setCommandListener(this);
		
//		ChoiceGroup ch = new ChoiceGroup("Database", ChoiceGroup.EXCLUSIVE);
//		ch.append("Online", null);
//		ch.append("Offline", null);
//		ch.setItemCommandListener(this);
//		_frmOptions.append(ch);
		_chSearchType = new ChoiceGroup(l10n.tx("srchtype"), ChoiceGroup.EXCLUSIVE);
		_chSearchType.append(l10n.tx("search_by_num"), null);
		_chSearchType.append(l10n.tx("search_by_name"), null);
		_chSearchType.setItemCommandListener(this);
		_frmOptions.append(_chSearchType);

		// --- search
		_frmSearch = new Form(l10n.tx("app_title"));
//		_frmSearch.addCommand(_cmdSearch);
		_frmSearch.addCommand(_cmdGroups);
		_frmSearch.addCommand(_cmdUpdate);
		_frmSearch.addCommand(_cmdOptions);
		_frmSearch.addCommand(_cmdAbout);
		_frmSearch.addCommand(_cmdQuit);
		_frmSearch.setCommandListener(this);
		
		try {
			ImageItem ii = new ImageItem(null, _imgLogoSmall, 
					ImageItem.LAYOUT_NEWLINE_AFTER | ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_2, 
					Defs.APP_NAME);
			_frmSearch.append(ii);
		} 
		catch (Exception e) {
			//#mdebug
			e.printStackTrace();
			//#enddebug
		}
		
		_tSearch = new TextField(l10n.tx("search"), null, Defs.SEARCH_MAX_SIZE, TextField.DECIMAL);
		_tSearch.setLayout(TextField.LAYOUT_2 | TextField.LAYOUT_LEFT | TextField.LAYOUT_EXPAND | TextField.LAYOUT_NEWLINE_AFTER);
		_tSearch.addCommand(_cmdSearchItem);
		_tSearch.setItemCommandListener(this);
		
		setSearchMode(Defs.SEARCH_MODE_NUMBER);
		_frmSearch.append(_tSearch);

		// --- item details
		_frmDetails = new Form(l10n.tx("details_title"));
		_frmDetails.addCommand(_cmdBack);
		_frmDetails.setCommandListener(this);

		// --- show groups
		_frmGroups = new Form(l10n.tx("groups_title"));
		_frmGroups.addCommand(_cmdBack);
		_frmGroups.setCommandListener(this);
		
		_listGroups = new List(l10n.tx("groups_title"), List.IMPLICIT);
		_listGroups.addCommand(_cmdOpen);
		_listGroups.addCommand(_cmdBack);
		_listGroups.setSelectCommand(_cmdOpen);
		_listGroups.setCommandListener(this);
		
		_listGroups.append("E100-E199 " + l10n.tx("colors"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E200-E299 " + l10n.tx("preservatives"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E300-E399 " + l10n.tx("antioxidants"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E400-E499 " + l10n.tx("stabilizers"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E500-E599 " + l10n.tx("regulators"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E600-E699 " + l10n.tx("enhancers"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E700-E799 " + l10n.tx("antibiotics"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E900-E999 " + l10n.tx("miscellaneous"), _imgIcons[Defs.ICON_GROUP]);
		_listGroups.append("E1000-E1199 " + l10n.tx("chemicals"), _imgIcons[Defs.ICON_GROUP]);
		
		// --- update
		_alertUpdateOk = new Alert(l10n.tx("update_title"));
		_alertUpdateOk.setTimeout(Alert.FOREVER);
		_alertUpdateOk.setType(AlertType.INFO);
		_alertUpdateOk.setString(null);	
		_alertUpdateOk.addCommand(_cmdBack);
		_alertUpdateOk.addCommand(_cmdViewHistory);
		_alertUpdateOk.setCommandListener(this);

		_alertUpdateError = new Alert(l10n.tx("update_err_title"));
		_alertUpdateError.setTimeout(Alert.FOREVER);
		_alertUpdateError.setType(AlertType.ERROR);
		_alertUpdateError.setString(l10n.tx("update_error_msg"));
		_alertUpdateError.addCommand(_cmdBack);
		_alertUpdateError.setCommandListener(this);

		// --- about
		_frmAbout = new Form(l10n.tx("about_title"));
		_frmAbout.addCommand(_cmdBack);
		_frmAbout.addCommand(_cmdWeb);
		_frmAbout.setCommandListener(this);
		
		_si = new StringItem(null, l10n.tx("about") + " " + Defs.APP_NAME);
		_si.setLayout(Defs.ABOUT_STYLE); _si.setFont(Defs.FONT_BIG);
		_frmAbout.append(_si);		
		
		try {
			ImageItem ii = new ImageItem(null, _imgLogoBig, 
					ImageItem.LAYOUT_NEWLINE_AFTER | ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_2, 
					Defs.APP_NAME);
			_frmAbout.append(ii);
		} 
		catch (Exception e) {
			//#mdebug
			e.printStackTrace();
			//#enddebug
		}			

		_si = new StringItem(null, Defs.APP_NAME + " " + CoreMidlet._instance.getAppProperty("MIDlet-Version"));
		_si.setLayout(Defs.ABOUT_STYLE); _si.setFont(Defs.FONT_MEDIUM);
		_frmAbout.append(_si);
		_si = new StringItem(null, l10n.tx("total_addt") + l10n.getItemsCount() );
		_si.setLayout(Defs.ABOUT_STYLE | Item.LAYOUT_NEWLINE_BEFORE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);
		_si = new StringItem(null, "(c) Copyright " + CoreMidlet._instance.getAppProperty("MIDlet-Vendor"));
		_si.setLayout(Defs.ABOUT_STYLE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);	
		_si = new StringItem(null, Defs.WEB_URL);
		_si.setLayout(Defs.ABOUT_STYLE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);
		_si = new StringItem(null, l10n.tx("build") + " " + Defs.APP_BUILD_DATE);
		_si.setLayout(Defs.ABOUT_STYLE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);		
		
		// add one line space
		_sp = new Spacer(1, 5);
		_sp.setLayout(Spacer.LAYOUT_2 | Spacer.LAYOUT_EXPAND | Spacer.LAYOUT_NEWLINE_AFTER);
		_frmAbout.append(_sp);		
		_si = new StringItem(null, l10n.tx("license"));
		_si.setLayout(Defs.ABOUT_STYLE | Item.LAYOUT_NEWLINE_BEFORE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);
		
		// add one line space
		_sp = new Spacer(1, 5);
		_sp.setLayout(Spacer.LAYOUT_2 | Spacer.LAYOUT_EXPAND | Spacer.LAYOUT_NEWLINE_AFTER);
		_frmAbout.append(_sp);		
		_si = new StringItem(null, l10n.tx("iconsnfo"));
		_si.setLayout(Defs.ABOUT_STYLE | Item.LAYOUT_NEWLINE_BEFORE); _si.setFont(Defs.FONT_SMALL);
		_frmAbout.append(_si);
		
	}
	
	private void createFormStringItem( String title, String text, Form _frm ) {
		createFormStringItem(title, text, _frm, -1);
	}
	
	private void createFormStringItem( String title, String text, Form _frm, int iconIndex ) {
		
		if ( iconIndex != -1 ) {
			ImageItem ii = new ImageItem(null, _imgIcons[iconIndex], ImageItem.LAYOUT_2 | ImageItem.LAYOUT_LEFT, "");
			_frm.append(ii);
		}
		
		_si = new StringItem(null, title);
		_si.setLayout(Defs.DETAILS_STYLE2); _si.setFont(Defs.DETAILS_TITLE_FONT);
		_frm.append(_si);
		_si = new StringItem(null, text);
		_si.setLayout(Defs.DETAILS_STYLE); _si.setFont(Defs.DETAILS_ITEM_FONT);
		_frm.append(_si);
		
		// add one line space
		_sp = new Spacer(1, 5);
		_sp.setLayout(Spacer.LAYOUT_2 | Spacer.LAYOUT_EXPAND | Spacer.LAYOUT_NEWLINE_AFTER);
		_frm.append(_sp);
	}
	
	private boolean checkForUpdate(String url) {
		
		boolean ret = false;
		HttpConnection hc = null;
		DataInputStream dis = null;
		csvValues.removeAllElements();
		
		try {
			hc = (HttpConnection)Connector.open(url);
			hc.setRequestMethod( HttpConnection.GET );
			hc.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
			hc.setRequestProperty("Content-Language", "en-US");
			hc.setRequestProperty("Connection", "close");
            
			if ( hc.getResponseCode() == HttpConnection.HTTP_OK ) {
				dis = new DataInputStream(hc.openInputStream());
				
				int c = 0;
				_tmpStringBuffer.setLength(0);
				do {
					
					c = dis.read();
					if ( c == -1 )
						break;
					
					if ( (char)c == ';' ) {
						csvValues.addElement(_tmpStringBuffer.toString());
						//#mdebug
						System.out.println("Fond value =" + _tmpStringBuffer.toString());
						//#enddebug
						_tmpStringBuffer.setLength(0);
						continue;
					}

					_tmpStringBuffer.append( (char)c );
					
				} while (c > 0);
				
				
				if ( csvValues.size() > 2 && csvValues.elementAt(0).equals(Defs.UPDATE_HEADER) ) {
					_tmpString = (String)csvValues.elementAt(1);
					if ( _tmpString.compareTo(CoreMidlet._instance.getAppProperty("MIDlet-Version")) <= 0 ) {
						_alertUpdateOk.setString(l10n.tx("update_no_new_version"));
						_alertUpdateOk.removeCommand(_cmdWeb);
					}
					else {
						_tmpStringBuffer.setLength(0);
						_tmpStringBuffer.append(l10n.tx("update_new_version"));
						_tmpStringBuffer.append(_tmpString);
						_alertUpdateOk.setString(_tmpStringBuffer.toString());
						_alertUpdateOk.addCommand(_cmdDownload);
//						_alertUpdateOk.addCommand(_cmdViewHistory);
						
						// get url for update
						_updateUrl = (String)csvValues.elementAt(2);
					}					
					
					ret = true;					
					
				}
				//#mdebug
				else {
					System.out.println("Header not found ! - " + (String)csvValues.elementAt(0));
				}
				//#enddebug
			}
			
			//#mdebug
			System.out.println("Respone Code="  + hc.getResponseCode());
			//#enddebug
		}
		catch( IOException ioex ) {
			// ignore
		}
		catch( Exception ex ) {
			// ignore
		}
		finally {
			if ( hc != null ) {
				try { hc.close(); } catch (IOException ioex ) { /*ignore*/ } finally { hc = null; }
			}
			if ( dis != null ) {
				try { dis.close(); } catch (IOException ioex ) { /*ignore*/ } finally { dis = null; }
			}
		}
		
		return ret;
	}
	
	private void refreshItems() {
		
		// clean-up previous results
		try {
			for( int i = _itemsCount; i > 1; i-- )
				_frmSearch.delete(i);
		}
		catch(IndexOutOfBoundsException iobex) {
			//#mdebug
			iobex.printStackTrace();
			//#enddebug
		}
		
		// perform search & display
		StringItem _siFirst = null;
		boolean addAll = _lastSearch.length() == 0;
		boolean addItem = addAll;
		_itemsCount = 0;

		if ( ! addAll ) {
			for( Enumeration e = l10n.items.elements(); e.hasMoreElements(); ) {
	
				String[] elem = (String[])e.nextElement();
				
				// get e-number
				_tmpString2 = elem[l10n.ATTR_KEY];
				_tmpString = null;
				
//				if ( ! addAll ) {
					if ( _searchMode == Defs.SEARCH_MODE_NUMBER ) {
						addItem = _tmpString2.startsWith(_lastSearch, 1);
					}
					else if ( _searchMode == Defs.SEARCH_MODE_NAME ) {
						// get name
						_tmpString = elem[l10n.ATTR_NAME];				
						addItem = _tmpString.toLowerCase().startsWith(_lastSearch.toLowerCase());
					}
//				}
//				else {
//					addItem = true;
//				}
				
				if ( addItem ) {
					addItem = false;
					
					if ( _tmpString == null ) // get name
						_tmpString = elem[l10n.ATTR_NAME];
					
					_tmpStringBuffer.setLength(0);
					if ( _tmpString.length() > Defs.ADTV_MAX_NAME_SIZE ) {
						_tmpString = _tmpString.substring(0, Defs.ADTV_MAX_NAME_SIZE);
						_tmpStringBuffer.append(_tmpString);
						_tmpStringBuffer.append("...");
					}
					else {
						_tmpStringBuffer.append(_tmpString);
					}
					
					_si = new StringItem(_tmpString2, _tmpStringBuffer.toString(), Item.PLAIN);
					_si.setLayout(Defs.ITEMS_STYLE);
					_si.setDefaultCommand(Application._cmdOpen);
					_si.setItemCommandListener(this);
					_frmSearch.append( _si );
					_itemsCount++;
					
					if ( _siFirst == null )
						_siFirst = _si;
				}
			}
			
			// anything found !?
			if ( _itemsCount == 0 ) {
				if ( _iiSearchNotFound == null ) {
					_iiSearchNotFound = new ImageItem(null, _imgIcons[Defs.ICON_WARNINGS], ImageItem.LAYOUT_2 | ImageItem.LAYOUT_LEFT, null);
				}
				_frmSearch.append(_iiSearchNotFound);
				_itemsCount++;
				
				_si = new StringItem(null, l10n.tx("srchnf"), Item.PLAIN);
				//_si.setLayout(Defs.ITEMS_STYLE);
				_frmSearch.append( _si );
				_itemsCount++;
			}
			else if ( _siFirst != null ) {
				// select the first found (if any)
				Display.getDisplay(CoreMidlet._instance).setCurrentItem(_siFirst);
			}
		}
		else {
			if ( _iiSearchHint == null ) {
				_iiSearchHint = new ImageItem(null, _imgIcons[Defs.ICON_SEARCH], ImageItem.LAYOUT_2 | ImageItem.LAYOUT_LEFT, null);
			}
			_frmSearch.append(_iiSearchHint);
			_itemsCount++;
			
			_si = new StringItem(null, l10n.tx("srchint"), Item.PLAIN);
			//_si.setLayout(Defs.ITEMS_STYLE);
			_frmSearch.append( _si );
			_itemsCount++;		
		}
		
		_itemsCount++; // +1 for logo image item
	}
	
	private void loadOptions() {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(Defs.APP_RS_ID, true);
			if ( rs.getNumRecords() > 0 ) {
				byte[] data = rs.getRecord(1);
				
				Application._searchMode = (byte)data[0];
				switch(Application._searchMode) {
				case Defs.SEARCH_MODE_NAME:
					_chSearchType.setSelectedIndex(1, true);
					setSearchMode(Defs.SEARCH_MODE_NAME);
					break;
				case Defs.SEARCH_MODE_NUMBER:
					_chSearchType.setSelectedIndex(0, true);
					setSearchMode(Defs.SEARCH_MODE_NUMBER);
					break;
				}
				
			}
			
			//#mdebug
			System.out.println("Loading done.");
			//#enddebug			
		}
		catch(RecordStoreException ex) {
			//#mdebug
			ex.printStackTrace();
			//#enddebug
		}
		finally {
			if ( rs != null ) {
				try {
					rs.closeRecordStore();
				}
				catch( Exception e ) {
					//#mdebug
					e.printStackTrace();
					//#enddebug				
				}
			}
		}		
	}
	
	private void saveOptions() {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(Defs.APP_RS_ID, true);
			
			// delete if already existing
			if ( rs.getNumRecords() > 0 ) {
				rs.closeRecordStore();
				RecordStore.deleteRecordStore(Defs.APP_RS_ID);
				rs = RecordStore.openRecordStore(Defs.APP_RS_ID, true);
			}

			byte[] data = new byte[1];
			
			data[0] = (byte)Application._searchMode; //_chSearchType.getSelectedIndex();
			rs.addRecord(data, 0, data.length);
			
			//#mdebug
			System.out.println("Saving done.");
			//#enddebug
		}
		catch(RecordStoreException ex) {
			//#mdebug
			ex.printStackTrace();
			//#enddebug
		}
		finally {
			if ( rs != null ) {
				try {
					rs.closeRecordStore();
				}
				catch( Exception e ) {
					//#mdebug
					e.printStackTrace();
					//#enddebug				
				}
			}
		}
	}
	
	private void setSearchMode(int mode) {
		Application._searchMode = mode;
		
		switch( Application._searchMode ) {
		case Defs.SEARCH_MODE_NUMBER:
			_tSearch.setConstraints(TextField.DECIMAL);
			_tSearch.setMaxSize(Defs.SEARCH_MAX_SIZE_NUMBER);
			//_tSearch.removeCommand(_cmdSearchModeNumber);
			//_tSearch.addCommand(_cmdSearchModeName);
			//_tSearch.setItemCommandListener(this);
			break;
			
		case Defs.SEARCH_MODE_NAME:
			_tSearch.setConstraints(TextField.ANY);
			_tSearch.setMaxSize(Defs.SEARCH_MAX_SIZE);
			//_tSearch.removeCommand(_cmdSearchModeName);
			//_tSearch.addCommand(_cmdSearchModeNumber);
			//_tSearch.setItemCommandListener(this);
			_tSearch.setString("");
			break;
		}
	}
	
	private void setState(int state) {
		Application._state = state;
		
		switch( Application._state ) {
		case Defs.STATE_LOADING:
			
			_frmSplash = new Form(Defs.APP_NAME);
//			_splashTimeout = System.currentTimeMillis() + Defs.LOADING_TIMEOUT;
			
			_imgIcons = new Image[Defs.ICONS_COUNT];
			String[] imgNames = new String[]{"/ico_pill.png",
					"/ico_attach.png",
					"/ico_world.png",
					"/ico_error.png",
					"/ico_drink.png",
					"/ico_whatisit.png",
					"/ico_magnifier.png",
					"/ico_information.png",
					"/ico_tag_red.png"
					};
			
			try {
				// load icons
				for( int i = 0; i < Defs.ICONS_COUNT; i++ ) {
					_imgIcons[i] = Image.createImage(imgNames[i]);
				}
				
				// load logo/splash images
				_imgLogoBig = Image.createImage("/splash.png");
				_imgLogoSmall = Image.createImage("/logo_96x96.png");
				
				ImageItem ii = new ImageItem(null, _imgLogoBig, 
						ImageItem.LAYOUT_NEWLINE_AFTER | ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_2, 
						Defs.APP_NAME);				

				_frmSplash.append(ii);
				Display.getDisplay(CoreMidlet._instance).setCurrent(Application._frmSplash);
			} 
			catch (Exception e) {
				//#mdebug
				e.printStackTrace();
				//#enddebug
			}
			break;
			
//		case Defs.STATE_DISCLAIMER:
//			Display.getDisplay(CoreMidlet._instance).setCurrent(Application._frmDisclaimer);
//			break;
			
		case Defs.STATE_OPTIONS:
			Display.getDisplay(CoreMidlet._instance).setCurrent(Application._frmOptions);
			break;
			
		case Defs.STATE_SEARCH:
			Display.getDisplay(CoreMidlet._instance).setCurrentItem(_tSearch);
			break;
			
		case Defs.STATE_SEARCH_NOFOCUS:
			Display.getDisplay(CoreMidlet._instance).setCurrent(Application._frmSearch);
			break;
			
		case Defs.STATE_DETAILS:
			// NOTE: _tmpString - should already be assigned !!!
			
			_frmDetails.deleteAll();
			String[] data = l10n.itemData(_tmpString);
			
			if ( data[l10n.ATTR_NAME] != null && data[l10n.ATTR_NAME].length() > 0 ) {
				createFormStringItem(l10n.tx("additive"), _tmpString + " - " + data[l10n.ATTR_NAME], _frmDetails, Defs.ICON_NAME);
			}
			else {
				createFormStringItem(l10n.tx("code"), _tmpString, _frmDetails, Defs.ICON_NAME);
			}
			
			if ( data[l10n.ATTR_FUNCTION] != null && data[l10n.ATTR_FUNCTION].length() > 0 )
				createFormStringItem(l10n.tx("func"), data[l10n.ATTR_FUNCTION], _frmDetails, Defs.ICON_FUNCTION);

			if ( data[l10n.ATTR_STATUS] != null && data[l10n.ATTR_STATUS].length() > 0)
				createFormStringItem(l10n.tx("danger"), data[l10n.ATTR_STATUS], _frmDetails, Defs.ICON_STATUS);
			
			if ( data[l10n.ATTR_VEGETARIANS] != null && data[l10n.ATTR_VEGETARIANS].length() > 0)
				createFormStringItem(l10n.tx("animorg"), data[l10n.ATTR_VEGETARIANS], _frmDetails, Defs.ICON_ANIMALORIGIN);

			if ( data[l10n.ATTR_WARN] != null && data[l10n.ATTR_WARN].length() > 0)
				createFormStringItem(l10n.tx("warn"), data[l10n.ATTR_WARN], _frmDetails, Defs.ICON_WARNINGS);

			if ( data[l10n.ATTR_FOODTYPE] != null && data[l10n.ATTR_FOODTYPE].length() > 0)
				createFormStringItem(l10n.tx("foods"), data[l10n.ATTR_FOODTYPE], _frmDetails, Defs.ICON_FOUNDIN);

			if ( data[l10n.ATTR_INFO] != null && data[l10n.ATTR_INFO].length() > 0)
				createFormStringItem(l10n.tx("details"), data[l10n.ATTR_INFO], _frmDetails, Defs.ICON_WHATISIT);
			
			// display & focus
			Display.getDisplay(CoreMidlet._instance).setCurrentItem(_frmDetails.get(1));
			break;
			
		case Defs.STATE_SHOWGROUPS:
			Display.getDisplay(CoreMidlet._instance).setCurrent(_listGroups);
			break;
			
		case Defs.STATE_SHOWGROUP_INFO:
//			createFormStringItem("E100-E199", l10n.tx("colors"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E200-E299", l10n.tx("preservatives"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E300-E399", l10n.tx("antioxidants"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E400-E499", l10n.tx("stabilizers"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E500-E599", l10n.tx("regulators"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E600-E699", l10n.tx("enhancers"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E700-E799", l10n.tx("antibiotics"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E900-E999", l10n.tx("miscellaneous"), _frmGroups, Defs.ICON_GROUP);
//			createFormStringItem("E1000-E1199", l10n.tx("chemicals"), _frmGroups, Defs.ICON_GROUP);
			_frmGroups.deleteAll();
			
			switch( _listGroups.getSelectedIndex() ) {
			case 0: //E100-199
				createFormStringItem(l10n.tx("colors"), l10n.tx("colors_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 1: //E200-299
				createFormStringItem(l10n.tx("preservatives"), l10n.tx("preservatives_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 2: //E300-399
				createFormStringItem(l10n.tx("antioxidants"), l10n.tx("antioxidants_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 3: //E400-499
				createFormStringItem(l10n.tx("stabilizers"), l10n.tx("stabilizers_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 4: //E500-599
				createFormStringItem(l10n.tx("regulators"), l10n.tx("regulators_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 5: //E600-699
				createFormStringItem(l10n.tx("enhancers"), l10n.tx("enhancers_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 6: //E700-799
				createFormStringItem(l10n.tx("antibiotics"), l10n.tx("antibiotics_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 7: //E900-999
				createFormStringItem(l10n.tx("miscellaneous"), l10n.tx("miscellaneous_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			case 8: //E1000-1199
				createFormStringItem(l10n.tx("chemicals"), l10n.tx("miscellaneous_nfo"), _frmGroups, Defs.ICON_GROUP);
				break;
			}
			Display.getDisplay(CoreMidlet._instance).setCurrent(_frmGroups);
			break;
			
		case Defs.STATE_UPDATE:
			// run update action in separate thread
			_thread = new Thread(this);
			_thread.start();
			break;
			
		case Defs.STATE_UPDATE_OK:
			Display.getDisplay(CoreMidlet._instance).setCurrent(_alertUpdateOk);
			break;
			
		case Defs.STATE_UPDATE_ERROR:
			Display.getDisplay(CoreMidlet._instance).setCurrent(_alertUpdateError);
			break;
			
		case Defs.STATE_ABOUT:
			Display.getDisplay(CoreMidlet._instance).setCurrent(_frmAbout);
			break;
		}
	}
	
	public void deploy() {
		
		if ( _isPaused ) {
			//--- resume
			return;
		}
		else {
			
			if ( ! _isLoaded ) {
				l10n.load(); // load database
				init(); // create UI 
				loadOptions();
				refreshItems(); // reload items 
				_isLoaded = true;
				// show search form right away
				setState(Defs.STATE_SEARCH);
			}
			
			//--- start
			_thread = null;
		}
		
	}
	
	public void release() {
		// nothing to do
	}
	
	public void pause () {
		// nothing to do
	}
	
	public void run() {
		
		Thread.yield();
		
		if ( ! checkForUpdate(Defs.UPDATE_URL) ) {
			//#mdebug
			System.out.println("First Url failed, trying next one ...");
			//#enddebug
			if ( ! checkForUpdate(Defs.UPDATE_URL_FAILOVER) ) {
				//#mdebug
				System.out.println("Error getting update !");
				//#enddebug
				setState(Defs.STATE_UPDATE_ERROR);
				return;
			}
		}
		setState(Defs.STATE_UPDATE_OK);	
	}

//	public void run() {
//		// main loop
//		while( _isRunning ) {
//			Thread.yield();
//			
//			if ( _isPaused )
//				continue;
//			
//			// check loading state ...
//			if ( _state == Defs.STATE_LOADING ) {
//				if ( System.currentTimeMillis() > _splashTimeout && _isLoaded ) {
//					setState(Defs.STATE_SEARCH);
//				}
//				continue;
//			}
//			
//			// check for update
//			if ( _state == Defs.STATE_UPDATE ) {
//				if ( ! checkForUpdate(Defs.UPDATE_URL) ) {
//					//#debug
//					System.out.println("First Url failed, trying next one ...");
//					//#enddebug
//					if ( ! checkForUpdate(Defs.UPDATE_URL_FAILOVER) ) {
//						//#debug
//						System.out.println("Error getting update !");
//						//#enddebug
//						setState(Defs.STATE_UPDATE_ERROR);
//						continue;
//					}
//				}
//				setState(Defs.STATE_UPDATE_OK);
//			}
//			
//			// update search items
//			if ( _state == Defs.STATE_SEARCH ) {
////				if ( Application._lastSearchStartTime < System.currentTimeMillis() ) {
////					if ( ! Application._lastSearch.equals(Application._tSearch.getString()) ) {
////						Application._lastSearch = Application._tSearch.getString();
////						CoreMidlet.app.refreshItems();
////					}
////					Application._lastSearchStartTime = System.currentTimeMillis() + Defs.REFRESH_TIMEOUT;
////				}
//				if ( ! Application._lastSearch.equals(Application._tSearch.getString()) ) {
//					Application._lastSearchStartTime = System.currentTimeMillis() + Defs.REFRESH_TIMEOUT;
//					Application._lastSearch = Application._tSearch.getString();
//				}
//				else if ( Application._lastSearchStartTime != -1 && Application._lastSearchStartTime < System.currentTimeMillis() ) {
//					CoreMidlet.app.refreshItems();
//					Application._lastSearchStartTime = -1;
//				}				
//			}
//			
////			try {
////				Thread.sleep(Defs.WORKERTHREAD_TIMEOUT);
////			}
////			catch(InterruptedException iex) {
////				//#debug
////				System.err.println(iex.toString());
////				//#enddebug
////			}
//		}
//	}
	
	public void commandAction(Command c, Displayable d) {
		
		if ( c == _cmdQuit ) {
			try {
				CoreMidlet._instance.destroyApp(true);
			}
			catch (MIDletStateChangeException ex) {
				//#mdebug
				System.err.println(ex.toString());
				//#enddebug
			}
		}
		else if ( c == _cmdBack ) {
			if ( _state == Defs.STATE_OPTIONS ) {
				// set & save selected search mode
				switch(_chSearchType.getSelectedIndex()) {
				case 0:
					setSearchMode(Defs.SEARCH_MODE_NUMBER);
					break;
				case 1:
					setSearchMode(Defs.SEARCH_MODE_NAME);
					break;
				}
				saveOptions();
				setState(Defs.STATE_SEARCH);
			}
			else if ( _state == Defs.STATE_DETAILS ) {
				setState(Defs.STATE_SEARCH_NOFOCUS);
			}
			else if ( _state == Defs.STATE_ABOUT 
					|| _state == Defs.STATE_UPDATE_OK 
					|| _state == Defs.STATE_SHOWGROUPS 
					|| _state == Defs.STATE_UPDATE_OK 
					|| _state == Defs.STATE_UPDATE_ERROR )
			{
				setState(Defs.STATE_SEARCH);
			}
			else if ( _state == Defs.STATE_SHOWGROUP_INFO ) {
				setState(Defs.STATE_SHOWGROUPS);
			}
		}
		else if ( c == _cmdUpdate ) {
			setState(Defs.STATE_UPDATE);
		}
		else if ( c == _cmdAbout ) {
			setState(Defs.STATE_ABOUT);
		}
		else if ( c == _cmdGroups ) {
			setState(Defs.STATE_SHOWGROUPS);
		}
		else if ( c == _cmdOptions ) {
			setState(Defs.STATE_OPTIONS);
		}
		else if ( c == _cmdWeb ) {
			try {
				CoreMidlet._instance.platformRequest(Defs.WEB_URL);
			}
			catch( ConnectionNotFoundException ex ) {}
		}
		else if ( c == _cmdDownload ) {
			try {
				CoreMidlet._instance.platformRequest(_updateUrl);
			}
			catch( ConnectionNotFoundException ex ) {}				
		}
		else if ( c == _cmdViewHistory ) {
			try {
				CoreMidlet._instance.platformRequest(Defs.HISTORY_URL);
			}
			catch( ConnectionNotFoundException ex ) {}				
		}
		else if ( c == _cmdOpen ) {
			setState(Defs.STATE_SHOWGROUP_INFO);
		}
//		else if ( c == _cmdSearch ) {
//			_lastSearch = _tSearch.getString();
//			refreshItems();
//		}
	}
	
	public void commandAction(Command c, Item item) {
		if ( c == Application._cmdOpen ) {
			_tmpString = ((StringItem)item).getLabel(); // sync op !!!
			setState(Defs.STATE_DETAILS);
		}
		else if ( c == Application._cmdSearchItem ) {
			_lastSearch = _tSearch.getString();
			refreshItems();
		}
	}

}
