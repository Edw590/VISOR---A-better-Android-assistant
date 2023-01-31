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

package com.dadi590.assist_c_a.Modules.Telephony;

import android.Manifest;

import androidx.annotation.NonNull;

import com.dadi590.assist_c_a.GlobalInterfaces.IModuleInst;
import com.dadi590.assist_c_a.GlobalUtils.UtilsCheckHardwareFeatures;
import com.dadi590.assist_c_a.GlobalUtils.UtilsPermsAuths;
import com.dadi590.assist_c_a.Modules.CmdsExecutor.CmdsList.UtilsCmdsList;
import com.dadi590.assist_c_a.Modules.ModulesManager.ModulesManager;
import com.dadi590.assist_c_a.Modules.Telephony.PhoneCallsProcessor.PhoneCallsProcessor;
import com.dadi590.assist_c_a.Modules.Telephony.SmsMsgsProcessor.SmsMsgsProcessor;
import com.dadi590.assist_c_a.ModulesList;

/**
 * <p>The module that manages all telephony-related things, including the Phone Calls Processor and the SMS Messages
 * Processor submodules.</p>
 */
public class TelephonyManager implements IModuleInst {

	@NonNull
	public static String[][] ALL_CONTACTS = {};

	///////////////////////////////////////////////////////////////
	// IModuleInst stuff
	private boolean is_module_destroyed = false;
	@Override
	public final boolean isFullyWorking() {
		if (is_module_destroyed) {
			return false;
		}

		return infinity_thread.isAlive();
	}
	@Override
	public final void destroy() {
		infinity_thread.interrupt();
		ModulesList.stopModule(ModulesList.getElementIndex(PhoneCallsProcessor.class));
		ModulesList.stopModule(ModulesList.getElementIndex(SmsMsgsProcessor.class));

		is_module_destroyed = true;
	}
	@Override
	public final int wrongIsSupported() {return 0;}
	/**.
	 * @return read all here {@link IModuleInst#wrongIsSupported()} */

	public static boolean isSupported() {
		return UtilsCheckHardwareFeatures.isTelephonySupported(true);
	}
	// IModuleInst stuff
	///////////////////////////////////////////////////////////////

	/**
	 * <p>Main class constructor.</p>
	 */
	public TelephonyManager() {
		infinity_thread.start();
	}

	final Thread infinity_thread = new Thread(new Runnable() {
		@Override
		public void run() {
			final int[] modules_indexes = {
					ModulesList.getElementIndex(PhoneCallsProcessor.class),
					ModulesList.getElementIndex(SmsMsgsProcessor.class),
			};
			final Class<?>[] modules_classes = {
					PhoneCallsProcessor.class,
					SmsMsgsProcessor.class,
			};
			final int num_modules = modules_indexes.length;

			while (true) {
				for (int i = 0; i < num_modules; ++i) {
					final int module_index = modules_indexes[i];

					if (ModulesList.isSubModuleSupported(modules_classes[i]) && !ModulesList.isModuleFullyWorking(module_index)) {
						ModulesList.restartModule(module_index);
					}
				}

				if (UtilsPermsAuths.checkSelfPermission(Manifest.permission.READ_CONTACTS)) {
					// Every CHECK_INTERNAL seconds, update the contacts list for commands to be available for new
					// contacts or to remove from it removed contacts, or to update updated contacts (like number or
					// name or whatever). Also if the READ_CONTACTS permissions was just granted, add the contacts from
					// scratch.
					ALL_CONTACTS = UtilsTelephony.getAllContacts(UtilsTelephony.ALL_CONTACTS);
					UtilsCmdsList.updateMakeCallCmdContacts(ALL_CONTACTS);

					System.out.println("~~~~~~~~~~~~~~~~~~");
					System.out.println(ALL_CONTACTS.length);
				}

				try {
					Thread.sleep(ModulesManager.CHECK_INTERVAL);
				} catch (final InterruptedException ignored) {
					Thread.currentThread().interrupt();

					return;
				}
			}
		}
	});
}
