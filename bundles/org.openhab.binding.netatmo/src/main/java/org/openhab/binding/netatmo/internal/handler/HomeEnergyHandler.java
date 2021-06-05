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

import static org.openhab.binding.netatmo.internal.NetatmoBindingConstants.CHANNEL_PLANNING;
import static org.openhab.binding.netatmo.internal.NetatmoBindingConstants.CHANNEL_SETPOINT_MODE;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.netatmo.internal.NetatmoDescriptionProvider;
import org.openhab.binding.netatmo.internal.api.ApiBridge;
import org.openhab.binding.netatmo.internal.api.EnergyApi;
import org.openhab.binding.netatmo.internal.api.HomeApi;
import org.openhab.binding.netatmo.internal.api.ModuleType;
import org.openhab.binding.netatmo.internal.api.NetatmoConstants.SetpointMode;
import org.openhab.binding.netatmo.internal.api.NetatmoException;
import org.openhab.binding.netatmo.internal.api.dto.NAHome;
import org.openhab.binding.netatmo.internal.channelhelper.AbstractChannelHelper;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link HomeEnergyHandler} is the class used to handle the plug
 * device of a thermostat set
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@NonNullByDefault
public class HomeEnergyHandler extends NetatmoDeviceHandler {

    private final Logger logger = LoggerFactory.getLogger(HomeEnergyHandler.class);

    private NAHome home = new NAHome();

    public HomeEnergyHandler(Bridge bridge, List<AbstractChannelHelper> channelHelpers, ApiBridge apiBridge,
            TimeZoneProvider timeZoneProvider, NetatmoDescriptionProvider descriptionProvider) {
        super(bridge, channelHelpers, apiBridge, timeZoneProvider, descriptionProvider);
    }

    @Override
    protected NAHome updateReadings() throws NetatmoException {
        EnergyApi api = apiBridge.getRestManager(EnergyApi.class);
        HomeApi homeapi = apiBridge.getRestManager(HomeApi.class);
        if (api != null && homeapi != null) {
            home = homeapi.getHomesData(config.id, ModuleType.NAPlug);
            NAHome status = api.getHomeStatus(config.id);
            // could not find out how to persist retrieved /homesdata and /homestatus so that the information later is
            // accesssible by the other handlers
            home.setRooms(status.getRooms());
            home.setModules(status.getModules());
            return home;
        }
        throw new NetatmoException("No api available to access Welcome Home");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            super.handleCommand(channelUID, command);
        } else {
            String channelName = channelUID.getIdWithoutGroup();
            if (CHANNEL_PLANNING.equals(channelName)) {
                apiBridge.getEnergyApi().ifPresent(api -> {
                    tryApiCall(() -> api.switchSchedule(config.id, command.toString()));
                });
            } else if (channelName.equals(CHANNEL_SETPOINT_MODE)) {
                SetpointMode targetMode = SetpointMode.valueOf(command.toString());
                if (targetMode == SetpointMode.MANUAL) {
                    // updateState(channelUID, toStringType(currentData.getSetpointMode()));
                    logger.info("Switch to 'Manual' is done by setting a setpoint temp, command ignored");
                } else {
                    callSetThermMode(config.id, targetMode);
                }
            }

        }
    }

    @Override
    protected void updateChildModules() {
        super.updateChildModules();
        if (naThing instanceof NAHome) {
            NAHome localNaThing = (NAHome) naThing;
            if (localNaThing != null) {
                localNaThing.getRooms().forEach(entry -> notifyListener(entry.getId(), entry));
            }
        }
    }

    public void callSetThermMode(String homeId, SetpointMode targetMode) {
        apiBridge.getEnergyApi().ifPresent(api -> {
            tryApiCall(() -> api.setThermMode(homeId, targetMode.getDescriptor()));
        });
    }

    public int getSetpointDefaultDuration() {
        NAHome localHome = getHome();
        if (localHome != null) {
            return home.getThermSetpointDefaultDuration();
        }
        return -1;
    }

    public @Nullable NAHome getHome() {
        return home;
    }
}
