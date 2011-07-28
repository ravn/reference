/*
 * #%L
 * Bitrepository Common
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
package org.bitrepository.protocol.bitrepositorycollection;

import org.bitrepository.protocol.configuration.MessageBusConfigurations;

/** Defines the global settings for a <code>BitRepositoryCollection</code>. 
 * 
 * See <a {@link https://sbforge.org/display/BITMAG/BitRepositoryCollection} for details.*/
public interface BitRepositoryCollectionSettings {

    /**
     * Returns the ID for this BitRepositoryCollection
     */
    public String getBitRepositoryCollectionID();
    
    /** Returns the definitions for the messagebus for this <code>BitRepositoryCollection</code>. */
    public MessageBusConfigurations getMessageBusConfiguration();

    /**
     * Return the ID for the topic used for messages with are broadcasted to all participants for a given SLA. 
     * 
     * See <a href="https://sbforge.org/display/BITMAG/Queues+and+topics#Queuesandtopics-TheperSLAtopic">The per-SLA topic,/a> for details.
     * @return
     */
    public String getBitRepositoryCollectionTopicID();

}
