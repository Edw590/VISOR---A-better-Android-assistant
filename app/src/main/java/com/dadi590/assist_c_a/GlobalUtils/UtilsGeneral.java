/*
 * Copyright 2023 DADi590
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.dadi590.assist_c_a.GlobalUtils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dadi590.assist_c_a.ApplicationClass;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * <p>Global app-related utilities.</p>
 */
public final class UtilsGeneral {

	/**
	 * <p>Private empty constructor so the class can't be instantiated (utility class).</p>
	 */
	private UtilsGeneral() {
	}

	private static final String[] hashes_APU_lib_files = {
			// All SHA-512
			"BF3EFA298679FEAC228A3AA3EB3D1ED75F433C1B3CEA09734B41D381FAA3590B0E5506AC1F1464F73E0039F8DD1F923944758EB59173A1B8E52151E9CFB54CE3", // x86_64
			"BBEEC2B13E697BE92E96E0DDCAD4BCE9FF818CA379B697E42478F29FFFD62245A97DEE7A2D0AA6545BC095D4FED52E98EAB483C2AFDA9554E4327A9DAF494680", // x86
			"75FE7F3A6490767C013D1EBD77048ECBC3E38A21834C89C4EEE904EBD14A3EECB7547CBA93DF356154F77F50D9F2E5F54090A487D074825BB0AA55F84540129A", // armeabi-v7a
			"8BBF14C19BD72EAF5F9E8E48780F3D1778323B60BA5C95455FFB2935E1AA87533D4B8862F01E3A445EADD2EFBFC13FD9DCCA18D2001674098EA30DD948DCD374", // arm64-v8a
	};

	/**
	 * <p>Checks if the Assistant Platforms Unifier module is available.</p>
	 * <p>It does so by checking if the APU library file is available on the device.</p>
	 *
	 * @return true if it is available for use (APU library file on the device and on a correct folder), false otherwise
	 */
	public static boolean isAPUAvailable() {
		if (!UtilsNativeLibs.isPrimaryNativeLibAvailable(UtilsNativeLibs.ACD_LIB_NAME)) {
			return false;
		}

		return UtilsCryptoHashing.fileMatchesHash(UtilsNativeLibs.getPrimaryNativeLibsPath() + "/" +
				UtilsNativeLibs.ACD_LIB_NAME, hashes_APU_lib_files, UtilsCryptoHashing.IDX_SHA512);
	}

	/**
	 * <p>Generates a random string with the given length, containing only ASCII letters (upper case or lower case) and
	 * numbers.</p>
	 *
	 * @param length length of the generating string
	 *
	 * @return the generated string
	 */
	@NonNull
	public static String generateRandomString(final int length) {
		final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String lower = upper.toLowerCase(Locale.ROOT);
		final String digits = "0123456789";
		final String alphanum = upper + lower + digits;

		final Random random = new SecureRandom();
		final char[] symbols = alphanum.toCharArray();
		final char[] buf;

		if (length < 1) throw new IllegalArgumentException("Length 0 string requested");
		buf = new char[length];

		for (int idx = 0; idx < length; ++idx) {
			buf[idx] = symbols[random.nextInt(symbols.length)];
		}
		return new String(buf);
	}

	/**
	 * <p>Check if an accessory with speakers (like earphones, headphones, headsets...) are connected.</p>
	 *
	 * @return true if an accessory with speakers is connected, false otherwise
	 */
	public static boolean areExtSpeakersOn() {
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (null == audioManager) {
			return false;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			final AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

			for (final AudioDeviceInfo device : devices) {
				if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
						|| device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
						|| device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
						|| device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
						|| device.getType() == AudioDeviceInfo.TYPE_USB_HEADSET) {
					return true;
				}
			}
		} else {
			return audioManager.isWiredHeadsetOn() || audioManager.isBluetoothScoOn()
					|| audioManager.isBluetoothA2dpOn();
		}

