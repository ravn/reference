/*
 * #%L
 * Bitmagasin modify client
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

package org.bitrepository.modify;

import org.bitrepository.common.ModuleCharacteristics;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.modify.deletefile.ConversationBasedDeleteFileClient;
import org.bitrepository.modify.deletefile.DeleteFileClient;
import org.bitrepository.modify.putfile.ConversationBasedPutFileClient;
import org.bitrepository.modify.putfile.PutFileClient;
import org.bitrepository.modify.replacefile.ConversationBasedReplaceFileClient;
import org.bitrepository.modify.replacefile.ReplaceFileClient;
import org.bitrepository.protocol.ProtocolComponentFactory;
import org.bitrepository.protocol.mediator.ConversationMediatorManager;
import org.bitrepository.protocol.security.SecurityManager;

/**
 * Factory class for the access module. 
 * Instantiates the instances of the interfaces within this module.
 */
public final class ModifyComponentFactory {
    /** The singleton instance. */
    private static ModifyComponentFactory instance;

    /**
     * Instantiation of this singleton.
     * 
     * @return The singleton instance of this factory class.
     */
    public static synchronized ModifyComponentFactory getInstance() {
        // ensure singleton.
        if(instance == null) {
            instance = new ModifyComponentFactory();
        }
        return instance;
    }

    /** The characteristics for this module.*/
    private ModuleCharacteristics moduleCharacter;

    /**
     * Private constructor for initialisation of the singleton.
     */
    private ModifyComponentFactory() { 
        moduleCharacter = new ModuleCharacteristics("modify-client");
    }

    /**
     * Method for retrieving the characteristics for this module.
     * @return The characteristics for this module.
     */
    public ModuleCharacteristics getModuleCharacteristics() {
        return moduleCharacter;
    }

    /**
     * Method for initialising the PutClient.
     * @return The configured PutClient.
     */
    public PutFileClient retrievePutClient(Settings settings, SecurityManager securityManager) {
        return new ConversationBasedPutFileClient(
                ProtocolComponentFactory.getInstance().getMessageBus(settings, securityManager), 
                ConversationMediatorManager.getConversationMediator(settings, securityManager), 
                settings);
    }
    
    /**
     * @param settings The settings for the DeleteFileClient.
     * @return The requested DeleteClient.
     */
    public DeleteFileClient retrieveDeleteFileClient(Settings settings, SecurityManager securityManager) {
        return new ConversationBasedDeleteFileClient(
                ProtocolComponentFactory.getInstance().getMessageBus(settings, securityManager), 
                ConversationMediatorManager.getConversationMediator(settings, securityManager), 
                settings);
    }
        
    /**
     * @param settings The settings for the DeleteFileClient.
     * @return The requested DeleteClient.
     */
    public ReplaceFileClient retrieveReplaceFileClient(Settings settings, SecurityManager securityManager) {
        return new ConversationBasedReplaceFileClient(
                ProtocolComponentFactory.getInstance().getMessageBus(settings, securityManager), 
                ConversationMediatorManager.getConversationMediator(settings, securityManager), 
                settings);
    }
}
