/*
 * #%L
 * bitrepository-access-client
 * 
 * $Id: SimpleGetFileConversation.java 250 2011-08-03 08:44:19Z mss $
 * $HeadURL: https://sbforge.org/svn/bitrepository/trunk/bitrepository-access-client/src/main/java/org/bitrepository/access/getfile/conversation/SimpleGetFileConversation.java $
 * %%
 * Copyright (C) 2010 The State and University Library, The Royal Library and The State Archives, Denmark
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
package org.bitrepository.access.getchecksums;

import java.net.URL;
import java.util.Arrays;

import org.bitrepository.access.getchecksums.conversation.SimpleGetChecksumsConversation;
import org.bitrepository.access.getfile.conversation.SimpleGetFileConversation;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecs;
import org.bitrepository.bitrepositoryelements.FileIDs;
import org.bitrepository.bitrepositoryelements.ResultingChecksums;
import org.bitrepository.common.ArgumentValidator;
import org.bitrepository.protocol.bitrepositorycollection.ClientSettings;
import org.bitrepository.protocol.eventhandler.DefaultEvent;
import org.bitrepository.protocol.eventhandler.EventHandler;
import org.bitrepository.protocol.eventhandler.OperationEvent.OperationEventType;
import org.bitrepository.protocol.exceptions.NoPillarFoundException;
import org.bitrepository.protocol.exceptions.OperationFailedException;
import org.bitrepository.protocol.exceptions.OperationTimeOutException;
import org.bitrepository.protocol.mediator.CollectionBasedConversationMediator;
import org.bitrepository.protocol.mediator.ConversationMediator;
import org.bitrepository.protocol.messagebus.MessageBus;
import org.bitrepository.protocol.messagebus.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default <code>GetChecksumsClient</code>.
 * 
 * This class is just a thin wrapper which creates a conversion each time a operation is started. The conversations 
 * takes over the rest of the operation handling.
 */
public class BasicGetChecksumsClient implements GetChecksumsClient {
    /** The log for this class. */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /** The settings for this instance.*/
    private final GetChecksumsClientSettings settings;
    /** The messagebus for communication.*/
    private final MessageBus bus;
    
    /** The mediator which should manage the conversations. */
    private final ConversationMediator<SimpleGetChecksumsConversation> conversationMediator;

    /**
     * The constructor.
     * @param messageBus The messagebus for communication.
     * @param settings The settings for this instance.
     */
    public BasicGetChecksumsClient(MessageBus messageBus, GetChecksumsClientSettings settings) {
        this.bus = messageBus;
        this.settings = settings;
        conversationMediator = new CollectionBasedConversationMediator<SimpleGetChecksumsConversation>(settings, 
                bus, settings.getClientTopicID());
    }
    
    @Override
    public void getChecksums(String[] pillarIDs, FileIDs fileIDs, ChecksumSpecs checksumSpec, 
            EventHandler eventHandler) {
        ArgumentValidator.checkNotNullOrEmpty(pillarIDs, "String[] pillarIDs");
        ArgumentValidator.checkNotNull(fileIDs, "FileIDs fileIDs");
        ArgumentValidator.checkNotNull(checksumSpec, "ChecksumSpecTYPE checksumSpec");
        
        log.info("Requesting the checksum of the file '" + fileIDs.getFileID() + "' from the pillars '"
                + Arrays.asList(pillarIDs) + "' with the specifications '" + checksumSpec + "'");
        // TODO Auto-generated method stub
    }

    @Override
    public ResultingChecksums getChecksums(String[] pillarIDs, FileIDs fileIDs, ChecksumSpecs checksumSpec) 
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        ArgumentValidator.checkNotNullOrEmpty(pillarIDs, "String[] pillarIDs");
        ArgumentValidator.checkNotNull(fileIDs, "FileIDs fileIDs");
        ArgumentValidator.checkNotNull(checksumSpec, "ChecksumSpecTYPE checksumSpec");
        
        log.info("Requesting the checksum of the file '" + fileIDs.getFileID() + "' from the pillars '"
                + Arrays.asList(pillarIDs) + "' with the specifications '" + checksumSpec + "'");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getChecksums(String[] pillarIDs, FileIDs fileIDs, ChecksumSpecs checksumSpec, URL addressForResult,
            EventHandler eventHandler) {
        ArgumentValidator.checkNotNullOrEmpty(pillarIDs, "String[] pillarIDs");
        ArgumentValidator.checkNotNull(fileIDs, "FileIDs fileIDs");
        ArgumentValidator.checkNotNull(checksumSpec, "ChecksumSpecTYPE checksumSpec");
        ArgumentValidator.checkNotNull(addressForResult, "URL addressForResult");
        
        log.info("Requesting the checksum of the file '" + fileIDs.getFileID() + "' from the pillars '"
                + Arrays.asList(pillarIDs) + "' with the specifications '" + checksumSpec + "'. "
                + "The result should be uploaded to '" + addressForResult + "'.");

        SimpleGetChecksumsConversation conversation = new SimpleGetChecksumsConversation(bus, settings, 
                addressForResult, fileIDs, checksumSpec, pillarIDs, eventHandler);
        try {
            conversationMediator.addConversation(conversation);
            conversation.startConversation();
        } catch (OperationFailedException e) {
            eventHandler.handleEvent(new DefaultEvent(OperationEventType.Failed, e.getMessage()));
        }
    }

    @Override
    public void getChecksums(String[] pillarIDs, FileIDs fileIDs, ChecksumSpecs checksumSpec, URL addressForResult)
            throws NoPillarFoundException, OperationTimeOutException, OperationFailedException {
        ArgumentValidator.checkNotNullOrEmpty(pillarIDs, "String[] pillarIDs");
        ArgumentValidator.checkNotNull(fileIDs, "FileIDs fileIDs");
        ArgumentValidator.checkNotNull(checksumSpec, "ChecksumSpecTYPE checksumSpec");
        ArgumentValidator.checkNotNull(addressForResult, "URL addressForResult");
        
        log.info("Requesting the checksum of the file '" + fileIDs.getFileID() + "' from the pillars '"
                + Arrays.asList(pillarIDs) + "' with the specifications '" + checksumSpec + "'. "
                + "The result should be uploaded to '" + addressForResult + "'.");
        SimpleGetChecksumsConversation conversation = new SimpleGetChecksumsConversation(bus, settings, 
                addressForResult, fileIDs, checksumSpec, pillarIDs, null);
        conversationMediator.addConversation(conversation);
        conversation.startConversation();
    }
}
