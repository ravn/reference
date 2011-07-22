/*
 * #%L
 * Bitrepository Access Client
 * *
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
package org.bitrepository.access.getfileids;

import org.bitrepository.bitrepositoryelements.FileIDs;
import org.bitrepository.bitrepositoryelements.ResultingFileIDs;
import org.bitrepository.protocol.eventhandler.EventHandler;
import org.bitrepository.protocol.exceptions.NoPillarFoundException;
import org.bitrepository.protocol.exceptions.OperationFailedException;
import org.bitrepository.protocol.exceptions.OperationTimeOutException;
import org.jaccept.TestEventManager;

import java.net.URL;

/**
 * Wraps the <code>GetFileIDsClient</code> adding test event logging and functionality for handling blocking calls.
 */
public class GetFileIDsClientTestWrapper implements GetFileIDsClient {
    private GetFileIDsClient getFileIDsClient;
    private TestEventManager testEventManager;

    public GetFileIDsClientTestWrapper(GetFileIDsClient getFileIDsClient,
                                       TestEventManager testEventManager) {
        this.getFileIDsClient = getFileIDsClient;
        this.testEventManager = testEventManager;
    }

    @Override
    public ResultingFileIDs getFileIDsFromFastestPillar(String bitRepositoryCollectionID, FileIDs fileIDs)
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" +
                bitRepositoryCollectionID + ", " + fileIDs + ")");
        return getFileIDsClient.getFileIDsFromFastestPillar(bitRepositoryCollectionID, fileIDs);
    }

    @Override
    public void getFileIDsFromFastestPillar(String bitRepositoryCollectionID, FileIDs fileIDs,
                                            EventHandler eventHandler) {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + eventHandler + ")");
        getFileIDsClient.getFileIDsFromFastestPillar(bitRepositoryCollectionID, fileIDs, eventHandler);
    }

    @Override
    public void getFileIDsFromFastestPillar(String bitRepositoryCollectionID, FileIDs fileIDs, URL uploadUrl)
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + uploadUrl + ")");
        getFileIDsClient.getFileIDsFromFastestPillar(bitRepositoryCollectionID, fileIDs, uploadUrl);
    }

    @Override
    public void getFileIDsFromFastestPillar(String bitRepositoryCollectionID, FileIDs fileIDs, URL uploadUrl,
                                            EventHandler eventHandler) {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + uploadUrl + ", " + eventHandler + ")");
        getFileIDsClient.getFileIDsFromFastestPillar(bitRepositoryCollectionID, fileIDs, uploadUrl, eventHandler);
    }

    @Override
    public ResultingFileIDs getFileIDsFromSpecificPillar(String pillarID, String bitRepositoryCollectionID,
                                                         FileIDs fileIDs)
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" + pillarID + ", " +
                bitRepositoryCollectionID + ", " + fileIDs + ")");
        return getFileIDsClient.getFileIDsFromSpecificPillar(pillarID, bitRepositoryCollectionID, fileIDs);
    }

    @Override
    public void getFileIDsFromSpecificPillar(String pillarID, String bitRepositoryCollectionID, FileIDs fileIDs,
                                             EventHandler eventHandler) {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" + pillarID + ", " +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + eventHandler + ")");
        getFileIDsClient.getFileIDsFromSpecificPillar(pillarID, bitRepositoryCollectionID, fileIDs, eventHandler);
    }

    @Override
    public void getFileIDsFromSpecificPillar(String pillarID, String bitRepositoryCollectionID,
                                             FileIDs fileIDs, URL uploadUrl)
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" + pillarID + ", " +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + uploadUrl + ")");
        getFileIDsClient.getFileIDsFromSpecificPillar(pillarID, bitRepositoryCollectionID, fileIDs, uploadUrl);
    }

    @Override
    public void getFileIDsFromSpecificPillar(String pillarID, String bitRepositoryCollectionID,
                                             FileIDs fileIDs, URL uploadUrl, EventHandler eventHandler) {
        testEventManager.addStimuli("Calling getFileIDsFromFastestPillar(" + pillarID + ", " +
                bitRepositoryCollectionID + ", " + fileIDs + ", " + uploadUrl + ", " + eventHandler + ")");
        getFileIDsClient.getFileIDsFromSpecificPillar(pillarID, bitRepositoryCollectionID, fileIDs, uploadUrl,
                eventHandler);
    }
}