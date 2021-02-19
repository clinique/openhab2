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
package org.openhab.binding.netatmo.internal.handler;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.netatmo.internal.NetatmoDescriptionProvider;
import org.openhab.binding.netatmo.internal.api.ApiBridge;
import org.openhab.binding.netatmo.internal.api.dto.NRV;
import org.openhab.binding.netatmo.internal.channelhelper.AbstractChannelHelper;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.binding.netatmo.internal.api.NetatmoException;
import org.openhab.binding.netatmo.internal.api.EnergyApi;



/**
 * {@link NRVHandler} is the class used to handle the valve
 * module of a thermostat set
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@SuppressWarnings("unused")
@NonNullByDefault
public class NRVHandler extends NetatmoDeviceHandler {

    public NRVHandler(Bridge bridge, List<AbstractChannelHelper> channelHelpers, ApiBridge apiBridge,
            TimeZoneProvider timeZoneProvider, NetatmoDescriptionProvider descriptionProvider) {
        super(bridge, channelHelpers, apiBridge, timeZoneProvider, descriptionProvider);
    }

	private @NonNullByDefault({}) HomeEnergyHandler getHomeHandler() {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getStatus() == ThingStatus.ONLINE) {
            return (HomeEnergyHandler) bridge.getHandler();
        }
        return null;
    }
/* 	@Override
    protected NRV updateReadings() throws NetatmoException {
        EnergyApi api = apiBridge.getRestManager(EnergyApi.class);
        if (api != null) {
            return api.getValveData(config.id);
        }
        throw new NetatmoException("No restmanager available for Energy access");
    } */

}
