/*
 * File: Defs.java
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Item;

public class Defs {
	
	final static String APP_NAME = "E-additives";
	final static String APP_RS_ID = "eadditivesrs2010";
	final static String APP_BUILD_DATE = "__BUILD_DATE__";
	final static long LOADING_TIMEOUT = 2500L;
	final static long REFRESH_TIMEOUT = 900L;
	final static long WORKERTHREAD_TIMEOUT = 100L;
	final static int ADTV_MAX_NAME_SIZE = 18;
	final static String WEB_URL = "http://e-additives.vexelon.net";
	final static String WEB_DEFAULT_DOWNLOAD_URL = "http://e-additives.vexelon.net/?page_id=55";
	//#ifdef LANG_BG
	final static String UPDATE_URL = "http://e-additives.vexelon.net/downloads/bg/VERSION";
	final static String UPDATE_URL_FAILOVER = "http://e-additives.vexelon.net/downloads/bg/VERSION";
	final static String UPDATE_HEADER = "EADBG";
	//#else
//@	final static String UPDATE_URL = "http://e-additives.vexelon.net/downloads/en/VERSION";
//@	final static String UPDATE_URL_FAILOVER = "http://e-additives.vexelon.net/downloads/en/VERSION";
//@	final static String UPDATE_HEADER = "EADEN";
	//#endif
	final static String HISTORY_URL = "http://e-additives.vexelon.net/downloads/HISTORY";
	
	final static int STATE_LOADING = 0;
//	final static int STATE_DISCLAIMER = 5;
	final static int STATE_SEARCH = 10;
	final static int STATE_SEARCH_NOFOCUS = 11;
	final static int STATE_OPTIONS = 12;
	final static int STATE_DETAILS = 20;
	final static int STATE_SHOWGROUPS = 30;
	final static int STATE_SHOWGROUP_INFO = 32;
	final static int STATE_UPDATE = 40;
	final static int STATE_UPDATE_OK = 42;
	final static int STATE_UPDATE_ERROR = 48;
	final static int STATE_ABOUT = 50;
	
	final static int SEARCH_MAX_SIZE = 20;
	final static int SEARCH_MAX_SIZE_NUMBER = 5;
	final static int SEARCH_MODE_NUMBER = 1;
	final static int SEARCH_MODE_NAME = 2;
	final static int ABOUT_STYLE = Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_CENTER;
    final static int DETAILS_STYLE = Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_EXPAND;
    final static int DETAILS_STYLE2 = Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_EXPAND;
    final static Font DETAILS_TITLE_FONT = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD | Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM);
    final static Font DETAILS_ITEM_FONT = Font.getFont(Font.FACE_SYSTEM);
    final static Font FONT_BIG = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_LARGE);
    final static Font FONT_MEDIUM = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    final static Font FONT_SMALL = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL);
    final static int ITEMS_STYLE = Item.LAYOUT_2 | Item.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_EXPAND;
    
    final static int ICONS_COUNT = 9;
    final static int ICON_NAME = 0;
    final static int ICON_FUNCTION = 1;
    final static int ICON_STATUS = 2;
    final static int ICON_WARNINGS = 3;
    final static int ICON_FOUNDIN = 4;
    final static int ICON_WHATISIT = 5;
    final static int ICON_SEARCH = 6;
    final static int ICON_GROUP = 7;
    final static int ICON_ANIMALORIGIN = 8;
    
}
