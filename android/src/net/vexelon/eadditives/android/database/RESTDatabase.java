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

public class RESTDatabase implements Database, Concept {

	@Override
	public Additive getAdditive(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Additive> getAllAdditives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastSyncTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void synchronize() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void ping(Object o) {
		// TODO Auto-generated method stub

	}

}
