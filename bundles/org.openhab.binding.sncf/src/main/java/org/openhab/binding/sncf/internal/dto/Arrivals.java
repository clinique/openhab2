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
package org.openhab.binding.sncf.internal.dto;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Arrivals} is responsible for storing
 * informations returned by vehicle position rest
 * answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class Arrivals extends SncfAnswer {
    private List<Passage> arrivals = new ArrayList<>();
    private List<Exception> exceptions = new ArrayList<>();

    public List<Passage> getArrivals() {
        return arrivals;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }
}
