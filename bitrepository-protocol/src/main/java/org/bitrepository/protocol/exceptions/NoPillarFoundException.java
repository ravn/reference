/*
 * #%L
 * Bitrepository Protocol
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
package org.bitrepository.protocol.exceptions;

/**
 * Indicates a failure find a suitable pillar after dispatching a IdentidyPillarsRequest.
 */
@SuppressWarnings("serial")
public class NoPillarFoundException extends OperationFailedException {

    /** Constructor including causing exception */
    public NoPillarFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructor without causing exception */
    public NoPillarFoundException(String message) {
        super(message);
    }
}
