/*
 * #%L
 * Bitrepository Protocol
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
package org.bitrepository.protocol.conversation;

import java.util.Timer;
import java.util.TimerTask;

import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.bitrepositorymessages.Message;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.common.exceptions.UnableToFinishException;
import org.bitrepository.protocol.exceptions.UnexpectedResponseException;

/**
 * Implements the generic conversation state functionality, 
 * like timeouts and the definition of the common state attributes.
 *
 * ToDo Implement ConversationState and consider moving selected method here when this class is in general usage.
 */
public abstract class AbstractConversationState {
    /** The timer used for timeout checks. */
    private final Timer timer = new Timer();
    /** The timer task for timeout of identify in this conversation. */
    private final TimerTask stateTimeoutTask = new StateTimerTask();

    public synchronized final void start() {
        timer.schedule(stateTimeoutTask, getTimeout());
        sendRequest();
    }

    public synchronized final void handleMessage(Message message) {
        if (!(message instanceof MessageResponse)) {
            getContext().getMonitor().outOfSequenceMessage("Can only handle responses, but received " +
                    message.getClass().getSimpleName());
        }
        MessageResponse response = (MessageResponse)message;
        if (!response.getResponseInfo().getResponseCode().equals(ResponseCode.IDENTIFICATION_POSITIVE)) {
            getContext().getMonitor().pillarFailed("Received negative response from component " +
                    response.getFrom() +
                    ":  " + response.getResponseInfo());
        }
        try {
            processMessage(response);
        } catch (UnexpectedResponseException e) {
            getContext().getMonitor().invalidMessage(response, e);
        }

        try {
            setNewState(getNextState());
        } catch (UnableToFinishException e) {
            getContext().getMonitor().operationFailed(e);
        }
    }

    protected void failConversation(String info) {
        getContext().getMonitor().operationFailed(info);
    }

    /**
     * The timer task class for the outstanding identify requests.
     * When the time is reached the selected pillar should
     * be called requested for the delivery of the file.
     */
    private class StateTimerTask extends TimerTask {
        @Override
        public void run() {
            synchronized(this) {
                setNewState(handleStateTimeout());
            }
        }
    }

    /**
     * Handles the change to the new state.
     * @param newState The state to change to.
     */
    private void setNewState(AbstractConversationState newState) {
        if (newState != this){
            stateTimeoutTask.cancel();
            getContext().setState(newState);
            newState.start();
        }
    }

    /**
     * Implement by concrete states for sending the request starting this state.
     */
    protected abstract void sendRequest();
    /**
     * Implement by concrete states for handling timeout for the state.
     * @throws UnexpectedResponseException The response could be processed successfully
     */
    protected abstract void processMessage(MessageResponse response) throws UnexpectedResponseException;

    /**
     * Implement by concrete states for handling timeout for the state.
     */
    protected abstract AbstractConversationState handleStateTimeout();

    /**
     * @return The conversation context used for this conversation.
     */
    protected abstract ConversationContext getContext();

    /**
     * Called to get the next state when all responses have been received. This would be: <ul>
     *     <li>The operation state if this is a identification state.</li>
     *     <li>The finished state if this is the operation state</li>
     * </ul>
     * Note that if the implementing class must also handle the sending of events in cae of a state change.
     * @return The next state after this one.
     */
    protected abstract AbstractConversationState getNextState() throws UnableToFinishException;

    protected abstract long getTimeout();

    /**
     * Informative naming of the process this state is performing. Used for logging. Examples are 'Delete files',
     * 'Identify contributers for Audit Trails'
     */
    protected abstract String getName();
}
