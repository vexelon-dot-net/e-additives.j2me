/*
 * File: CoreMidlet.java
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

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class CoreMidlet extends MIDlet {
	
	public static CoreMidlet _instance;
	public static Application app;

	public CoreMidlet() {
		CoreMidlet._instance = this;
		app = new Application();
	}

	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
		app.release();
		app = null;
		System.gc();
		this.notifyDestroyed();		
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		app.deploy();
	}

}
