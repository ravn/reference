/*
 * #%L
 * Bitrepository Modifying Client
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
package org.bitrepository.modify.deletefile;

import java.util.Arrays;
import org.bitrepository.bitrepositoryelements.ChecksumDataForFileTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.client.AbstractClient;
import org.bitrepository.client.conversation.mediator.ConversationMediator;
import org.bitrepository.client.eventhandler.EventHandler;
import org.bitrepository.common.ArgumentValidator;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.modify.deletefile.conversation.DeleteFileConversationContext;
import org.bitrepository.modify.deletefile.conversation.IdentifyPillarsForDeleteFile;
import org.bitrepository.protocol.messagebus.MessageBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationBasedDeleteFileClient extends AbstractClient implements DeleteFileClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @see AbstractClient
     */
    public ConversationBasedDeleteFileClient(MessageBus messageBus, ConversationMediator conversationMediator, 
            Settings settings, String clientID) {
        super(settings, conversationMediator, messageBus, clientID);
    }
    
    @Override
    public void deleteFile(String collectionID, String fileId, String pillarId,
                           ChecksumDataForFileTYPE checksumForPillar,
            ChecksumSpecTYPE checksumRequested, EventHandler eventHandler, String auditTrailInformation) {
        ArgumentValidator.checkNotNullOrEmpty(fileId, "String fileId");
        ArgumentValidator.checkNotNullOrEmpty(pillarId, "String pillarId");
        validateFileID(fileId);
        if(settings.getRepositorySettings().getProtocolSettings().isRequireChecksumForDestructiveRequests()) {
            ArgumentValidator.checkNotNull(checksumForPillar, "ChecksumForPillar");
        }
        
        log.info("Requesting the deletion of the file '" + fileId + "' from the pillar '" + pillarId 
                + "' with checksum '" + checksumForPillar + "', while requested checksum '" + checksumRequested 
                + "'. And the audit trail information '" + auditTrailInformation + "'.");
        
        DeleteFileConversationContext context = new DeleteFileConversationContext(fileId,
                collectionID, checksumForPillar, checksumRequested, settings, messageBus, clientID,
                Arrays.asList(pillarId), eventHandler, auditTrailInformation);
        startConversation(context, new IdentifyPillarsForDeleteFile(context));
    }
}
