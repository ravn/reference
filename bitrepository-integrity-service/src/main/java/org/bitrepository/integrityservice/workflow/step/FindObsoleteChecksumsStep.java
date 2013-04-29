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

import org.bitrepository.common.settings.Settings;
import org.bitrepository.common.utils.TimeUtils;
import org.bitrepository.integrityservice.alerter.IntegrityAlerter;
import org.bitrepository.integrityservice.checking.IntegrityChecker;
import org.bitrepository.integrityservice.checking.MaxChecksumAgeProvider;
import org.bitrepository.integrityservice.checking.reports.IntegrityReportModel;
import org.bitrepository.service.workflow.AbstractWorkFlowStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workflow step for finding obsolete checksums.
 * Sends an alarm if any checksums are too old.
 */
public class FindObsoleteChecksumsStep extends AbstractWorkFlowStep {
    /** The log.*/
    private Logger log = LoggerFactory.getLogger(getClass());
    /** Checker for performing the integrity checks.*/
    private final IntegrityChecker checker;
    /** The dispatcher of alarms.*/
    private final IntegrityAlerter dispatcher;
    private final Settings settings;
    /** The id of the collection to find obsolete checksums in */
    private final String collectionId;
    /** A year */
    public static final long DEFAULT_MAX_CHECKSUM_AGE = TimeUtils.MS_PER_YEAR;
    
    /**
     * @param settings Defines the intervals for a checksum timestamp to timeout and become obsolete pr. pillar.
     * @param checker The concrete checker to use for finding the obsolete checksums.
     * @param alarmDispatcher Used for alarm dispatching.
     */
    public FindObsoleteChecksumsStep(Settings settings, IntegrityChecker checker, IntegrityAlerter alarmDispatcher,
            String collectionId) {
        this.checker = checker;
        this.settings = settings;
        this.dispatcher = alarmDispatcher;
        this.collectionId = collectionId;
    }
    
    @Override
    public String getName() {
        return "Find obsolete checksums";
    }

    /**
     * Goes through all the file ids in the database and extract their respective fileinfos.
     * Then it goes through all the file infos to validate that the timestamp for the checksum calculation.
     */
    @Override
    public synchronized void performStep() {
        MaxChecksumAgeProvider maxChecksumAgeProvider = new MaxChecksumAgeProvider(
            DEFAULT_MAX_CHECKSUM_AGE,
            settings.getReferenceSettings().getIntegrityServiceSettings().getObsoleteChecksumSettings());
        IntegrityReportModel report = checker.checkObsoleteChecksums(
            maxChecksumAgeProvider,
            getPillarIDs(collectionId), collectionId);
        
        if(!report.hasIntegrityIssues()) {
            log.debug("No osolete checksum found.");
        } else {
            log.trace("Found the following obsolete checksum issues:\n" + report.generateReport());
            dispatcher.integrityFailed(report);
        }
    }

    public static String getDescription() {
        return "Finds all the checksum with timestamp older that the configured 'MaxChecksumAge'";
    }
    
    private List<String> getPillarIDs(String collectionId) {
        List<String> pillars = null;
        for(org.bitrepository.settings.repositorysettings.Collection c : settings.getRepositorySettings().getCollections().getCollection()) {
            if(c.getID().equals(collectionId)) {
                pillars = c.getPillarIDs().getPillarID();
                break;
            }
        }
        return pillars;
    }
}
