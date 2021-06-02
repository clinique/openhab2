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
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.netatmo.internal.NetatmoDescriptionProvider;
import org.openhab.binding.netatmo.internal.api.ApiBridge;
import org.openhab.binding.netatmo.internal.api.NetatmoException;
import org.openhab.binding.netatmo.internal.api.dto.NAHome;
import org.openhab.binding.netatmo.internal.api.dto.NAPlug;
import org.openhab.binding.netatmo.internal.channelhelper.AbstractChannelHelper;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NRVHandler} is the class used to handle the valve
 * module of a thermostat set
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@NonNullByDefault
public class NRVHandler extends NetatmoDeviceHandler {

    private final Logger logger = LoggerFactory.getLogger(NRVHandler.class);

    public NRVHandler(Bridge bridge, List<AbstractChannelHelper> channelHelpers, ApiBridge apiBridge,
            TimeZoneProvider timeZoneProvider, NetatmoDescriptionProvider descriptionProvider) {
        super(bridge, channelHelpers, apiBridge, timeZoneProvider, descriptionProvider);
    }

    private @NonNullByDefault({}) HomeEnergyHandler getHomeHandler() {
        Bridge bridge = super.getBridge();
        if (bridge != null && bridge.getStatus() == ThingStatus.ONLINE) {
            PlugHandler plughandler = (PlugHandler) bridge.getHandler();
            if (plughandler != null) {
                return plughandler.getHomeHandler();
            }
        }
        return null;
    }

    @Override
    protected NAPlug updateReadings() throws NetatmoException {
        NAHome localHome = getHomeHandler().getHome();
        if (localHome != null) {
            return (NAPlug) Objects.requireNonNullElse(localHome.getModule(config.id), new NAPlug());
        }
        return new NAPlug();

    }

}
