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
package org.bitrepository.integrityservice.mocks;

import org.bitrepository.bitrepositoryelements.Alarm;
import org.bitrepository.integrityservice.AlarmDispatcher;
import org.bitrepository.integrityservice.checking.IntegrityReport;

public class MockAlarmDispatcher implements AlarmDispatcher {
    
    private int callsForIntegrityFailed = 0;
    @Override
    public void integrityFailed(IntegrityReport report) {
        callsForIntegrityFailed++;
    }
    public int getCallsForIntegrityFailed() {
        return callsForIntegrityFailed;
    }
    
    private int callsForSendAlarm = 0;
    @Override
    public void sendAlarm(Alarm alarm) {
        callsForSendAlarm++;
    }
    public int getCallsForSendAlarm() {
        return callsForSendAlarm;
    }
    
}
