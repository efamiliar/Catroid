/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.adapter;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.UtilFile;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LookAdapter extends ArrayAdapter<LookData> {

	protected ArrayList<LookData> lookDataItems;
	protected Context context;

	private OnLookEditListener onLookEditListener;

	public LookAdapter(final Context context, int textViewResourceId, ArrayList<LookData> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		lookDataItems = items;
	}

	public void setOnLookEditListener(OnLookEditListener listener) {
		onLookEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.fragment_look_looklist_item, null);
		}

		convertView.findViewById(R.id.btn_look_copy).setTag(position);
		convertView.findViewById(R.id.btn_look_delete).setTag(position);
		convertView.findViewById(R.id.btn_look_edit).setTag(position);
		convertView.findViewById(R.id.look_name).setTag(position);
		convertView.findViewById(R.id.look_image).setTag(position);

		LookData lookData = lookDataItems.get(position);

		if (lookData != null) {
			ImageView lookImage = (ImageView) convertView.findViewById(R.id.look_image);
			TextView lookNameTextField = (TextView) convertView.findViewById(R.id.look_name);
			TextView lookResolution = (TextView) convertView.findViewById(R.id.look_res);
			TextView lookSize = (TextView) convertView.findViewById(R.id.look_size);
			Button lookEditButton = (Button) convertView.findViewById(R.id.btn_look_edit);
			Button lookCopyButton = (Button) convertView.findViewById(R.id.btn_look_copy);
			Button lookDeleteButton = (Button) convertView.findViewById(R.id.btn_look_delete);

			lookImage.setImageBitmap(lookData.getThumbnailBitmap());
			lookNameTextField.setText(lookData.getLookName());

			//setting resolution and look size:
			{
				int[] resolution = lookData.getResolution();
				lookResolution.setText(resolution[0] + " x " + resolution[1]);

				//setting size
				if (lookData.getAbsolutePath() != null) {
					lookSize.setText(UtilFile.getSizeAsString(new File(lookData.getAbsolutePath())));
				}
			}

			lookImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookEdit(v);
					}
				}
			});

			lookNameTextField.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookRename(v);
					}
				}
			});

			lookEditButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookEdit(v);
					}
				}
			});

			lookCopyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookCopy(v);
					}
				}
			});

			lookDeleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookDelete(v);
					}
				}
			});
		}

		return convertView;
	}

	public interface OnLookEditListener {

		public void onLookEdit(View v);

		public void onLookRename(View v);

		public void onLookDelete(View v);

		public void onLookCopy(View v);
	}
}
