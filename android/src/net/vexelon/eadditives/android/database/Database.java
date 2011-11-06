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

import java.util.List;

public interface Database {
	
	// R/W data from and back to the database
	public void synchronize() throws Exception;
	
	//public void reload() throws Exception;
	
	// get the last time ticks when the database has been synchronized
	public long getLastSyncTime();
	
	// get the amount of additives currently stored in the database
	public int getCount();
	
	// quick check if the database is empty
	public boolean isEmpty();
	
//	// clear all loaded items
//	public void clear();
	
	// find and retrieve additive by it's code
	public Additive getAdditive(String code);

	// get a list of all available additives
	public List<Additive> getAllAdditives();
	
}
