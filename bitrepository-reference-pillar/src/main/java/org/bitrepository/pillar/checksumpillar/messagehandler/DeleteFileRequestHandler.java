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

import org.bitrepository.bitrepositoryelements.ChecksumDataForFileTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.FileAction;
import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.bitrepositoryelements.ResponseInfo;
import org.bitrepository.bitrepositorymessages.DeleteFileFinalResponse;
import org.bitrepository.bitrepositorymessages.DeleteFileProgressResponse;
import org.bitrepository.bitrepositorymessages.DeleteFileRequest;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.common.utils.Base16Utils;
import org.bitrepository.common.utils.CalendarUtils;
import org.bitrepository.common.utils.ResponseInfoUtils;
import org.bitrepository.pillar.cache.ChecksumEntry;
import org.bitrepository.pillar.cache.ChecksumStore;
import org.bitrepository.pillar.common.MessageHandlerContext;
import org.bitrepository.protocol.*;
import org.bitrepository.service.exception.IllegalOperationException;
import org.bitrepository.service.exception.InvalidMessageException;
import org.bitrepository.service.exception.RequestHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling the DeleteFile operation for the checksum pillar.
 */
public class DeleteFileRequestHandler extends ChecksumPillarMessageHandler<DeleteFileRequest> {
    /** The log.*/
    private Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * Constructor.
     * @param context The context of the message handler.
     * @param refCache The cache for the checksum data.
     */
    public DeleteFileRequestHandler(MessageHandlerContext context, ChecksumStore refCache) {
        super(context, refCache);
    }
    
    @Override
    public Class<DeleteFileRequest> getRequestClass() {
        return DeleteFileRequest.class;
    }

    @Override
    public void processRequest(DeleteFileRequest message, MessageContext messageContext) throws RequestHandlerException {
        validateMessage(message);
        sendProgressMessage(message);
        ChecksumDataForFileTYPE resultingChecksum = calculatedRequestedChecksum(message);
        deleteTheFile(message, messageContext);
        sendFinalResponse(message, resultingChecksum);
    }

    @Override
    public MessageResponse generateFailedResponse(DeleteFileRequest request) {
        return createDeleteFileFinalResponse(request);
    }
    
    /**
     * Method for validating the content of the message.
     * @param message The message requesting the operation, which should be validated.
     */
    protected void validateMessage(DeleteFileRequest message) throws RequestHandlerException {
        // Validate the message.
        validatePillarId(message.getPillarID());
        validateCollectionID(message);
        validateChecksumSpec(message.getChecksumRequestForExistingFile(), message.getCollectionID());
        if(message.getChecksumDataForExistingFile() != null) {
            validateChecksumSpec(message.getChecksumDataForExistingFile().getChecksumSpec(), message.getCollectionID());
        } else if(getSettings().getRepositorySettings()
                .getProtocolSettings().isRequireChecksumForDestructiveRequests()) {
            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setResponseCode(ResponseCode.EXISTING_FILE_CHECKSUM_FAILURE);
            responseInfo.setResponseText("No checksum was supplied for the file to delete, even though according to "
                    + "the contract a checksum for file to be deleted during the deleting operation is required.");
            throw new IllegalOperationException(responseInfo, message.getCollectionID());            
        }
        
        validateFileID(message.getFileID());

        // Validate, that we have the requested file.
        if(!getCache().hasFile(message.getFileID(), message.getCollectionID())) {
            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setResponseCode(ResponseCode.FILE_NOT_FOUND_FAILURE);
            responseInfo.setResponseText("The file '" + message.getFileID() + "' has been requested, but we do "
                    + "not have that file!");
            throw new InvalidMessageException(responseInfo, message.getCollectionID());
        }
        
        ChecksumDataForFileTYPE checksumData = message.getChecksumDataForExistingFile();
        if(checksumData != null) {
            String calculatedChecksum = getCache().getChecksum(message.getFileID(), message.getCollectionID());
            String requestChecksum = Base16Utils.decodeBase16(checksumData.getChecksumValue());
            if(!calculatedChecksum.equals(requestChecksum)) {
                // Log the different checksums, but do not send the right checksum back!
                log.warn("Failed to handle delete operation on file '" + message.getFileID() + "' since the request "
                        + "had the checksum '" + requestChecksum + "' where our local file has the value '" 
                        + calculatedChecksum + "'. Sending alarm and respond failure.");
                
                ResponseInfo responseInfo = new ResponseInfo();
                responseInfo.setResponseCode(ResponseCode.EXISTING_FILE_CHECKSUM_FAILURE);
                responseInfo.setResponseText("Requested to delete file '" + message.getFileID() + "' with checksum '"
                        + requestChecksum + "', but our file had a different checksum.");
                throw new IllegalOperationException(responseInfo, message.getCollectionID());
            }
        } else {
            log.debug("No checksum for validation of the existing file before delete the file '" + message.getFileID() 
                    + "'");
        }
    }

