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
package org.openhab.binding.sncf.internal;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

/**
 * Exception for errors when using the VolvoOnCall API
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class SncfException extends Exception {
    private final Logger logger = LoggerFactory.getLogger(SncfException.class);
    private static final long serialVersionUID = -6215621577081394328L;

    public static enum ErrorType {
        UNKNOWN,
        SERVICE_UNAVAILABLE,
        IOEXCEPTION,
        JSON_SYNTAX;
    }

    private final ErrorType cause;

    public SncfException(String label, @Nullable String description) {
        super(label);
        if ("FoundationServicesUnavailable".equalsIgnoreCase(label)) {
            cause = ErrorType.SERVICE_UNAVAILABLE;
        } else {
            cause = ErrorType.UNKNOWN;
            logger.warn("Unhandled VoC error : {} : {}", label, description);
        }
    }

    public SncfException(Exception e) {
        super(e);
        if (e instanceof IOException) {
            cause = ErrorType.IOEXCEPTION;
        } else if (e instanceof JsonSyntaxException) {
            cause = ErrorType.JSON_SYNTAX;
        } else {
            cause = ErrorType.UNKNOWN;
            logger.warn("Unhandled VoC error : {}", e.getMessage());
        }
    }

    public ErrorType getType() {
        return cause;
    }
}
