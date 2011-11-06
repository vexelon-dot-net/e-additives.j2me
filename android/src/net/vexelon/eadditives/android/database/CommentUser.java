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

public class CommentUser {
	
	private String _username;
	private String _email;
	
	public CommentUser(String username, String email) {
		this._username = username;
		this._email = email;
	}
	
	public String getUsername() {
		return _username;
	}
	
	public String getEmail() {
		return _email;
	}

}