/*
 * #%L
 * Bitrepository Core
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
package org.bitrepository.service.exception;

import org.bitrepository.bitrepositoryelements.ResponseInfo;

/**
 * The exception for the request handlers.
 */
@SuppressWarnings("serial")
public abstract class RequestHandlerException extends Exception {
    /** The ResponseInfo wrapped by this exception. Tells the reason for the exception.*/
    private final ResponseInfo responseInfo;
    /** The id of the collection, which this exception is relevant for, if any. */
    private String collectionId;
    
    /**
     * Constructor.
     * @param rInfo The ResponseInfo for this class to wrap.
     * @param collectionID The id of the collection. Use 'null' if no collection is relevant.
     */
    public RequestHandlerException(ResponseInfo rInfo, String collectionId) {
        super(rInfo.getResponseText());
        this.responseInfo = rInfo;
        this.collectionId = collectionId;
    }
    
    /**
     * Constructor.
     * @param rInfo The ResponseInfo for this class to wrap.
     * @param collectionID The id of the collection. Use 'null' if no collection is relevant.
     * @param e The exception to wrap into the StackTrace.
     */
    public RequestHandlerException(ResponseInfo rInfo, String collectionId, Exception e) {
        super(rInfo.getResponseText(), e);
        this.responseInfo = rInfo;
        this.collectionId = collectionId;
    }
    
    /**
     * @return The wrapped ResponseInfo.
     */
    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }
    
    /**
     * @return The id of the collection regarding this exception.
     */
    public String getCollectionId() {
        return collectionId;
    }
    
    /**
     * @param collectionId The id of the collection.
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    
    @Override
    public String toString() {
        return super.toString() + ", " + responseInfo.toString();
    }
}
