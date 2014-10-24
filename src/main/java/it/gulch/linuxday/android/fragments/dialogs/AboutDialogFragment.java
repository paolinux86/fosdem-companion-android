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
package it.gulch.linuxday.android.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import it.gulch.linuxday.android.BuildConfig;
import it.gulch.linuxday.android.R;

public class AboutDialogFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Context context = getActivity();
		String versionName = BuildConfig.VERSION_NAME;
		String title = String.format("%1$s %2$s", getString(R.string.app_name), versionName);

		return new AlertDialog.Builder(context).setTitle(title).setIcon(R.drawable.ic_launcher)
				.setMessage(getResources().getText(R.string.about_text)).setPositiveButton(android.R.string.ok, null)
				.create();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// Make links clickable; must be called after the dialog is shown
		((TextView) getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance
				());
	}
}
