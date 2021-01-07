/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.freeboxos.internal.api.system;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.freeboxos.internal.api.FreeboxException;
import org.openhab.binding.freeboxos.internal.api.Response;
import org.openhab.binding.freeboxos.internal.api.RestManager;
import org.openhab.binding.freeboxos.internal.handler.ApiHandler;

/**
 * The {@link SystemManager} is the Java class used to handle api requests
 * related to system
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class SystemManager extends RestManager {

    public SystemManager(ApiHandler apiHandler) {
        super(apiHandler);
    }

    public SystemConf getConfig() throws FreeboxException {
        return apiHandler.get("system/", SystemConfigurationResponse.class, true);
    }

    public void reboot() throws FreeboxException {
        apiHandler.post("system/reboot/", null);
    }

    // Response classes and validity evaluations
    private static class SystemConfigurationResponse extends Response<SystemConf> {
    }
}
