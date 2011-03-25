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
package org.bitrepository.protocol;

import org.bitrepository.bitrepositorymessages.IdentifyPillarsForGetFileRequest;

/**
 * Generates test messages for use in test.
 */
public class TestMessageFactory {

    /**
     * Prevent initialisation - currently a utility class.
     */
    private TestMessageFactory() {}

    /**
     * Generate a test message with dummy values.
     *
     * @return A valid but arbitrary message.
     */
    public static IdentifyPillarsForGetFileRequest getTestMessage() {
        IdentifyPillarsForGetFileRequest identifyPillarsForGetFileRequest
                = new IdentifyPillarsForGetFileRequest();
        identifyPillarsForGetFileRequest.setCorrelationID("CorrelationID");
        identifyPillarsForGetFileRequest.setFileID("FileID");
        identifyPillarsForGetFileRequest.setMinVersion((short) 0);
        identifyPillarsForGetFileRequest.setReplyTo("ReplyTo");
        identifyPillarsForGetFileRequest.setSlaID("SlaID");
        identifyPillarsForGetFileRequest.setVersion((short) 0);
        return identifyPillarsForGetFileRequest;
    }
}
