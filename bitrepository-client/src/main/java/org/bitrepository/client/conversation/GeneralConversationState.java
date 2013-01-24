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
package org.bitrepository.client.conversation;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import org.bitrepository.bitrepositorymessages.Message;
import org.bitrepository.bitrepositorymessages.MessageRequest;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.client.conversation.selector.ContributorResponseStatus;
import org.bitrepository.client.exceptions.UnexpectedResponseException;
import org.bitrepository.common.exceptions.UnableToFinishException;
import org.bitrepository.protocol.ProtocolVersionLoader;

/**
 * Implements the generic conversation state functionality, 
 * like timeouts and the definition of the common state attributes.
 */
public abstract class GeneralConversationState implements ConversationState {
    /** Defines that the timer is a daemon thread. */
    private static final Boolean TIMER_IS_DAEMON = true;
    /** The name of the timer.*/
    private static final String NAME_OF_TIMER = "Timer for the general conversation state";
    /** The timer used for timeout checks. */
    private static final Timer timer = new Timer(NAME_OF_TIMER, TIMER_IS_DAEMON);
    /** The timer task for timeout of identify in this conversation. */
    private final TimerTask stateTimeoutTask = new StateTimerTask();
    /** For response bookkeeping */
    private final ContributorResponseStatus responseStatus;

    /**
     *
     * @param expectedContributors The collection of components to monitor responses from. This conversation
     *                             phase is considered finished when all contributors have responded.
     */
    protected GeneralConversationState(Collection<String> expectedContributors) {
        responseStatus = new ContributorResponseStatus(expectedContributors);
    }

    /**
     * Startes the state by: <ol>
     *     <li>Starting the timeout timer.</li>
     *     <li>Sends the request which triggers the responses for this state.</li>
     * </ol>
     */
    public void start() {
        if (!responseStatus.getOutstandComponents().isEmpty()) {
            timer.schedule(stateTimeoutTask, getTimeoutValue());
            sendRequest();
        } else {
            // No contributors need to be called for the operation to finish.
            changeState();
        }
    }

    /**
     * The general message handler for this state. Will only accept <code>MessageResponses</code>.
     * Takes care of the general message bookkeeping and delegates the specifics of the message handling to the
     * concrete states {@link #processMessage(MessageResponse)}.
     * @param message The message to handle.
     */
    public final void handleMessage(Message message) {
        if (!(message instanceof MessageResponse)) {
            getContext().getMonitor().warning("Unable to handle none-response type message " + message);
            return;
        }
        MessageResponse response = (MessageResponse)message;

        if (!canHandleResponseType(response)) {
            getContext().getMonitor().outOfSequenceMessage(response);
            return;
        }
        if (!responseStatus.getComponentsWhichShouldRespond().contains(message.getFrom())) {
            getContext().getMonitor().debug("Ignoring message from irrelevant component " + response.getFrom());
            return;
        }

        try {
            if(processMessage(response)) {
                responseStatus.responseReceived(response);
                if (responseStatus.haveAllComponentsResponded()) {
                    stateTimeoutTask.cancel();
                    changeState();
                }
            }
        } catch (UnexpectedResponseException e) {
            getContext().getMonitor().invalidMessage(message, e);
        } catch (UnableToFinishException e) {
            failConversation(e.getMessage());
        }
    }

    /**
     * The timer task class for the outstanding identify requests.
     * When the time is reached the selected pillar should
     * be called requested for the delivery of the file.
     */
    private class StateTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                logStateTimeout();
                changeState();
            } catch (UnableToFinishException e) {
                failConversation(e.getMessage());
            }
        }
    }

    /**
     * Changes to the next state.
     */
    private void changeState() {
        try {
            GeneralConversationState nextState = completeState();
            getContext().setState(nextState);
            nextState.start();
        } catch (UnableToFinishException e) {
            failConversation(e.getMessage());
        }
    }

    private void failConversation(String message) {
        stateTimeoutTask.cancel();
        getContext().getMonitor().operationFailed(message);
        getContext().setState(new FinishedState(getContext()));
    }

    protected void initializeMessage(MessageRequest msg) {
        msg.setCorrelationID(getContext().getConversationID());
        msg.setMinVersion(ProtocolVersionLoader.loadProtocolVersion().getMinVersion());
        msg.setVersion(ProtocolVersionLoader.loadProtocolVersion().getVersion());
        msg.setCollectionID(getContext().getSettings().getCollectionID());
        msg.setReplyTo(getContext().getSettings().getReceiverDestinationID());
        msg.setAuditTrailInformation(getContext().getAuditTrailInformation());
        msg.setFrom(getContext().getClientID());
    }

    /** Returns a list of components where a identify response hasn't been received. */
    protected Collection<String> getOutstandingComponents() {
        return responseStatus.getOutstandComponents();
    }

    /** Must be implemented by subclasses to log informative timeout information */
    protected abstract void logStateTimeout() throws UnableToFinishException ;

    /**
     * Implement by concrete states for sending the request starting this state.
     */
    protected abstract void sendRequest();

    /**
     * Implement by concrete states. Only messages from the indicated contributors and with the right type
     * will be delegate to this method.
     * @return boolean Return true if response should be considered a final response, false if not. 
     *      This is intended for use when a failure response results in a retry, so the component is not finished. 
     * @throws UnexpectedResponseException The response could not be processed successfully.
     */
    protected abstract boolean processMessage(MessageResponse response)
            throws UnexpectedResponseException, UnableToFinishException;

    /**
     * @return The conversation context used for this conversation.
     */
    protected abstract ConversationContext getContext();

    /**
     * Completes the state by generating any state/primitive finish events, create the following state and
     * return it.
     * @return An instance of the state following this state. Will be called if the moveToNextState
     * has returned true.
     * @exception UnableToFinishException Thrown in case that it is impossible to create a valid ned state.
     */
    protected abstract GeneralConversationState completeState() throws UnableToFinishException;

    /**
     * Gives access to the concrete timeout for the state.
     */
    protected abstract long getTimeoutValue();

    /**
     * Informative naming of the process this state is performing. Used for logging. Examples are 'Delete files',
     * 'Identify contributers for Audit Trails'
     */
    protected abstract String getPrimitiveName();

    /**
     * Implemented by concrete classes to indicate whether the state expects responses of this type.
     */
    private boolean canHandleResponseType(MessageResponse response) {
        String responseType = response.getClass().getSimpleName();
        return responseType.contains(getPrimitiveName());
    }
}
