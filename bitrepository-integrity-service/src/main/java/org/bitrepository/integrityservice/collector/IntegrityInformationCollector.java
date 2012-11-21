/*
 * #%L
 * Bitrepository Integrity Client
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
package org.bitrepository.integrityservice.collector;

import java.util.Collection;

import org.bitrepository.access.ContributorQuery;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.client.eventhandler.EventHandler;

/**
 * This is the interface for initiating collecting integrity information from pillars.
 *
 * It is expected to be called from a scheduler that generates events to collect specific data.
 * Results should be stored in the {@link org.bitrepository.integrityservice.cache.IntegrityModel}
 * 
 * The EventHandler given as argument will determine what to do after the collection. 
 * E.g. perform a integrity check on the collected data.
 */
public interface IntegrityInformationCollector {
    
    /**
     * Starts collection the given file ids from the given pillar ids.
     * 
     * @param pillarIDs The collection of ids of the pillars to request for the file ids.
     * @param auditTrailInformation The audit trail information for the conversation.
     * @param queries The limiting contributor queries for the collection of file ids. 
     * @param eventHandler The eventhandler for the results of the checksum collection. 
     */
    void getFileIDs(Collection<String> pillarIDs, String auditTrailInformation, ContributorQuery[] queries, 
            EventHandler eventHandler);

    /**
     * Request the specified checksums for from the given the pillars.
     * @param pillarIDs The collection of ids of the pillars to request for the checksums.
     * @param checksumType The checksum algorithm (and salt) used for the calculation. 
     * May be null, in which case the collection default is used.
     * @param auditTrailInformation The audit trail information for the conversation.
     * @param queries The limiting contributor queries for the collection of file ids. 
     * @param eventHandler The eventhandler for the results of the checksum collection. 
     */
    void getChecksums(Collection<String> pillarIDs, ChecksumSpecTYPE checksumType, String auditTrailInformation, 
            ContributorQuery[] queries, EventHandler eventHandler);
}
