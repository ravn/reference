/*
 * #%L
 * Bitrepository Integrity Service
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
package org.bitrepository.integrityservice.workflow.step;

import java.util.List;

import org.bitrepository.integrityservice.cache.IntegrityModel;
import org.bitrepository.integrityservice.reports.IntegrityReporter;
import org.bitrepository.service.workflow.AbstractWorkFlowStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workflow step for finding missing checksums.
 * Uses the IntegrityChecker to perform the actual check.
 */
public class HandleDeletedFilesStep extends AbstractWorkFlowStep {
    /** The log.*/
    private Logger log = LoggerFactory.getLogger(getClass());
    /** The Integrity Model. */
    private final IntegrityModel store;
    /** The report model to populate */
    private final IntegrityReporter reporter;
    
    public HandleDeletedFilesStep(IntegrityModel store, IntegrityReporter reporter) {
        this.store = store;
        this.reporter = reporter;
    }
    
    @Override
    public String getName() {
        return "Handle files that's no longer in the collection.";
    }

    /**
     * Queries the IntegrityModel for 'orphan files'. Reports and removes them if any is returned.
     */
    @Override
    public synchronized void performStep() {
        List<String> deletedFiles = store.findOrphanFiles(reporter.getCollectionID());
        for(String deletedFile : deletedFiles) {
            store.deleteFileIdEntry(deletedFile, reporter.getCollectionID());
            reporter.reportDeletedFile(deletedFile);
        }
    }

    public static String getDescription() {
        return "Detects and removes files that are no longer in the collection.";
    }
}