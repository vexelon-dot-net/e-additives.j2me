/*
 * File: l10n.java
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class l10n {
	
	private final static String TAG_STRINGS = "strings";
	private final static String TAG_ITEMS = "items";
	private final static String TAG_ITEM = "item";
	private final static String TAG_DATA = "data";
	private final static String ATTRIB_FUNCTION = "function";
	private final static String ATTRIB_FOOD = "food";
	private final static String ATTRIB_SIDEFX = "warn";
	private final static String ATTRIB_INFO = "info";
	
	public final static int ATTR_COUNT = 8;
	public final static int ATTR_NAME = 0;
	public final static int ATTR_STATUS = 1;
	public final static int ATTR_FUNCTION = 2;
	public final static int ATTR_FOODTYPE = 3;
	public final static int ATTR_WARN = 4;
	public final static int ATTR_INFO = 5;
	public final static int ATTR_KEY = 6;
	public final static int ATTR_VEGETARIANS = 7;
	
	private static KXmlParser parser = new KXmlParser();
	private static Hashtable strings = new Hashtable();
	public static Vector items = null;
	
	protected l10n() {
		// no instances
	}

	public static void load() {
		try {
			//#mdebug
			System.out.println("[dbg] Loading Xml ...");
			//#enddebug
			l10n.readCache();
		}
		catch( XmlPullParserException xppex ) {
			//#mdebug
			System.err.println(xppex.toString());
			//#enddebug
		}
		catch( IOException ioe ) {
			//#mdebug		
			System.err.println(ioe.toString());
			//#enddebug
		}
	}
	
	public static String tx(String key) {
		return (String)strings.get(key);
	}
	
	public static String[] itemData(String key) {
		for( Enumeration e = l10n.items.elements(); e.hasMoreElements(); ) {
			String[] item = (String[])e.nextElement();
			if ( item[ATTR_KEY].equals(key) ) {
				return item;
			}
		}
		return null;
	}
	
	public static int getItemsCount() {
		return l10n.items.size();
	}
	
	private static void readCache() throws XmlPullParserException, IOException {

		//#ifdef LANG_BG
		parser.setInput( "".getClass().getResourceAsStream("/l10n/bg.xml"), null );
		//#else
//@		parser.setInput( "".getClass().getResourceAsStream("/l10n/en.xml"), null );
		//#endif
		
		//parser.require( KXmlParser.START_TAG, "", null );
		boolean startItems = false, startApp = false;
		String[] itemData = null;
		items = new Vector(200); //TODO: get items count from XML !
		
		while ( parser.next() != XmlPullParser.END_DOCUMENT ) {
			if ( parser.getEventType() == KXmlParser.START_TAG ) {
				//--- DATA
				if ( parser.getName().equals(TAG_ITEMS) ) {
					startItems = true; // ok, we now start parsing data contents
					continue;
				}
				else if ( parser.getName().equals(TAG_STRINGS) ) {
					startApp = true;
					continue;
				}
				
				if ( startApp ) {
					if ( parser.getName().equals(TAG_ITEM) && parser.getAttributeValue(0) != null ) {
						//#debug
						System.out.println("parsed String=" + parser.getAttributeValue(0) );
						//#enddebug
						strings.put(parser.getAttributeValue(0),  parser.nextText());
					}
				}
				
				if ( startItems ) {
					if ( parser.getName().equals(TAG_ITEM) ) {
						itemData = new String[ATTR_COUNT];
						itemData[ATTR_KEY] = parser.getAttributeValue(0);
						itemData[ATTR_NAME] = parser.getAttributeValue(1); // name
						itemData[ATTR_STATUS] = parser.getAttributeValue(2); // danger level
						itemData[ATTR_VEGETARIANS] = parser.getAttributeValue(3); // vegetarians info
					}
					else if ( parser.getName().equals(TAG_DATA) ) {
						if ( parser.getAttributeValue(0).equals(ATTRIB_FUNCTION) ) {
							itemData[ATTR_FUNCTION] = parser.nextText(); // function
						}
						else if ( parser.getAttributeValue(0).equals(ATTRIB_FOOD) ) {
							itemData[ATTR_FOODTYPE] = parser.nextText(); // food types
						}
						else if ( parser.getAttributeValue(0).equals(ATTRIB_SIDEFX) ) {
							itemData[ATTR_WARN] = parser.nextText(); // side effects
						}
						else if ( parser.getAttributeValue(0).equals(ATTRIB_INFO) ) {
							itemData[ATTR_INFO] = parser.nextText(); // info/details
						}
					}
				}
			}
			else if ( parser.getEventType() == KXmlParser.END_TAG ) {
				if ( parser.getName().equals(TAG_ITEMS) ) { 
					startItems = false; // end of data contents parsing
				}
				else if ( parser.getName().equals(TAG_ITEM) && startItems ) {
					
					//#debug
					System.out.println("parsed Item=" + itemData[ATTR_KEY]);
					//#enddebug
					
					l10n.items.addElement(itemData);
					itemData = null;
				}
				else if ( parser.getName().equals(TAG_STRINGS) ) {
					startApp = false; // end of app lang items
				}
			}
		}
	}

}
