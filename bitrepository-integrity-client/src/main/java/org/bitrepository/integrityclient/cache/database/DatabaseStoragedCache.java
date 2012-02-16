/*
 * #%L
 * Bitrepository Integrity Client
 * 
 * $Id$
 * $HeadURL$
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
package org.bitrepository.integrityclient.cache.database;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bitrepository.bitrepositoryelements.ChecksumDataForChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.FileIDsData;
import org.bitrepository.bitrepositoryelements.FileIDsDataItem;
import org.bitrepository.common.ArgumentValidator;

/**
 * Handles the communication with the database.
 */
public class DatabaseStoragedCache {
    /** The log.*/
    private static Log log = LogFactory.getLog(DerbyDBConnector.class);
    /** The connection to the database.*/
    private final Connection dbConnection;
    
    /** 
     * Constructor.
     * @param dbConnection The connection to the database, where the cache is stored.
     */
    public DatabaseStoragedCache(Connection dbConnection) {
        ArgumentValidator.checkNotNull(dbConnection, "Connection dbConnection");
        
        this.dbConnection = dbConnection;
    }
    
    /**
     * Inserts the results of a GetFileIDs operation for a given pillar.
     * @param data The results of the GetFileIDs operation.
     * @param pillarId The pillar, where the GetFileIDsOperation has been performed.
     */
    public void updateFileIDs(FileIDsData data, String pillarId) {
        ArgumentValidator.checkNotNull(data, "FileIDsData data");
        ArgumentValidator.checkNotNullOrEmpty(pillarId, "String pillarId");
        
        log.debug("Updating the file ids '" + data + "' for pillar '" + pillarId + "'");
        Long pillarGuid = retrievePillarGuid(pillarId);
        
        for(FileIDsDataItem dataItem : data.getFileIDsDataItems().getFileIDsDataItem()) {
            Long fileGuid = retrieveFileGuid(dataItem.getFileID());
            // TODO create calendar utils method for this
            Date modifyDate = dataItem.getLastModificationTime().toGregorianCalendar().getTime();
            
            updateFileInfoLastFileUpdateTimestamp(pillarGuid, fileGuid, modifyDate);
        }
        
        // TODO should be the oldest file list date for all the files within the pillar.
        // update the 'last file update' for this pillar.
        String sql = "UPDATE pillar SET last_file_update = ? WHERE pillar_guid = ?";
        DatabaseUtils.executeStatement(dbConnection, sql, new Date(), pillarGuid);
    }
    
    /**
     * Handles the result of a GetChecksums operation on a given pillar.
     * @param data The result data from the GetChecksums operation on the given pillar.
     * @param checksumType The type of checksum.
     * @param pillarId The id of the pillar, where the GetChecksums operation has been performed.
     */
    public void updateChecksumData(List<ChecksumDataForChecksumSpecTYPE> data, ChecksumSpecTYPE checksumType, 
            String pillarId) {
        ArgumentValidator.checkNotNullOrEmpty(data, "List<ChecksumDataForChecksumSpecTYPE data");
        ArgumentValidator.checkNotNull(checksumType, "ChecksumSpecTYPE checksumType");
        ArgumentValidator.checkNotNullOrEmpty(pillarId, "String pillarId");
        
        long pillarGuid = retrievePillarGuid(pillarId);
        long checksumGuid = retrieveChecksumSpecGuid(checksumType);
        
        for(ChecksumDataForChecksumSpecTYPE csData : data) {
            updateFileInfoWithChecksum(csData, pillarGuid, checksumGuid);
        }
        
        // TODO should be the oldest checksum date for any file within the pillar.
        // update the 'last checksum update' for this pillar.
        String sql = "UPDATE pillar SET last_checksum_update = ? WHERE pillar_guid = ?";
        DatabaseUtils.executeStatement(dbConnection, sql, new Date(), pillarGuid);
    }
    
    /**
     * @return The list of all the file ids within the database.
     */
    public List<String> getAllFileIDs() {
        String sql = "SELECT file_id FROM files";
        return DatabaseUtils.selectStringList(dbConnection, sql, new Object[0]);
    }
    
