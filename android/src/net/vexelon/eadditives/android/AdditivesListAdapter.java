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

import java.util.ArrayList;
import java.util.List;

import net.vexelon.eadditives.android.database.Additive;
import net.vexelon.eadditives.android.database.Additive.Attributes;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdditivesListAdapter extends ArrayAdapter<Additive> {
	
	private List<Additive> _items = null;
	
	public AdditivesListAdapter(Context context, int textViewResId, List<Additive> items) {
		super(context, textViewResId, items);
		_items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if ( v == null ) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_row_layout, null);
		}
		
		Additive adt = _items.get(position);
		if (adt != null) {
			setResText(v, R.id.codeText, adt.getAttribte(Attributes.CODE));
			setResText(v, R.id.nameText, adt.getAttribte(Attributes.NAME));
		}
		
		return v;
	}

	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView)v.findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}	
	
}
