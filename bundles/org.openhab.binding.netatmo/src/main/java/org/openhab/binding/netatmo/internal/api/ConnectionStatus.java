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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@NonNullByDefault
public class ConnectionStatus {
    public static ConnectionStatus SUCCESS = new ConnectionStatus("Connected to Netatmo API");
    public static ConnectionStatus UNKNOWN = new ConnectionStatus("Connection not yet tried");

    private final String message;

    private ConnectionStatus(String message) {
        this.message = message;
    }

    static ConnectionStatus Failed(String message, NetatmoException e) {
        return new ConnectionStatus(String.format(message, e.getMessage()));
    }

    public String getMessage() {
        return message;
    }
}
