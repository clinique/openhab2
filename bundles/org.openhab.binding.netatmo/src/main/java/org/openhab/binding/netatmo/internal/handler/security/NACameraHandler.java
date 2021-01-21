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
package org.openhab.binding.netatmo.internal.handler.security;

import static org.openhab.binding.netatmo.internal.NetatmoBindingConstants.*;
import static org.openhab.binding.netatmo.internal.utils.ChannelTypeUtils.*;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.netatmo.internal.api.ApiBridge;
import org.openhab.binding.netatmo.internal.api.NetatmoException;
import org.openhab.binding.netatmo.internal.api.dto.NAEvent;
import org.openhab.binding.netatmo.internal.api.dto.NAThing;
import org.openhab.binding.netatmo.internal.api.home.HomeApi;
import org.openhab.binding.netatmo.internal.api.home.NAHomeEvent;
import org.openhab.binding.netatmo.internal.api.home.NASnapshot;
import org.openhab.binding.netatmo.internal.api.security.NAWelcome;
import org.openhab.binding.netatmo.internal.channelhelper.AbstractChannelHelper;
import org.openhab.binding.netatmo.internal.handler.NetatmoDeviceHandler;
import org.openhab.binding.netatmo.internal.handler.energy.NADescriptionProvider;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateOption;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NACameraHandler} is the class used to handle Camera Data
 *
 * @author Sven Strohschein - Initial contribution (partly moved code from NAWelcomeCameraHandler to introduce
 *         inheritance, see NAWelcomeCameraHandler)
 *
 */
@NonNullByDefault
public class NACameraHandler extends NetatmoDeviceHandler {
    private final Logger logger = LoggerFactory.getLogger(NACameraHandler.class);
    private @Nullable CameraAddress cameraAddress;
    private @Nullable String vpnUrl;
    private boolean isLocal;
    private final HomeApi homeApi;

    public NACameraHandler(Bridge bridge, List<AbstractChannelHelper> channelHelpers, ApiBridge apiBridge,
            TimeZoneProvider timeZoneProvider, NADescriptionProvider descriptionProvider) {
        super(bridge, channelHelpers, apiBridge, timeZoneProvider, descriptionProvider);
        this.homeApi = apiBridge.getHomeApi();
    }

    private @Nullable NAHomeSecurityHandler getHomeHandler() {
        NetatmoDeviceHandler handler = super.getBridgeHandler(getBridge());
        return handler != null ? (NAHomeSecurityHandler) handler : null;
    }

    @Override
    public void setNAThing(NAThing naModule) {
        super.setNAThing(naModule);
        NAWelcome camera = (NAWelcome) naModule;
        this.vpnUrl = camera.getVpnUrl();
        this.isLocal = camera.isLocal();
        NAHomeSecurityHandler homeHandler = getHomeHandler();
        if (homeHandler != null) {
            descriptionProvider.setStateOptions(
                    new ChannelUID(getThing().getUID(), GROUP_WELCOME_EVENT, CHANNEL_EVENT_PERSON_ID),
                    homeHandler.getKnownPersons().stream().map(p -> new StateOption(p.getId(), p.getName()))
                            .collect(Collectors.toList()));
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if ((command instanceof OnOffType) && (CHANNEL_CAMERA_IS_MONITORING.equals(channelUID.getIdWithoutGroup()))) {
            String localCameraURL = getLocalCameraURL();
            if (localCameraURL != null) {
                tryApiCall(() -> homeApi.changeStatus(localCameraURL, command == OnOffType.ON));
            }
        } else {
            super.handleCommand(channelUID, command);
        }
    }

    protected @Nullable String getLocalCameraURL() {
        CameraAddress address = cameraAddress;
        String vpn = vpnUrl;
        if (vpn != null) {
            // The local address is (re-)requested when it wasn't already determined or when
            // the vpn address was changed.
            if (address == null || address.isVpnURLChanged(vpn)) {
                String localUrl = pingVpnUrl(vpn);
                if (localUrl != null) {
                    address = new CameraAddress(vpn, localUrl);
                    cameraAddress = address;
                    return address.getLocalURL();
                }
            } else {
                return address.getLocalURL();
            }
        }
        return null;
    }

    @Override
    public void setEvent(NAEvent event) {
        updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_TYPE, toStringType(event.getEventType()));
        updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_MESSAGE, toStringType(event.getMessage()));
        updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_TIME, toDateTimeType(event.getTime(), zoneId));
        updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_PERSON_ID, toStringType(event.getPersonId()));
        updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_SUBTYPE,
                event.getSubTypeDescription().map(d -> toStringType(d)).orElse(UnDefType.NULL));

        NASnapshot snapshot = event.getSnapshot();
        if (snapshot != null) {
            String url = snapshot.getUrl();
            updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_SNAPSHOT, toRawType(url));
            updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_SNAPSHOT_URL, toStringType(url));
        }
        if (event instanceof NAHomeEvent) {
            NAHomeEvent homeEvent = (NAHomeEvent) event;
            updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_VIDEO_STATUS, toStringType(homeEvent.getVideoStatus()));
            updateIfLinked(GROUP_WELCOME_EVENT, CHANNEL_EVENT_VIDEO_URL,
                    toStringType(getStreamURL(homeEvent.getVideoId())));
        }
    }

    public @Nullable String getStreamURL(@Nullable String videoId) {
        if (videoId != null && vpnUrl != null) {
            StringBuilder result = new StringBuilder(String.format("%s/vod/%s/index", vpnUrl, videoId));
            if (isLocal) {
                result.append("_local");
            }
            result.append(".m3u8");
            return result.toString();
        }
        return null;
    }

    public @Nullable String pingVpnUrl(String vpnUrl) {
        try {
            return homeApi.ping(vpnUrl);
        } catch (NetatmoException e) {
            logger.warn("Error pinging camera : {}", e.getMessage());
            return null;
        }
    }
}