    /**
     * Retrieves the number of files in the given pillar, which has the file state 'EXISTING'.
     * @param pillarId The id of the pillar.
     * @return The number of files with file state 'EXISTING' for the given pillar.
     */
    public int getNumberOfExistingFilesForAPillar(String pillarId) {
        Long pillarGuid = retrievePillarGuid(pillarId);
        String sql = "SELECT COUNT(*) FROM fileinfo WHERE pillar_guid = ? AND file_state = ?";
        return DatabaseUtils.selectIntValue(dbConnection, sql, pillarGuid, FileState.EXISTING);
    }
    
    /**
     * Retrieves the number of files in the given pillar, which has the file state 'MISSING'.
     * @param pillarId The id of the pillar.
     * @return The number of files with file state 'MISSING' for the given pillar.
     */
    public int getNumberOfMissingFilesForAPillar(String pillarId) {
        Long pillarGuid = retrievePillarGuid(pillarId);
        String sql = "SELECT COUNT(*) FROM fileinfo WHERE pillar_guid = ? AND file_state = ?";
        return DatabaseUtils.selectIntValue(dbConnection, sql, pillarGuid, FileState.MISSING);
    }
    
    /**
     * @param pillarId The pillar.
     * @return The latest file list update for the given pillar.
     */
    public Date getLastFileListUpdate(String pillarId) {
        String sql = "SELECT last_file_update FROM pillar WHERE pillar_id = ?";
        return DatabaseUtils.selectDateValue(dbConnection, sql, pillarId);
    }

    /**
     * Retrieves the number of files in the given pillar, which has the checksum state 'INCONSISTENT'.
     * @param pillarId The id of the pillar.
     * @return The number of files with checksum state 'INCONSISTENT' for the given pillar.
     */
    public int getNumberOfChecksumErrorsForAPillar(String pillarId) {
        Long pillarGuid = retrievePillarGuid(pillarId);
        String sql = "SELECT COUNT(*) FROM fileinfo WHERE pillar_guid = ? AND checksum_state = ?";
        return DatabaseUtils.selectIntValue(dbConnection, sql, pillarGuid, ChecksumState.INCONSISTENT);
    }

    /**
     * @param pillarId The pillar.
     * @return The latest checksum update for the given pillar.
     */
    public Date getLastChecksumUpdate(String pillarId) {
        String sql = "SELECT last_checksum_update FROM pillar WHERE pillar_id = ?";
        return DatabaseUtils.selectDateValue(dbConnection, sql, pillarId);
    }

    /**
     * Updates or creates the given timestamp for the latest modified date of the given file on the given pillar.
     * @param pillarGuid The guid for the pillar.
     * @param fileGuid The guid for the file.
     * @param filelistTimestamp The timestamp for when the file was latest modified.
     */
    private void updateFileInfoLastFileUpdateTimestamp(long pillarGuid, long fileGuid, Date filelistTimestamp) {
        String retrievalSql = "SELECT guid FROM fileinfo WHERE pillar_guid = ? AND file_guid = ?";
        Long guid = DatabaseUtils.selectLongValue(dbConnection, retrievalSql, pillarGuid, fileGuid);
        
        // if guid is null, then make new entry. Otherwise validate / update.
        if(guid == null) {
            String insertSql = "INSERT INTO fileinfo ( pillar_guid, file_guid, last_file_update, file_state, "
                    + "checksum_state) VALUES ( ?, ?, ?, ?, ? )";
            DatabaseUtils.executeStatement(dbConnection, insertSql, pillarGuid, fileGuid, filelistTimestamp,
                    FileState.EXISTING.ordinal(), ChecksumState.UNKNOWN.ordinal());
        } else {
            String validateSql = "SELECT last_file_update FROM fileinfo WHERE guid = ?";
            Date existingDate = DatabaseUtils.selectDateValue(dbConnection, validateSql, guid);
            
            // Only insert the date, if it is newer than the recorded one.
            if(existingDate == null || existingDate.getTime() < filelistTimestamp.getTime()) {
                String updateSql = "UPDATE fileinfo SET last_file_update = ? WHERE guid = ?";
                DatabaseUtils.executeStatement(dbConnection, updateSql, filelistTimestamp, guid);
            }
        }
    }
    
