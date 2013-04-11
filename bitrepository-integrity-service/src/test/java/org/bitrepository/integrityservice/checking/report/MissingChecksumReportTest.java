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
package org.bitrepository.integrityservice.checking.report;

import java.util.Arrays;

import org.bitrepository.integrityservice.checking.reports.MissingChecksumReportModel;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MissingChecksumReportTest extends ExtendedTestCase {
    
    public static final String TEST_PILLAR_1 = "test-pillar-1";
    
    public static final String TEST_FILE_1 = "test-file-1";

    public static final String TEST_COLLECTION = "collection1";
    
    @Test(groups = {"regressiontest", "integritytest"})
    public void testEmptyMissingChecksumReport() {
        addDescription("Tests the empty missing checksum report.");
        MissingChecksumReportModel report = new MissingChecksumReportModel(TEST_COLLECTION);
        Assert.assertFalse(report.hasIntegrityIssues(), report.generateReport());
        Assert.assertEquals(report.getMissingChecksums().size(), 0);
    }

    @Test(groups = {"regressiontest", "integritytest"})
    public void testMissingChecksumReportAtPillar() {
        addDescription("Tests missing checksum report when checksum is missing at a pillar.");
        MissingChecksumReportModel report = new MissingChecksumReportModel(TEST_COLLECTION);
        report.reportMissingChecksum(TEST_FILE_1, Arrays.asList(TEST_PILLAR_1));
        Assert.assertTrue(report.hasIntegrityIssues(), report.generateReport());
        Assert.assertEquals(report.getMissingChecksums().size(), 1);
        Assert.assertEquals(report.getMissingChecksums().get(0).getFileId(), TEST_FILE_1);
        Assert.assertEquals(report.getMissingChecksums().get(0).getPillarIds(), Arrays.asList(TEST_PILLAR_1));        
    }
}
