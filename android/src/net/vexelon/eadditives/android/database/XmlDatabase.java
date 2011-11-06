/*
 * Copyright 2010 Petar Petrov
 *     
 * This file is part of E-additives.
 * 
 * E-additives is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * E-additives is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with E-additives.  If not, see <http://www.gnu.org/licenses/>.
 * 
*/
package net.vexelon.eadditives.android.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.vexelon.eadditives.android.database.Additive.Attributes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlDatabase implements Database, Concept {
	
//	private final static String TAG_STRINGS = "strings";
	private final static String TAG_ITEMS = "items";
	private final static String TAG_ITEM = "item";
	private final static String TAG_DATA = "data";
	
	private final static String ATTRIB_KEY = "key";
	private final static String ATTRIB_NAME = "name";
	private final static String ATTRIB_STATUS = "status";
	private final static String ATTRIB_VEGETARIAN = "veg";
	private final static String ATTRIB_FUNCTION = "function";
	private final static String ATTRIB_FOOD = "food";
	private final static String ATTRIB_WARN = "warn";
	private final static String ATTRIB_INFO = "info";
	
	private long _lastUpdateTicks = -1;
	
	private HashMap<String, Additive> _additives = null;
	
	private ArrayList<Additive> _cacheList = null;
	
	private InputStream _inputStream = null;
	
	public XmlDatabase(InputStream is) throws Exception {
		_inputStream = is;
		synchronize();
	}
	
	private void parse(InputStream is) throws Exception {
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(is, null);
			
			int eventType = xpp.getEventType();
			boolean inItems = false;
			Additive curAdditive = null;
			
			while( eventType != XmlPullParser.END_DOCUMENT ) {
				
				String tagName = null;
				
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					
					break;
				
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					
					if (tagName.equals(TAG_ITEMS)) {

						// make place for items list
						if (_additives == null) {
							_additives = new HashMap<String, Additive>(1000);
						}
						
						inItems = true;
					}
					else if (inItems) {
						// check at items
						
						if (tagName.equals(TAG_ITEM)) {
							// new item
							
							curAdditive = new Additive(this, 
									xpp.getAttributeValue(null, ATTRIB_KEY));
							
							curAdditive.setAttribute(Attributes.NAME, xpp.getAttributeValue(null, ATTRIB_NAME));
							curAdditive.setAttribute(Attributes.STATUS, xpp.getAttributeValue(null, ATTRIB_STATUS));
							curAdditive.setAttribute(Attributes.VEGETARIAN_SAFE, xpp.getAttributeValue(null, ATTRIB_VEGETARIAN));
						}
						else if (tagName.equals(TAG_DATA)) {
							// parse current item data
							
							curAdditive.setAttribute(Attributes.FUNCTION, xpp.getAttributeValue(null, ATTRIB_FUNCTION));
							curAdditive.setAttribute(Attributes.FOUNDIN, xpp.getAttributeValue(null, ATTRIB_FOOD));
							curAdditive.setAttribute(Attributes.WARNINGS, xpp.getAttributeValue(null, ATTRIB_WARN));
							curAdditive.setAttribute(Attributes.INFO, xpp.getAttributeValue(null, ATTRIB_INFO));
						}
					}
					
					break;
				
				case XmlPullParser.END_TAG:
					tagName = xpp.getName();
					
					if (tagName.equals(TAG_ITEMS)) {
						inItems = false;
					}
					else if (tagName.equals(TAG_ITEM)) {
						// finished parsing item
						
						_additives.put(curAdditive.getAttribte(Attributes.CODE), curAdditive);
					}
				}
				
				// advance to next tag
				eventType = xpp.next();
			}
		}
//		catch(Exception e) {
//			//Log.e(TAG, "Error while parsing XML !", e);
//		}
		finally {
			try { 
				if ( is != null ) is.close(); 
			} catch (IOException e) { }
		}
		
//		return ret;
	}
	
	@Override
	public int getCount() {
		return _additives.size();
	}
	
	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public long getLastSyncTime() {
		return _lastUpdateTicks;
	}
	
	@Override
	public void synchronize() throws Exception {
		
		// clear currently loaded items
		if (this._additives != null) {
			_additives.clear();
			_additives = null;
		}
		if (this._cacheList != null) {
			_cacheList.clear();
			_cacheList = null;
		}
		
		parse(_inputStream);
		
		_lastUpdateTicks = System.currentTimeMillis();
	}
	
	@Override
	public Additive getAdditive(String code) {
		return _additives.get(code);
	}
	
	@Override
	public List<Additive> getAllAdditives() {
		if (_cacheList == null) {
			_cacheList = new ArrayList(_additives.values());
		}
		return _cacheList;
	}

	@Override
	public void ping(Object o) {
		if (o instanceof Additive) {
			//TODO
		}
	}
	
}
