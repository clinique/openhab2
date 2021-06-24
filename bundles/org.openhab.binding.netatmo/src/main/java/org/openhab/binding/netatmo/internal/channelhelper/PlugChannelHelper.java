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
package org.openhab.binding.netatmo.internal.channelhelper;

import static org.openhab.binding.netatmo.internal.NetatmoBindingConstants.*;
import static org.openhab.binding.netatmo.internal.utils.ChannelTypeUtils.toDateTimeType;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.netatmo.internal.api.dto.NAPlug;
import org.openhab.binding.netatmo.internal.api.dto.NAThing;
import org.openhab.core.types.State;

/**
 * The {@link PlugChannelHelper} handle specific behavior
 * of modules using batteries
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@NonNullByDefault
public class PlugChannelHelper extends AbstractChannelHelper {

    public PlugChannelHelper() {
        super(Set.of(GROUP_PLUG));
    }

    @Override
    protected @Nullable State internalGetProperty(String channelId, NAThing naThing) {
        if (naThing instanceof NAPlug) {
            NAPlug plug = (NAPlug) naThing;
            switch (channelId) {
                case CHANNEL_CONNECTED_BOILER:
                    return plug.getPlugConnectedBoiler();
                case CHANNEL_LAST_BILAN:
                    return toDateTimeType(plug.getLastBilan());
            }
        }
        return null;
    }
}
