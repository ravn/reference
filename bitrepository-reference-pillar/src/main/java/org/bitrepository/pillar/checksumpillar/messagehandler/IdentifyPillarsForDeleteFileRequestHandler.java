/*
 * #%L
 * Bitrepository Reference Pillar
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
package org.bitrepository.pillar.checksumpillar.messagehandler;

import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.bitrepositoryelements.ResponseInfo;
import org.bitrepository.bitrepositorymessages.IdentifyPillarsForDeleteFileRequest;
import org.bitrepository.bitrepositorymessages.IdentifyPillarsForDeleteFileResponse;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.pillar.checksumpillar.cache.ChecksumStore;
import org.bitrepository.pillar.common.PillarContext;
import org.bitrepository.protocol.utils.TimeMeasurementUtils;
import org.bitrepository.service.exception.IdentifyContributorException;
import org.bitrepository.service.exception.RequestHandlerException;

/**
 * Class for handling the identification of this pillar for the purpose of performing the DeleteFile operation.
 */
public class IdentifyPillarsForDeleteFileRequestHandler 
        extends ChecksumPillarMessageHandler<IdentifyPillarsForDeleteFileRequest> {
    /**
     * Constructor.
     * @param context The context of the message handler.
     * @param refCache The cache for the checksum data.
     */
    public IdentifyPillarsForDeleteFileRequestHandler(PillarContext context, ChecksumStore refCache) {
        super(context, refCache);
    }

    @Override
    public Class<IdentifyPillarsForDeleteFileRequest> getRequestClass() {
        return IdentifyPillarsForDeleteFileRequest.class;
    }

    @Override
    public void processRequest(IdentifyPillarsForDeleteFileRequest message) throws RequestHandlerException {
        validateFileID(message.getFileID());
        checkThatRequestedFileIsAvailable(message);
        respondSuccessfulIdentification(message);
    }

    @Override
    public MessageResponse generateFailedResponse(IdentifyPillarsForDeleteFileRequest message) {
        return createFinalResponse(message);
    }
    
    /**
     * Validates that the requested files are present in the archive. 
     * Otherwise an {@link IdentifyContributorException} with the appropriate errorcode is thrown.
     * @param message The message containing the id of the file. If no file id is given, then a warning is logged, 
     * but the operation is accepted.
     */
    private void checkThatRequestedFileIsAvailable(IdentifyPillarsForDeleteFileRequest message) 
            throws RequestHandlerException {
        if(!getCache().hasFile(message.getFileID())) {
            ResponseInfo irInfo = new ResponseInfo();
            irInfo.setResponseCode(ResponseCode.FILE_NOT_FOUND_FAILURE);
            irInfo.setResponseText("Could not find the requested file to delete.");
            throw new IdentifyContributorException(irInfo);
        }
    }

    /**
     * Method for making a successful response to the identification.
     * @param message The request message to respond to.
     */
    private void respondSuccessfulIdentification(IdentifyPillarsForDeleteFileRequest message) {
        // Create the response.
        IdentifyPillarsForDeleteFileResponse reply = createFinalResponse(message);
        
        // set the missing variables in the reply:
        // TimeToDeliver, IdentifyResponseInfo (ignore PillarChecksumSpec)
        reply.setTimeToDeliver(TimeMeasurementUtils.getTimeMeasurementFromMiliseconds(
                getSettings().getReferenceSettings().getPillarSettings().getTimeToStartDeliver()));
        
        ResponseInfo irInfo = new ResponseInfo();
        irInfo.setResponseCode(ResponseCode.IDENTIFICATION_POSITIVE);
        irInfo.setResponseText(RESPONSE_FOR_POSITIVE_IDENTIFICATION);
        reply.setResponseInfo(irInfo);
        
        getMessageBus().sendMessage(reply);
    }
    
    /**
     * Creates a IdentifyPillarsForDeleteFileResponse based on a 
     * IdentifyPillarsForDeleteFileRequest. The following fields are not inserted:
     * <br/> - TimeToDeliver
     * <br/> - ResponseInfo
     * <br/> - PillarChecksumSpec
     * 
     * @param msg The IdentifyPillarsForDeleteFileRequest to base the response on.
     * @return The response to the request.
     */
    private IdentifyPillarsForDeleteFileResponse createFinalResponse(IdentifyPillarsForDeleteFileRequest msg) {
        IdentifyPillarsForDeleteFileResponse res = new IdentifyPillarsForDeleteFileResponse();
        populateResponse(msg, res);
        res.setFileID(msg.getFileID());
        res.setPillarID(getSettings().getReferenceSettings().getPillarSettings().getPillarID());
        res.setPillarChecksumSpec(getChecksumType());
        
        return res;
    }
}
