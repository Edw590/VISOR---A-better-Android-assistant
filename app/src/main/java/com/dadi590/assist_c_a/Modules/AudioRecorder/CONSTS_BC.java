/*
 * Copyright 2021 DADi590
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

package com.dadi590.assist_c_a.Modules.AudioRecorder;

/**
 * <p>Actions, extras, and classes to use to send a broadcast to this module.</p>
 * <br>
 * <p>Check the doc on the action string to know what to do.</p>
 */
public final class CONSTS_BC {

	/**
	 * <p>Private empty constructor so the class can't be instantiated (utility class).</p>
	 */
	private CONSTS_BC() {
	}

	/**
	 * <p>Explanation: same as the main executed function.</p>
	 * <p>Main executed function: {@link UtilsAudioRecorderBC#recordAudio(boolean)}.</p>
	 * <p>Is broadcast by the class(es): {@link UtilsAudioRecorderBC}.</p>
	 * <p>To be received only by the class(es): {@link AudioRecorder}.</p>
	 * <p>Extras (ordered parameters):</p>
	 * <p>- {@link #EXTRA_RECORD_AUDIO_1}: mandatory</p>
	 * <p>- {@link #EXTRA_RECORD_AUDIO_2}: mandatory</p>
	 */
	static final String ACTION_RECORD_AUDIO = "AUDIO_RECORDER_ACTION_RECORD_AUDIO";
	static final String EXTRA_RECORD_AUDIO_1 = "AUDIO_RECORDER_EXTRA_RECORD_AUDIO_1";
	static final String EXTRA_RECORD_AUDIO_2 = "AUDIO_RECORDER_EXTRA_RECORD_AUDIO_2";
}
