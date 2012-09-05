/*
 * #%L
 * Bitrepository Reference Pillar
 * %%
 * Copyright (C) 2010 - 2012 The State and University Library, The Royal Library and The State Archives, Denmark
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
package org.bitrepository.pillar.referencepillar.archive;

import java.io.File;
import java.util.Date;

import org.bitrepository.bitrepositoryelements.ChecksumDataForChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumDataForFileTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.common.utils.Base16Utils;
import org.bitrepository.common.utils.CalendarUtils;
import org.bitrepository.common.utils.ChecksumUtils;
import org.bitrepository.pillar.cache.ChecksumEntry;
import org.bitrepository.pillar.cache.ChecksumStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The full ReferencePillar managing of the checksum store with respect to the ReferenceArchive.
 * 
 */
public class ReferenceChecksumManager {
    /** The log.*/
    private Logger log = LoggerFactory.getLogger(getClass());

    /** The storage of checksums.*/
    private final ChecksumStore cache;
    /** The archive with the files.*/
    private final ReferenceArchive archive;
    /** The maximum age for a checksum. Measured in milliseconds.*/
    private final long maxAgeForChecksums;
    /** The default checksum specification.*/
    private final ChecksumSpecTYPE defaultChecksumSpec;
    
    /**
     * Constructor.
     * @param archive The archive with the data.
     * @param cache The storage for the checksums.
     * @param defaultChecksumSpec The default specifications for the checksums.
     * @param maxAgeForChecksum The maximum age for the checksums.
     */
    public ReferenceChecksumManager(ReferenceArchive archive, ChecksumStore cache, 
            ChecksumSpecTYPE defaultChecksumSpec, long maxAgeForChecksum) {
        this.cache = cache;
        this.archive = archive;
        this.maxAgeForChecksums = maxAgeForChecksum;
        this.defaultChecksumSpec = defaultChecksumSpec;
    }
    
    /**
     * Retrieves the entry for the checksum for a given file with the given checksum specification.
     * If it is the default checksum specification, then the cached checksum is returned (though it is recalculated 
     * if it is too old).
     * A different checksum specification will cause the default checksum to be recalculated for the file and updated 
     * in the database, along with the calculation of the new checksum specification which will be returned. 
     * 
     * @param fileId The id of the file whose checksum is requested.
     * @param csType The type of checksum.
     * @return The entry for the requested type of checksum for the given file.
     */
    public ChecksumEntry getChecksumEntryForFile(String fileId, ChecksumSpecTYPE csType) {
        if(csType.equals(defaultChecksumSpec)) {
            log.trace("Default checksum specification: {}.", csType);
            ensureChecksumState(fileId);
            return cache.getEntry(fileId);
        } else {
            log.trace("Non-default checksum specification: {}. Recalculating the checksums.", csType);
            recalculateChecksum(fileId);
            
            File file = archive.getFile(fileId);
            String checksum = ChecksumUtils.generateChecksum(file, csType);
            return new ChecksumEntry(fileId, checksum, new Date());
        }
    }
    
    /**
     * Retrieves the checksum for a given file with the given checksum specification.
     * If it is the default checksum specification, then the cached checksum is returned (though it is recalculated 
     * if it is too old).
     * A different checksum specification will cause the default checksum to be recalculated for the file and updated 
     * in the database, along with the calculation of the new checksum specification which will be returned. 
     * 
     * @param fileId The id of the file whose checksum is requested.
     * @param csType The type of checksum.
     * @return The requested type of checksum for the given file.
     */
    public String getChecksumForFile(String fileId, ChecksumSpecTYPE csType) {
        if(csType == defaultChecksumSpec) {
            ensureChecksumState(fileId);
            return cache.getChecksum(fileId);            
        } else {
            recalculateChecksum(fileId);
            
            File file = archive.getFile(fileId);
            return ChecksumUtils.generateChecksum(file, csType);
        }
    }
    
    /**
     * Recalculates the checksum of a given file based on the default checksum specification.
     * @param fileId The id of the file to recalculate its default checksum for.
     */
    public void recalculateChecksum(String fileId) {
        File file = archive.getFile(fileId);
        String checksum = ChecksumUtils.generateChecksum(file, defaultChecksumSpec);
        cache.insertChecksumCalculation(fileId, checksum, new Date());
    }
    
    /**
     * Removes the entry for the given file.
     * @param fileId The id of the file to remove.
     */
    public void deleteEntry(String fileId) {
        cache.deleteEntry(fileId);
    }
    
    /**
     * Calculates the checksum of a file within the tmpDir.
     * @param fileId The id of the file to calculate the checksum for.
     * @param csType The specification for the type of checksum to calculate.
     * @return The checksum of the given type for the file with the given id.
     */
    public String getChecksumForTempFile(String fileId, ChecksumSpecTYPE csType) {
        File file = archive.getFileInTmpDir(fileId);
        return ChecksumUtils.generateChecksum(file, csType);
    }
    
    /**
     * Retrieves the entry for a given file with a given checksumSpec in the ChecksumDataForFileTYPE format.
     * @param fileId The id of the file to retrieve the data from.
     * @param csType The type of checksum to calculate.
     * @return The entry encapsulated in the ChecksumDataForFileTYPE data format.
     */
    public ChecksumDataForFileTYPE getChecksumDataForFile(String fileId, ChecksumSpecTYPE csType) {
        ChecksumEntry entry = getChecksumEntryForFile(fileId, csType);
        ChecksumDataForFileTYPE res = new ChecksumDataForFileTYPE();
        res.setCalculationTimestamp(CalendarUtils.getXmlGregorianCalendar(entry.getCalculationDate()));
        res.setChecksumSpec(csType);
        res.setChecksumValue(Base16Utils.encodeBase16(entry.getChecksum()));
        return res;
    }
    
    /**
     * Retrieves the entry for a given file with a given checksumSpec in the ChecksumDataForChecksumSpecTYPE format.
     * @param fileId The id of the file to retrieve the data from.
     * @param csType The type of checksum to calculate.
     * @return The entry encapsulated in the ChecksumDataForChecksumSpecTYPE data format.
     */    
    public ChecksumDataForChecksumSpecTYPE getChecksumDataForChecksumSpec(String fileId, ChecksumSpecTYPE csType) {
        ChecksumEntry csEntry = getChecksumEntryForFile(fileId, csType);
        ChecksumDataForChecksumSpecTYPE res = new ChecksumDataForChecksumSpecTYPE();
        res.setCalculationTimestamp(CalendarUtils.getXmlGregorianCalendar(csEntry.getCalculationDate()));
        res.setFileID(csEntry.getFileId());
        res.setChecksumValue(Base16Utils.encodeBase16(csEntry.getChecksum()));
        
        return res;
    }
    
    /**
     * Ensures that the cache has an non-deprecated checksum for the given file.
     * @param fileId The id of the file.
     */
    private void ensureChecksumState(String fileId) {
        if(!cache.hasFile(fileId)) {
            log.debug("No checksum cached for file '" + fileId + "'. Calculating the checksum.");
            recalculateChecksum(fileId);
        } else {
            long minDateForChecksum = System.currentTimeMillis() - maxAgeForChecksums;
            if(cache.getCalculationDate(fileId).getTime() < minDateForChecksum) {
                log.info("The checksum for the file '" + fileId + "' is too old. Recalculating.");                
                recalculateChecksum(fileId);
            }
        }
    }
}