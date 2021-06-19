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
package org.openhab.binding.netatmo.internal.api;

import static org.openhab.binding.netatmo.internal.api.NetatmoConstants.*;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.netatmo.internal.api.WeatherApi.NAStationDataResponse;
import org.openhab.binding.netatmo.internal.api.dto.NAMain;

/**
 * Base class for all Air Care related rest manager
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class AircareApi extends RestManager {

    public AircareApi(ApiBridge apiClient) {
        super(apiClient/* , FeatureArea.AIR_CARE */);
    }

    /**
     *
     * The method gethomecoachsdata Returns data from a user Healthy Home Coach Station (measures and device specific
     * data).
     *
     * @param deviceId Id of the device you want to retrieve information of (optional)
     * @return NAStationDataResponse
     * @throws NetatmoException If fail to call the API, e.g. server error or cannot deserialize the
     *             response body
     */
    public NAStationDataResponse getHomeCoachData(@Nullable String deviceId) throws NetatmoException {
        UriBuilder uriBuilder = getApiUriBuilder().path(SPATH_HOMECOACH);
        if (deviceId != null) {
            uriBuilder.queryParam(PARM_DEVICEID, deviceId);
        }
        return get(uriBuilder, NAStationDataResponse.class);
    }

    public NAMain getHomeCoach(String deviceId) throws NetatmoException {
        ListBodyResponse<NAMain> answer = getHomeCoachData(deviceId).getBody();
        NAMain station = answer.getElement(deviceId);
        if (station != null) {
            return station;
        }
        throw new NetatmoException(String.format("Unexpected answer cherching device '%s' : not found.", deviceId));
    }
}
