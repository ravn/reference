/*
 * #%L
 * Bitrepository Access
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
package org.bitrepository.access.getstatus.conversation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bitrepository.bitrepositorymessages.GetStatusFinalResponse;
import org.bitrepository.bitrepositorymessages.GetStatusRequest;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.client.conversation.ConversationContext;
import org.bitrepository.client.conversation.PerformingOperationState;
import org.bitrepository.client.conversation.selector.ContributorResponseStatus;
import org.bitrepository.client.conversation.selector.SelectedComponentInfo;
import org.bitrepository.client.exceptions.UnexpectedResponseException;

public class GettingStatus extends PerformingOperationState {
    private final GetStatusConversationContext context;
    private Map<String,String> activeContributers;
    /** Tracks who have responded */
    private final ContributorResponseStatus responseStatus;  
    
    public GettingStatus(GetStatusConversationContext context, List<SelectedComponentInfo> contributors) {
        super();
        this.context = context;
        this.activeContributers = new HashMap<String,String>();
        for (SelectedComponentInfo contributorInfo : contributors) {
            activeContributers.put(contributorInfo.getID(), contributorInfo.getDestination());
        }

        this.responseStatus = new ContributorResponseStatus(activeContributers.keySet());
    }

    @Override
    protected void generateContributorCompleteEvent(MessageResponse msg) throws UnexpectedResponseException {
        if (msg instanceof GetStatusFinalResponse) {
            GetStatusFinalResponse response = (GetStatusFinalResponse) msg;
            getContext().getMonitor().contributorComplete(
                    new StatusCompleteContributorEvent(
                            "Received status result from " + response.getContributor(), 
                            response.getContributor(), response.getResultingStatus(), 
                            getContext().getConversationID()));
         } else {
            throw new UnexpectedResponseException("Received unexpected msg " + msg.getClass().getSimpleName() +
                    " while waiting for Get Status response.");
        }        
    }

    @Override
    protected ContributorResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    protected void sendRequest() {
        GetStatusRequest request = new GetStatusRequest();
        initializeMessage(request);

        context.getMonitor().requestSent("Sending GetStatusRequest", activeContributers.keySet().toString());
        for(String ID : activeContributers.keySet()) {
            request.setContributor(ID);
            request.setTo(activeContributers.get(ID));
            context.getMessageSender().sendMessage(request); 
        }
    }

    @Override
    protected ConversationContext getContext() {
        return context;
    }

    @Override
    protected String getName() {
        return "Getting status's";
    }


}
