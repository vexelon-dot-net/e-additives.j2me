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

import java.io.InputStream;

import net.vexelon.eadditives.android.common.Defs;
import net.vexelon.eadditives.android.database.Database;
import net.vexelon.eadditives.android.database.XmlDatabase;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BrowseActivity extends Activity {
	
	private Database _database = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        
        loadAdditives();
		
		final Activity activity = this;
		
		ListView view = (ListView) findViewById(R.id.ListViewBrowse);
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				Intent intent = new Intent(activity, ShowItemActivity.class);
				startActivity(intent);
			}
		});
        
    }
    
    private void loadAdditives() {
    	
		InputStream is = this.getResources().openRawResource(R.raw.en);
		
		try {
			_database = new XmlDatabase(is);
			
	        ListView view = (ListView) findViewById(R.id.ListViewBrowse);
	        
	        AdditivesListAdapter adapter = new AdditivesListAdapter(this, android.R.layout.simple_list_item_1, 
	        		_database.getAllAdditives());
			view.setAdapter(adapter);			
		}
		catch(Exception e) {
			Log.e(Defs.LOG_TAG, "Error loading adds: " + e.toString() );
		}
		
		Log.i(Defs.LOG_TAG, "Loaded items - " + _database.getAllAdditives().size());
		 
    }
}