/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.freeboxos.internal.api.netshare;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.freeboxos.internal.api.FreeboxException;
import org.openhab.binding.freeboxos.internal.api.Response;
import org.openhab.binding.freeboxos.internal.api.RestManager;
import org.openhab.binding.freeboxos.internal.handler.ApiHandler;

/**
 * The {@link NetShareManager} is the Java class used to handle api requests
 * related to network shares
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class NetShareManager extends RestManager {

    public NetShareManager(ApiHandler apiHandler) {
        super(apiHandler);
    }

    public SambaConfig getSambaConfig() throws FreeboxException {
        return apiHandler.get("netshare/samba/", SambaConfigResponse.class, true);
    }

    public SambaConfig setSambaConfig(SambaConfig config) throws FreeboxException {
        return apiHandler.put("netshare/samba/", config, SambaConfigResponse.class);
    }

    // Response classes and validity evaluations
    private static class SambaConfigResponse extends Response<SambaConfig> {
    }
}
