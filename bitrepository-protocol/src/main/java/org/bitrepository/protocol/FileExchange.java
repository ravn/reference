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
package org.bitrepository.protocol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Simple interface for data transfer between an application and a server.
 *
 * If an implementation does not support a method, it may throw {@link UnsupportedOperationException}
 *
 * TODO Interface is extremely HTTP-centric. Should be generified. HTTP-centricity shines through to usage.
 */
public interface FileExchange {
    /**
     * Put a piece of data onto a http-server and returns the url for the
     * location of this data.
     *
     * @param in The inputstream to the data to be put onto the http-server.
     * @param filename The name of the piece of data to be put onto the
     * http-server.
     * @return The url of the location for the data on the http-server.
     * @throws IOException If any problems occurs during the transportation of
     * the data.
     */
    URL uploadToServer(InputStream in, String filename)
            throws IOException;

    /**
     * Puts a given file onto a http-server.
     *
     * @param dataFile The file to be put into the http-server.
     * @return The url for the file, when it has been placed onto the
     * http-server.
     */
    URL uploadToServer(File dataFile);

    /**
     * Retrieves the data from a given url and puts it onto a given
     * outputstream. It has to be a 'HTTP' url, since the data is retrieved
     * through a HTTP-request.
     *
     * @param out The output stream to put the data.
     * @param url The url for where the data should be retrieved.
     * @throws IOException If any problems occurs during the retrieval of the
     * data.
     */
    void downloadFromServer(OutputStream out, URL url)
            throws IOException;

    /**
     * Method for downloading a file at a given adress.
     *
     * @param outputFile The file where the data at the address should be
     * placed.
     * @param fileAddress The address where the data should be downloaded from.
     */
    void downloadFromServer(File outputFile, String fileAddress);

    /**
     * Creates the URL based on a filename.
     *
     * @param filename The name of the piece of data to transfer (in the form
     * of a file).
     * @return The URL containing the filename.
     * @throws MalformedURLException If the filename prevents the creation of
     * a valid URL.
     */
    URL getURL(String filename) throws MalformedURLException;
}