    /**
     * Updates an entry in the FileInfo table with the results of a GetChecksums operation of a single file.
     * @param data The result of the GetChecksums operation.
     * @param pillarGuid The guid of the pillar.
     * @param checksumGuid The guid of the checksum.
     */
    private void updateFileInfoWithChecksum(ChecksumDataForChecksumSpecTYPE data, long pillarGuid, long checksumGuid) {
        // TODO make CalendarUtils function for this.
        Date csTimestamp = data.getCalculationTimestamp().toGregorianCalendar().getTime();
        String checksum = new String(data.getChecksumValue());
        Long fileGuid = retrieveFileGuid(data.getFileID());

        // TODO what if there already is a entry, but with a different checksum?
        String retrievalSql = "SELECT guid FROM fileinfo WHERE pillar_guid = ? AND file_guid = ?";
        Long guid = DatabaseUtils.selectLongValue(dbConnection, retrievalSql, pillarGuid, fileGuid);
        
        // if guid is null, then make new entry. Otherwise validate / update.
        if(guid == null) {
            String insertSql = "INSERT INTO fileinfo ( pillar_guid, file_guid, checksum_guid, last_checksum_update, "
                    + "checksum, file_state, checksum_state ) VALUES ( ?, ?, ?, ?, ? )";
            DatabaseUtils.executeStatement(dbConnection, insertSql, pillarGuid, fileGuid, checksumGuid, csTimestamp,
                    checksum, FileState.EXISTING.ordinal(), ChecksumState.UNKNOWN.ordinal());
        } else {
            String validateSql = "SELECT last_checksum_update FROM fileinfo WHERE guid = ?";
            Date existingDate = DatabaseUtils.selectDateValue(dbConnection, validateSql, guid);
            
            // Only update, if it has a newer checksum timestamp than the recorded one.
            if(existingDate == null || existingDate.getTime() < csTimestamp.getTime()) {
                String updateSql = "UPDATE fileinfo SET checksum_guid = ?, last_checksum_update = ?, checksum = ?, "
                        + "file_state = ?, checksum_state = ? WHERE guid = ?";
                DatabaseUtils.executeStatement(dbConnection, updateSql, checksumGuid, csTimestamp, checksum, 
                        FileState.EXISTING.ordinal(), ChecksumState.UNKNOWN.ordinal(), guid);
            }
        }
    }
    
    /**
     * Retrieves the guid corresponding to a given file id. If no such entry exists, then it is created.
     * @param fileId The id of the file to retrieve the guid of.
     * @return The guid of the file with the given id.
     */
    private long retrieveFileGuid(String fileId) {
        ArgumentValidator.checkNotNullOrEmpty(fileId, "String fileId");
        if(!containsFileID(fileId)) {
            insertFileID(fileId);
        }
        
        String sql = "SELECT file_guid FROM files WHERE file_id = ?";
        return DatabaseUtils.selectLongValue(dbConnection, sql, fileId);
    }
    
    /**
     * Inserts a new file id into the 'files' table in the database.
     * @param fileId The id of the file to insert.
     */
    private void insertFileID(String fileId) {
        String sql = "INSERT INTO files ( file_id, creation_date ) VALUES ( ?, ? )";
        DatabaseUtils.executeStatement(dbConnection, sql, fileId, new Date());
    }
    
    /**
     * @param fileId The id of the file, which existence in the 'files' table has to be determined.
     * @return Whether an entry with the given id exist within the 'files' table.
     */
    private boolean containsFileID(String fileId) {
        // Retrieve the amount of times the file id is within 'files' table.
        String sql = "SELECT COUNT(*) FROM files WHERE file_id = ?";
        int count = DatabaseUtils.selectIntValue(dbConnection, sql, fileId);

        // Handle the different cases for count.
        switch (count) {
        case 0:
            return false;
        case 1:
            return true;
            default:
                throw new IllegalStateException("Found more than 1");
        }    
    }
    
