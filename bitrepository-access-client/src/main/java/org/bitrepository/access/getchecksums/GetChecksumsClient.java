/*
 * #%L
 * Bitrepository Access
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
package org.bitrepository.access.getchecksums;

import java.net.URL;
import java.util.Collection;
import org.bitrepository.access.ContributorQuery;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.FileIDs;
import org.bitrepository.client.BitrepositoryClient;
import org.bitrepository.client.eventhandler.EventHandler;

/**
 * Used for retrieving checksums for files stored on the collection pillars.
 */
public interface GetChecksumsClient extends BitrepositoryClient {

    /**
     * Retrieves the checksums for a set of files.
     * <br/>
     * If the number of fileIDs in a collection is large (10.000's) the fileIDs should  be retrieved in chunks by
     * using the <code>ContributorQuery</code> functionality.
     * <br/>
     * Since every pillar cannot upload their checksums to the same URL, it is extended with the pillarId for the given
     * pillar, e.g.: 'http://upload.url/mypath' + '-pillarId'.
     * <br/>
     * The results are returned through as a ChecksumsCompletePillarCompete event as the results are returned by the
     * pillars.
     *
     * @param contributorQueries Defines which fileIDs to retrieve. If null all fileIDs from all contributers are
     *                           returned. Note that
     * @param fileID The optional fileID to retrieve file information for. If <code>null</code> file information are
     *               retrieved for all files.
     * @param checksumSpec Specification of how the type of checksums. If no checksum spec is specified the default
     * checksum type will be returned.
     * @param addressForResult [OPTIONAL] The address to upload the calculated checksums to. If this is null, then the
     * results will be retrieved through the message.
     * @param eventHandler [OPTIONAL] The handler which should receive notifications of the events occurring in
     * connection with the pillar communication.
     *
     */
    public void getChecksums(
        ContributorQuery[] contributorQueries,
        String fileID,
        ChecksumSpecTYPE checksumSpec,
        URL addressForResult,
        EventHandler eventHandler,
        String auditTrailInformation);

    /**
     * Method for retrieving a checksums for a set of files from a set of pillars.
     * <br/>
     * Since every pillar cannot upload their checksums to the same URL, it is extended with the pillarId for the given
     * pillar, e.g.: 'http://upload.url/mypath' + '-pillarId'.
     * <br/>
     * The results are returned through as a ChecksumsCompletePillarCompete event as the results are returned by the
     * pillars.
     *
     * @param pillarIDs The list of IDs for the pillars, where the checksum should be retrieved from. If null, checksums
     *                  are requested from all pillars.
     * @param fileIDs Defines whether checksums should be returned for a single file or all files.
     * @param checksumSpec Specification of how the type of checksums. If no checksum spec is specified the default
     * checksum type will be returned.
     * @param addressForResult [OPTIONAL] The address to upload the calculated checksums to. If this is null, then the
     * results will be retrieved through the message.
     * @param eventHandler [OPTIONAL] The handler which should receive notifications of the events occurring in 
     * connection with the pillar communication.
     * @deprecated Use the #getChecksums(ContributorQuery[], String, ChecksumSpecTYPE, URL, EventHandler, String)
     * method instead.
     */
    @Deprecated
    public void getChecksums(Collection<String> pillarIDs, FileIDs fileIDs, ChecksumSpecTYPE checksumSpec, 
            URL addressForResult, EventHandler eventHandler, String auditTrailInformation);
    
}
