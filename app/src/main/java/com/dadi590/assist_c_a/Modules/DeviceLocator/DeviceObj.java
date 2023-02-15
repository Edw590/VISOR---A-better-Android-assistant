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

package com.dadi590.assist_c_a.Modules.DeviceLocator;

/**
 * <p>Class to instantiate to get and set information about the current device (using the app).</p>
 */
public final class DeviceObj {

	// Location
	public static final int LOCATION_UNKNOWN = -1;
	public static final int LOCATION_AT_HOME = 0;
	public static final int LOCATION_LEAVING_HOME = 1;

	public int location = LOCATION_UNKNOWN;
}
