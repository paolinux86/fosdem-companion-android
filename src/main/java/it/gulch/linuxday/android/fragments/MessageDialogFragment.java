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
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * A generic class to display a simple message in a dialog box.
 *
 * @author Christophe Beyls
 */
public class MessageDialogFragment extends DialogFragment
{
	public static MessageDialogFragment newInstance(@StringRes int titleResId, @StringRes int messageResId)
	{
		MessageDialogFragment f = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putInt("titleResId", titleResId);
		args.putInt("messageResId", messageResId);
		f.setArguments(args);
		return f;
	}

	public static MessageDialogFragment newInstance(@StringRes int titleResId, CharSequence message)
	{
		MessageDialogFragment f = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putInt("titleResId", titleResId);
		args.putCharSequence("message", message);
		f.setArguments(args);
		return f;
	}

	public static MessageDialogFragment newInstance(CharSequence title, CharSequence message)
	{
		MessageDialogFragment f = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putCharSequence("title", title);
		args.putCharSequence("message", message);
		f.setArguments(args);
		return f;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Bundle args = getArguments();
		int titleResId = args.getInt("titleResId", -1);
		CharSequence title = (titleResId != -1) ? getText(titleResId) : args.getCharSequence("title");
		int messageResId = args.getInt("messageResId", -1);
		CharSequence message = (messageResId != -1) ? getText(messageResId) : args.getCharSequence("message");

		return new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message)
			.setPositiveButton(android.R.string.ok, null).create();
	}

	public void show(FragmentManager manager)
	{
		show(manager, "message");
	}
}
