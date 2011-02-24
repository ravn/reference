/*
 * #%L
 * bitrepository-common
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2010 - 2011 The State and University Library, The Royal Library and The State Archives, Denmark
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.bitrepository.access.exception;

/**
 * Exception for the access module.
 * @author jolf
 */
public class AccessException extends RuntimeException {
    /**
     * Constructor for this exception with both text message and a cause.
     * @param message The message for the exception.
     * @param cause The cause in the form of another Throwable which has 
     * triggered this exception.
     */
    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for this exception based on a text message.
     * @param message The message for the exception.
     */
    public AccessException(String message) {
        super(message);
    }

    /**
     * Constructor for this exception based on a cause.
     * @param cause The Throwable cause which has triggered this exception.
     */
    public AccessException(Throwable cause) {
        super(cause);
    }
}
