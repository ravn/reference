/*
 * #%L
 * Bitrepository Protocol
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
package org.bitrepository.protocol.time;

import java.math.BigInteger;

import org.bitrepository.bitrepositoryelements.TimeMeasureTYPE;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TimeMeasureComparatorTest {
    @Test (groups = { "regressiontest" })
    public void testCompareMilliSeconds() {
        TimeMeasureComparator comparator = new TimeMeasureComparator();
        TimeMeasureTYPE referenceTime = new TimeMeasureTYPE();
        referenceTime.setTimeMeasureValue(new BigInteger("2"));
        referenceTime.setTimeMeasureUnit("MILLISECONDS");
        
        TimeMeasureTYPE compareTime = new TimeMeasureTYPE();
        compareTime.setTimeMeasureValue(new BigInteger("3"));
        compareTime.setTimeMeasureUnit("MILLISECONDS");
        
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) < 0, referenceTime + 
                " should be smaller than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("1"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) > 0, referenceTime + 
                " should be larger than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("2"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) == 0, referenceTime + 
                " should be same as " + compareTime);
    }
    
    @Test (groups = { "regressiontest" })
    public void testCompareMilliSecondsToSeconds() {
        TimeMeasureComparator comparator = new TimeMeasureComparator();
        TimeMeasureTYPE referenceTime = new TimeMeasureTYPE();
        referenceTime.setTimeMeasureValue(new BigInteger("2000"));
        referenceTime.setTimeMeasureUnit("MILLISECONDS");
        
        TimeMeasureTYPE compareTime = new TimeMeasureTYPE();
        compareTime.setTimeMeasureValue(new BigInteger("3"));
        compareTime.setTimeMeasureUnit("SECONDS");
        
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) < 0, referenceTime + 
                " should be smaller than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("1"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) > 0, referenceTime + 
                " should be larger than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("2"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) == 0, referenceTime + 
                " should be same as " + compareTime);
    }
    
    @Test (groups = { "regressiontest" })
    public void testCompareMilliSecondsToMinutes() {
        TimeMeasureComparator comparator = new TimeMeasureComparator();
        TimeMeasureTYPE referenceTime = new TimeMeasureTYPE();
        referenceTime.setTimeMeasureValue(new BigInteger("120000"));
        referenceTime.setTimeMeasureUnit("MILLISECONDS");
        
        TimeMeasureTYPE compareTime = new TimeMeasureTYPE();
        compareTime.setTimeMeasureValue(new BigInteger("3"));
        compareTime.setTimeMeasureUnit("MINUTES");
        
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) < 0, referenceTime + 
                " should be smaller than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("1"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) > 0, referenceTime + 
                " should be larger than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("2"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) == 0, referenceTime + 
                " should be same as " + compareTime);
    }

    @Test (groups = { "regressiontest" })
    public void testCompareMilliSecondsToHours() {
        TimeMeasureComparator comparator = new TimeMeasureComparator();
        TimeMeasureTYPE referenceTime = new TimeMeasureTYPE();
        referenceTime.setTimeMeasureValue(new BigInteger("7200000"));
        referenceTime.setTimeMeasureUnit("MILLISECONDS");
        
        TimeMeasureTYPE compareTime = new TimeMeasureTYPE();
        compareTime.setTimeMeasureValue(new BigInteger("3"));
        compareTime.setTimeMeasureUnit("HOURS");
        
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) < 0, referenceTime + 
                " should be smaller than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("1"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) > 0, referenceTime + 
                " should be larger than " + compareTime);
        
        compareTime.setTimeMeasureValue(new BigInteger("2"));
        Assert.assertTrue(comparator.compare(referenceTime, compareTime) == 0, referenceTime + 
                " should be same as " + compareTime);
    }
}