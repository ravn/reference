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
package org.bitrepository.clienttest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bitrepository.protocol.eventhandler.EventHandler;
import org.bitrepository.protocol.eventhandler.OperationEvent;
import org.jaccept.TestEventManager;

public class TestEventHandler implements EventHandler {

	private final TestEventManager testEventManager;
	private final BlockingQueue<OperationEvent<?>> eventQueue = new LinkedBlockingQueue<OperationEvent<?>>();

	/** The default time to wait for events */
	private static final long DEFAULT_WAIT_SECONDS = 3;  

	public TestEventHandler(TestEventManager testEventManager) {
		super();
		this.testEventManager = testEventManager;
	}

	@Override
	public void handleEvent(OperationEvent event) {
		eventQueue.add(event);
	}

	/**
	 * Wait for an event for the DEFAULT_WAIT_SECONDS amaount of time.
	 * @return The next event if any, else null 
	 */
	public OperationEvent<?> waitForEvent() throws InterruptedException {
		return waitForEvent(DEFAULT_WAIT_SECONDS, TimeUnit.SECONDS);
	}

	public OperationEvent<?> waitForEvent(long timeout, TimeUnit unit) throws InterruptedException {
		return eventQueue.poll(timeout, unit);
	}
}