/*
 * #%L
 * Bitrepository Access Client
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
package org.bitrepository.modify.deletefile;

import org.bitrepository.bitrepositoryelements.ChecksumDataForFileTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.client.eventhandler.EventHandler;
import org.jaccept.TestEventManager;

/**
 * Wrapper class for a DeleteFileClient adding testmanager logging.
 */
public class DeleteFileClientTestWrapper implements DeleteFileClient {
    /** The PutClient to wrap. */
    private DeleteFileClient wrappedDeleteClient;
    /** The manager to monitor the operations.*/
    private TestEventManager testEventManager;

    /**
     * Constructor.
     * @param deleteClientInstance The instance to wrap and monitor.
     * @param eventManager The manager to monitor the operations.
     */
    public DeleteFileClientTestWrapper(DeleteFileClient deleteClientInstance, TestEventManager eventManager) {
        this.wrappedDeleteClient = deleteClientInstance;
        this.testEventManager = eventManager;
    }

    @Override
    public void deleteFile(String collectionID, String fileID, String pillarID,
                           ChecksumDataForFileTYPE checksumForPillar,
            ChecksumSpecTYPE checksumRequested, EventHandler eventHandler, String auditTrailInformation) {
        testEventManager.addStimuli("Calling deleteFile(" + fileID + ", " + pillarID + ", " + checksumForPillar + ", "
            + checksumRequested + ", eventHandler, " + auditTrailInformation + ")");
        wrappedDeleteClient.deleteFile(collectionID, fileID, pillarID, checksumForPillar, checksumRequested,
                eventHandler,
                auditTrailInformation);        
    }
}