		return false;
	}

	/**
	 * <p>Returns the Application Context.</p>
	 * <p>Main note: do NOT use on Content Provider classes. Only on Activities, Services and Receivers. Read the doc of
	 * {@link ApplicationClass#application_context}, which is the variable returned by this function.</p>
	 *
	 * @return same as in {@link Context#getApplicationContext()}
	 */
	@NonNull
	public static Context getContext() {
		// This shouldn't return null ever, if used from Activities, Service, and Receivers. If it ever does, change
		// @NonNull to @Nullable or think of/find something else/other method.
		return ApplicationClass.application_context;
	}

	/**
	 * <p>Checks if the device is currently running on low memory.</p>
	 *
	 * @return true if running on low memory, false otherwise
	 */
	public static boolean isDeviceRunningOnLowMemory() {
		final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (null == activityManager) {
			return false;
		}

		activityManager.getMemoryInfo(memoryInfo);

		return memoryInfo.lowMemory;
	}

	/**
	 * <p>Vibrate the device once for the amount of time given.</p>
	 *
	 * @param duration milliseconds to vibrate
	 */
	public static void vibrateDeviceOnce(final long duration) {
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (null == vibrator) {
			return;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.MAX_AMPLITUDE));
		} else {
			vibrator.vibrate(duration);
		}
	}

	/**
	 * <p>Broadcast a key press (key down and key up).</p>
	 *
	 * @param key_code one of the {@link KeyEvent}#KEYCODE_-started constants
	 * @param permission the permission the receivers must hold to receive this broadcast, or null to don't require this
	 *                   restriction
	 */
	public static void broadcastKeyEvent(final int key_code, @Nullable final String permission) {
		final Context context = UtilsGeneral.getContext();
		final Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, key_code));
		context.sendBroadcast(intent, permission);
		final Intent intent1 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		intent1.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, key_code));
		context.sendBroadcast(intent1, permission);
	}

	/**
	 * <p>Checks if an intent is recognized on the device (that includes any packages, not just system ones).</p>
	 * <p>For example the intent of sending a message is not supposed to be available on a device which cannot send
	 * SMS messages, and would be a way to check such thing.</p>
	 *
	 * @param intent the intent to check
	 * @param flags same as in {@link PackageManager#queryIntentActivities(Intent, int)}
	 *
	 * @return true if the intent is available, false otherwise
	 */
	public static boolean isIntentActionAvailable(@NonNull final Intent intent, final int flags) {
		final List<ResolveInfo> list = UtilsGeneral.getContext().getPackageManager().
				queryIntentActivities(intent, flags);

		return !list.isEmpty();
	}

	/**
	 * <p>Checks if a {@link android.media.MediaRecorder.AudioSource} is available for immediate use.</p>
	 *
	 * @param audio_source the audio source to check
	 *
	 * @return true if it's available for immediate use, false otherwise (doesn't exist on the device or is busy) OR if
	 * there is no permission to record audio (check that before calling this function)
	 */
	public static boolean isAudioSourceAvailable(final int audio_source) {
		final int sample_rate = 44100;
		final int channel_config = AudioFormat.CHANNEL_IN_MONO;
		final int audio_format = AudioFormat.ENCODING_PCM_16BIT;

		if (!UtilsPermsAuths.checkSelfPermission(Manifest.permission.RECORD_AUDIO)) {
			return false;
		}

		final AudioRecord audioRecord;
		try {
			audioRecord = new AudioRecord(audio_source, sample_rate, channel_config, audio_format,
					AudioRecord.getMinBufferSize(sample_rate, channel_config, audio_format));
		} catch (final IllegalStateException ignored) {
			return false;
		}

		final boolean initialized = AudioRecord.STATE_INITIALIZED == audioRecord.getState();

		boolean success_recording = false;
		if (initialized) {
			try {
				audioRecord.startRecording();
				success_recording = audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
			} catch (final IllegalStateException ignored) {
			}
		}

		if (initialized) {
			// If it's initialized, stop it.
			audioRecord.stop();
		}
		audioRecord.release();

		return success_recording;
	}

	/**
	 * Get available system RAM memory in MiB (not absolute - doesn't count with Kernel memory).
	 *
	 * @return the available RAM memory
	 */
	public static long getAvailableRAM() {
		final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (null == activityManager) {
			return Long.MAX_VALUE;
		}

		activityManager.getMemoryInfo(memoryInfo);

		return memoryInfo.availMem / 1048576L;
	}

	/**
	 * Checks if the device reports that is running on low memory.
	 *
	 * @return true if it's running on low memory, false otherwise
	 */
	public static boolean isDeviceLowOnMemory() {
		final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (null == activityManager) {
			return false;
		}

		return memoryInfo.lowMemory;
	}

	/**
	 * <p>Checks if a Thread state is {@link Thread.State#RUNNABLE}.</p>
	 *
	 * @param thread the thread
	 *
	 * @return true if working, false if stopped
	 */
	public static boolean isThreadWorking(@NonNull final Thread thread) {
		final Thread.State thread_state = thread.getState();
		return !((Thread.State.NEW == thread_state) || (Thread.State.TERMINATED == thread_state));
	}

	/**
	 * <p>Interrupt and quit a {@link HandlerThread} safely if on API level 18+, or forcibly if below.</p>
	 *
	 * @param handlerThread the handler thread to quit from
	 */
	public static void quitHandlerThread(@NonNull final HandlerThread handlerThread) {
		handlerThread.interrupt();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			handlerThread.quitSafely();
		} else {
			handlerThread.quit();
		}
	}

	/**
	 * <p>Same as {@code Application}{@link Context#getSystemService(String)}, but that includes @Nullable on it from AndroidX
	 * to provide warnings.</p>
	 *
	 * @param name same as in {@link Context#getSystemService(String)}
	 *
	 * @return same as in {@link Context#getSystemService(String)}
	 */
	@Nullable
	public static Object getSystemService(@NonNull final String name) {
		return getContext().getSystemService(name);
	}

	/**
	 * <p>Same as {@link ServiceManager#getService(String)}, but that includes @Nullable on it from AndroidX
	 * to provide warnings.</p>
	 *
	 * @param name same as in {@link ServiceManager#getService(String)}
	 *
	 * @return same as in {@link ServiceManager#getService(String)}
	 */
	@Nullable
	public static IBinder getService(@NonNull final String name) {
		return ServiceManager.getService(name);
	}
}
