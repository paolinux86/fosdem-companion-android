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
package it.gulch.linuxday.android.utils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;

/**
 * NFC helper methods for receiving data sent by NfcSenderUtils. This class wraps API 10+ code.
 *
 * @author Christophe Beyls
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
class NfcReceiverUtils
{
	public static boolean hasAppData(Intent intent)
	{
		return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
	}

	public static byte[] extractAppData(Intent intent)
	{
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		return msg.getRecords()[0].getPayload();
	}
}
