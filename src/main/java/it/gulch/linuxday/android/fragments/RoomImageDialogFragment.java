/*
 * Copyright 2014 Christophe Beyls
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.gulch.linuxday.android.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import it.gulch.linuxday.android.R;

public class RoomImageDialogFragment extends DialogFragment
{
	public static final String TAG = "room";

	public static RoomImageDialogFragment newInstance(String roomName, int imageResId)
	{
		RoomImageDialogFragment f = new RoomImageDialogFragment();
		Bundle args = new Bundle();
		args.putString("roomName", roomName);
		args.putInt("imageResId", imageResId);
		f.setArguments(args);
		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Bundle args = getArguments();

		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(args.getInt("imageResId"));

		Dialog dialog =
			new AlertDialog.Builder(getActivity()).setTitle(args.getString("roomName")).setView(imageView).create();
		dialog.getWindow().getAttributes().windowAnimations = R.style.RoomImageDialogAnimations;
		return dialog;
	}

	public void show(FragmentManager manager)
	{
		show(manager, TAG);
	}
}
