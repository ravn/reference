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
package org.bitrepository.protocol.bus;

import org.bitrepository.protocol.ProtocolComponentFactory;
import org.bitrepository.protocol.configuration.MessageBusConfiguration;
import org.bitrepository.protocol.configuration.MessageBusConfigurations;

/**
 * 
 * 
 * Consider moving definitions to disk
 */
public class MessageBusConfigurationFactory {

	private MessageBusConfigurationFactory() {}

	public static MessageBusConfigurations createDefaultConfiguration() {
		return ProtocolComponentFactory.getInstance().getProtocolConfiguration().getMessageBusConfigurations();
	}

	public static MessageBusConfigurations createEmbeddedMessageBusConfiguration() {
		MessageBusConfigurations configs2 = new MessageBusConfigurations();
		MessageBusConfiguration config2 = new MessageBusConfiguration();
		config2.setUrl("tcp://localhost:61616");
		config2.setId("Embedded-messagebus1");
		config2.setUsername("");
		config2.setPassword("");
		configs2.setPrimaryMessageBusConfiguration(config2);
		return configs2;
	}
}