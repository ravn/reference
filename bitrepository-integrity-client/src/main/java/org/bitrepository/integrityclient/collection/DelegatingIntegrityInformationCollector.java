/*
 * #%L
 * Bitrepository Integrity Client
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
package org.bitrepository.integrityclient.collection;

import org.bitrepository.access.getfileids.BasicGetFileIDsClient;
import org.bitrepository.access.getfileids.GetFileIDsClient;
import org.bitrepository.bitrepositoryelements.FileIDsData;
import org.bitrepository.bitrepositorymessages.IdentifyPillarsForGetFileIDsResponse;
import org.bitrepository.common.JaxbHelper;
import org.bitrepository.integrityclient.IntegrityInformationRetrievalException;
import org.bitrepository.integrityclient.cache.CachedIntegrityInformationStorage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

/**
 * Integrity information collector that delegates collecting information to the clients.
 */
public class DelegatingIntegrityInformationCollector implements IntegrityInformationCollector {
    /** The storage to store data in. */
    private final CachedIntegrityInformationStorage storage;
    /** The client for retrieving file IDs. */
    private final GetFileIDsClient getFileIDsClient;
    //TODO Real client
    /** The client for retrieving checksums. */
    private final Object getChecksumsClient;

    /**
     * Initialise a delegating integrity information collector.
     *
     * @param storage The storage to store data in.
     * @param getFileIDsClient The client for retrieving file IDs
     * @param getChecksumsClient The client for retrieving checksums
     */
    public DelegatingIntegrityInformationCollector(CachedIntegrityInformationStorage storage,
                                                   BasicGetFileIDsClient getFileIDsClient,
                                                   Object getChecksumsClient) {
        this.storage = storage;
        this.getFileIDsClient = getFileIDsClient;
        this.getChecksumsClient = getChecksumsClient;
    }

    @Override
    public void getFileIDs(String slaID, String pillarID, Collection<String> fileIDs) {
        List<IdentifyPillarsForGetFileIDsResponse> responses = getFileIDsClient.identifyPillarsForGetFileIDs(slaID);

        for (IdentifyPillarsForGetFileIDsResponse response: responses) {
            if (pillarID == null || pillarID.equals(response.getPillarID())) {
                File file = getFileIDsClient.getFileIDs(response.getCorrelationID(),
                        slaID, response.getReplyTo(), response.getPillarID());
                FileIDsData data;
                try {
                    data = JaxbHelper.loadXml(FileIDsData.class, new FileInputStream(file));
                } catch (JAXBException e) {
                    throw new IntegrityInformationRetrievalException("Unable to retrieve file IDs", e);
                } catch (FileNotFoundException e) {
                    throw new IntegrityInformationRetrievalException("Unable to retrieve file IDs", e);
                }
                storage.addFileIDs(data);
            }
        }
    }

    @Override
    public void getChecksums(String slaID, String pillarID, Collection<String> fileIDs, String checksumType, byte[] salt) {
        // TODO Implement real thing
        throw new IntegrityInformationRetrievalException("Not implemented");
    }
}
