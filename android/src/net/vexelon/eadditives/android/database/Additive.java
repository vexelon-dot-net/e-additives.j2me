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

import java.util.HashMap;

public class Additive implements Concept {
	
	public enum Attributes {
		CODE,
		NAME,
		STATUS,
		VEGETARIAN_SAFE,
		FUNCTION,
		FOUNDIN,
		WARNINGS,
		INFO
	}
	
	private Concept _parent = null;
	
	private HashMap<Attributes, String> _values = null;
	
	private Comments _comments = null;
	
	public Additive(Concept parent, String code) {
		this._parent = parent;
		this._values = new HashMap<Attributes, String>(10);
		this.setAttribute(Attributes.CODE, code);
	}
	
	public void setAttribute(Attributes key, String value) {
		_values.put(key, value);
	}
	
	public String getAttribte(Attributes key) {
		return _values.get(key);
	}
	
	public void setComments(Comments c) {
		this._comments = c;
	}
	
	public Comments getComments() {
		return this._comments;
	}
	
	@Override
	public void ping(Object o) {
		if ( o instanceof Comments ) {
			//TODO:
		
			_parent.ping(this);
		}
	}
	
}