    /**
     * Retrieves the guid corresponding to a given pillar id. If no such entry exists, then it is created.
     * @param pillarId The id of the pillar to retrieve the guid of.
     * @return The guid of the pillar with the given id.
     */
    private long retrievePillarGuid(String pillarId) {
        ArgumentValidator.checkNotNullOrEmpty(pillarId, "String pillarId");
        if(!containsPillarID(pillarId)) {
            insertPillarID(pillarId);
        }
        
        String sql = "SELECT pillar_guid FROM pillar WHERE pillar_id = ?";
        return DatabaseUtils.selectLongValue(dbConnection, sql, pillarId);
    }
    
    /**
     * Creates a new entry in the 'pillar' table for the given pillar id.
     * @param pillarId The id of the pillar which are to be inserted into the 'pillar' table.
     */
    private void insertPillarID(String pillarId) {
        String sql = "INSERT INTO pillar ( pillar_id ) VALUES ( ? )";
        DatabaseUtils.executeStatement(dbConnection, sql, pillarId);
    }
    
    /**
     * @param pillarId The id of the pillar, which presence in the 'pillar' table should be determined.
     * @return Whether the 'pillar' table contains the given pillar id.
     */
    private boolean containsPillarID(String pillarId) {
        // Retrieve the amount of times the pillar id is within 'pillar' table.
        String sql = "SELECT COUNT(*) FROM pillar WHERE pillar_id = ?";
        int count = DatabaseUtils.selectIntValue(dbConnection, sql, pillarId);

        // Handle the different cases for count.
        switch (count) {
        case 0:
            return false;
        case 1:
            return true;
            default:
                throw new IllegalStateException("Found more than 1");
        }    
    }
    
    /**
     * Retrieves the guid corresponding to a given checksum specification. If no such entry exists, then it is created.
     * @param fileId The checksum specification to retrieve the guid of.
     * @return The guid of the give checksum specification.
     */
    private long retrieveChecksumSpecGuid(ChecksumSpecTYPE checksumType) {
        ArgumentValidator.checkNotNull(checksumType, "ChecksumSpecTYPE checksumType");
        if(!containsChecksumSpec(checksumType)) {
            insertChecksumSpec(checksumType);
        }
        
        String sql = "SELECT checksum_guid FROM checksumspec WHERE checksum_algorithm = ? AND checksum_salt = ?";
        return DatabaseUtils.selectLongValue(dbConnection, sql, checksumType.getChecksumType(), 
                new String(checksumType.getChecksumSalt()));
    }
    
    /**
     * Inserts a new entry in the 'checksum' table for the given checksum specification.
     * @param checksumType The checksum specification to insert into the 'checksum' table.
     */
    private void insertChecksumSpec(ChecksumSpecTYPE checksumType) {
        String sql = "INSERT INTO checksumspec ( checksum_algorithm, checksum_salt ) VALUES ( ?, ? )";
        DatabaseUtils.executeStatement(dbConnection, sql, checksumType.getChecksumType(),
                new String(checksumType.getChecksumSalt()));
    }
    
    /**
     * @param checksumType The checksum specification to be determined whether it exists within the 'checksum' table.
     * @return Whether the given checksum specification is within the 'checksum' table.
     */
    private boolean containsChecksumSpec(ChecksumSpecTYPE checksumType) {
        // Retrieve the amount of times the checksum type and checksum salt is within 'checksumspec' table.
        String sql = "SELECT COUNT(*) FROM checksumspec WHERE checksum_algorithm = ? AND checksum_salt = ?";
        int count = DatabaseUtils.selectIntValue(dbConnection, sql, checksumType.getChecksumType(),
                new String(checksumType.getChecksumSalt()));

        // Handle the different cases for count.
        switch (count) {
        case 0:
            return false;
        case 1:
            return true;
            default:
                throw new IllegalStateException("Found more than 1 entry for '" + checksumType + "'");
        }    
    }
}