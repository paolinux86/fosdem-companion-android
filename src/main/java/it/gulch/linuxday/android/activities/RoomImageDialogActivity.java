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
package it.gulch.linuxday.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * A special Activity which is displayed like a dialog and shows a room image. Specify the room name and the room image id as Intent extras.
 *
 * @author Christophe Beyls
 */
public class RoomImageDialogActivity extends Activity
{
	public static final String EXTRA_ROOM_NAME = "roomName";

	public static final String EXTRA_ROOM_IMAGE_RESOURCE_ID = "imageResId";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		setTitle(intent.getStringExtra(EXTRA_ROOM_NAME));

		ImageView imageView = new ImageView(this);
		imageView.setImageResource(intent.getIntExtra(EXTRA_ROOM_IMAGE_RESOURCE_ID, 0));
		setContentView(imageView);
	}
}