    /**
     * The method for sending a progress response telling, that the operation is about to be performed.
     * @param request The request for the GetFile operation.
     */
    protected void sendProgressMessage(DeleteFileRequest request) {
        DeleteFileProgressResponse response = createDeleteFileProgressResponse(request);
        response.setResponseInfo(ResponseInfoUtils.getInitialProgressResponse());
        dispatchResponse(response, request);
    }
    
    /**
     * Method for calculating the requested checksum. 
     * If no checksum is requested to be delivered back a warning is logged.
     * @param message The request for deleting the file. Contains the specs for calculating the checksum.
     * @return The requested checksum, or null if no such checksum is requested.
     */
    protected ChecksumDataForFileTYPE calculatedRequestedChecksum(DeleteFileRequest message) {
        if(message.getChecksumRequestForExistingFile() == null) {
            return null;
        }
        
        ChecksumDataForFileTYPE res = new ChecksumDataForFileTYPE();
        ChecksumSpecTYPE checksumType = message.getChecksumRequestForExistingFile();
        
        ChecksumEntry entry = getCache().getEntry(message.getFileID(), message.getCollectionID());
        res.setChecksumSpec(checksumType);
        res.setCalculationTimestamp(CalendarUtils.getXmlGregorianCalendar(entry.getCalculationDate()));
        res.setChecksumValue(Base16Utils.encodeBase16(entry.getChecksum()));
        
        return res;
    }
    
    /**
     * Performs the operation of deleting the file from the archive.
     * @param message The message requesting the file to be deleted.
     */
    protected void deleteTheFile(DeleteFileRequest message, MessageContext messageContext) {
        getAuditManager().addAuditEvent(message.getCollectionID(), message.getFileID(), message.getFrom(), 
                "Deleting the file.", message.getAuditTrailInformation(), FileAction.DELETE_FILE,
                message.getCorrelationID(), messageContext.getCertificateFingerprint());
        getCache().deleteEntry(message.getFileID(), message.getCollectionID());
    }

    /**
     * Method for sending the final response.
     * @param request The request to respond to.
     * @param requestedChecksum The results of the requested checksums
     */
    protected void sendFinalResponse(DeleteFileRequest request, ChecksumDataForFileTYPE requestedChecksum) {
        DeleteFileFinalResponse response = createDeleteFileFinalResponse(request);
        response.setChecksumDataForExistingFile(requestedChecksum);
        ResponseInfo frInfo = new ResponseInfo();
        frInfo.setResponseCode(ResponseCode.OPERATION_COMPLETED);
        response.setResponseInfo(frInfo);

        dispatchResponse(response, request);
    }
    
    /**
     * Creates a DeleteFileProgressResponse based on a DeleteFileRequest. Missing the 
     * following fields:
     * <br/> - ResponseInfo
     * 
     * @param request The DeleteFileRequest to base the progress response on.
     * @return The DeleteFileProgressResponse based on the request.
     */
    private DeleteFileProgressResponse createDeleteFileProgressResponse(DeleteFileRequest request) {
        DeleteFileProgressResponse res = new DeleteFileProgressResponse();
        res.setFileID(request.getFileID());
        res.setPillarID(getSettings().getReferenceSettings().getPillarSettings().getPillarID());
        
        return res;
    }
    
    /**
     * Creates a DeleteFileFinalResponse based on a DeleteFileRequest. Missing the 
     * following fields:
     * <br/> - ResponseInfo
     * <br/> - ChecksumDataForFile
     * 
     * @param request The DeleteFileRequest to base the final response on.
     * @return The DeleteFileFinalResponse based on the request.
     */
    private DeleteFileFinalResponse createDeleteFileFinalResponse(DeleteFileRequest request) {
        DeleteFileFinalResponse res = new DeleteFileFinalResponse();
        res.setFileID(request.getFileID());
        res.setPillarID(getSettings().getReferenceSettings().getPillarSettings().getPillarID());

        return res;
    }
}
