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
package it.gulch.linuxday.android.model;

import android.text.TextUtils;

public enum Building
{
	J, K, H, U, AW, Unknown;

	public static Building fromRoomName(String roomName)
	{
		if(!TextUtils.isEmpty(roomName)) {
			switch(Character.toUpperCase(roomName.charAt(0))) {
				case 'K':
					return K;
				case 'H':
					return H;
				case 'U':
					return U;
			}
			if(roomName.regionMatches(true, 0, "AW", 0, 1)) {
				return AW;
			}
			if("Janson".equalsIgnoreCase(roomName)) {
				return J;
			}
			if("Ferrer".equalsIgnoreCase(roomName)) {
				return H;
			}
			if("Chavanne".equalsIgnoreCase(roomName) || "Lameere".equalsIgnoreCase(roomName) ||
				"Guillissen".equalsIgnoreCase(roomName)) {
				return U;
			}
		}

		return Unknown;
	}
}
