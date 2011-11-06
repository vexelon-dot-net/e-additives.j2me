/*    
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
package net.vexelon.eadditives.android;

import net.vexelon.eadditives.android.common.Defs;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TabWidgetActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent = null;
        
        // create browse activity
        intent = new Intent(this, BrowseActivity.class);
        spec = tabHost.newTabSpec(Defs.TAB_NAME_BROWSE).setIndicator("Browse",
        		res.getDrawable(R.drawable.magnifier)).setContent(intent);
        tabHost.addTab(spec);
        
        // create favorite activity
        intent = new Intent(this, FavouritesActivity.class);
        spec = tabHost.newTabSpec(Defs.TAB_NAME_FAVS).setIndicator("Favourites",
        		res.getDrawable(R.drawable.heart)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTabByTag(Defs.TAB_NAME_BROWSE);
    }
}