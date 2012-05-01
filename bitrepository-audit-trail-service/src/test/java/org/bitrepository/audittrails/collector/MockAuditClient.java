/*
 * #%L
 * Bitrepository Audit Trail Service
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
package org.bitrepository.audittrails.collector;

import org.bitrepository.access.getaudittrails.AuditTrailQuery;
import org.bitrepository.access.getaudittrails.client.AuditTrailClient;
import org.bitrepository.client.eventhandler.EventHandler;

public class MockAuditClient implements AuditTrailClient {
    
    @Override
    public void shutdown() {}
    
    private EventHandler latestEventHandler = null;
    public EventHandler getLatestEventHandler() {
        return latestEventHandler;
    }
    
    private int callsToGetAuditTrails = 0;
    @Override
    public void getAuditTrails(AuditTrailQuery[] componentQueries, String fileID, String urlForResult,
            EventHandler eventHandler, String auditTrailInformation) {
        latestEventHandler = eventHandler;
        callsToGetAuditTrails++;
    }
    public int getCallsToGetAuditTrails() {
        return callsToGetAuditTrails;
    }
    
}
