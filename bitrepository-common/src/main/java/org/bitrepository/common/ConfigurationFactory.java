/*
 * #%L
 * bitrepository-common
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
package org.bitrepository.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

/**
 * General class for instantiating configurations based on xml files
 */
public final class ConfigurationFactory {

    /** The default system property path for the */
    public static final String CONFIGURATION_DIR_SYSTEM_PROPERTY = "org.bitrepository.config";
    /** The default path to the configuration files.*/
    public static final String DEFAULT_CONFIGURATION_CLASSPATH_PATH = "configuration/xml/";
    /** The default name of the configuration files.*/
    public static final String DEFAULT_CONFIGURATION_CLASSPATH_NAME = "%s-configuration.xml";
    /** The default name of the test configuration files.*/
    public static final String DEFAULT_TEST_CONFIGURATION_CLASSPATH_NAME = "%s-test-configuration.xml";
    /**
     * This is configuration the <code>\ConfigurationFactory</code> will look for, if no test configuration is found.
     */
    public static final String DEFAULT_CONFIGURATION_CLASSPATH_LOCATION = 
        DEFAULT_CONFIGURATION_CLASSPATH_PATH + DEFAULT_CONFIGURATION_CLASSPATH_NAME;
    
    /**
     * This is test configuration the <code>ConfigurationFactory</code> will look for.
     */
    public static final String DEFAULT_TEST_CONFIGURATION_CLASSPATH_LOCATION = 
        DEFAULT_CONFIGURATION_CLASSPATH_PATH + DEFAULT_TEST_CONFIGURATION_CLASSPATH_NAME;
    
    /**
     * Loads the configuration for a module. 
     * 
     * The following prioritized sequence is used to find the configuration file. 
     * <ol>
     * <li>Tries to load the 'configuration/xml/%s-test-configuration.xml' file from the classpath,
     *  where %s is the modules name.</li>
     * <li>Tries to load the 'configuration/xml/%s--configuration.xml' file from the classpath,
     *  where %s is the modules name.</li>
     * </ol>
     * 
     * The modules name is retrieved via. the moduleCharacteristics objects getLowerCaseNameWithHyphen method.
     * 
     * @param moduleCharacteristics Used for extracting name strings for the module
     * @param configurationClass The class to load the configuration to. 
     * The namespace for this class (defined in the Jaxb generated package-info.java) is also used for the xml loading.
     * @return Returns a new instance of the indicated configuration loaded from file
     */
    public <T> T loadConfiguration(ModuleCharacteristics moduleCharacteristics, Class<T> configurationClass) {

        String defaultClassPathLocation = String.format(DEFAULT_CONFIGURATION_CLASSPATH_LOCATION, 
                moduleCharacteristics.getLowerCaseName());
        String defaultTestClassPathLocation = String.format(DEFAULT_TEST_CONFIGURATION_CLASSPATH_LOCATION, 
                        moduleCharacteristics.getLowerCaseName());

        InputStream configStream = null;
        if(configStream == null) {
            String path = System.getProperty(CONFIGURATION_DIR_SYSTEM_PROPERTY);
            String name = String.format(DEFAULT_CONFIGURATION_CLASSPATH_NAME,
                    moduleCharacteristics.getLowerCaseName());
            if(path != null && !path.isEmpty()){ 
                File configFile = new File(path, name);
                try {
                    configStream = new FileInputStream(configFile);
                } catch (IOException e) {
                    System.err.println("Couldn't find or handle config file '" + configFile.getAbsolutePath() + "'");
                }
            }
        }
        if(configStream == null) {
            configStream = ClassLoader.getSystemResourceAsStream(defaultTestClassPathLocation);
        }
        if (configStream == null) {
            configStream = ClassLoader.getSystemResourceAsStream(defaultClassPathLocation);
        }
        if (configStream == null) {
            throw new ConfigurationException("Failed to find " + defaultTestClassPathLocation + " or " + 
                    defaultClassPathLocation + " in classpath");
        }
        try {
            return (T) JaxbHelper.loadXml(configurationClass, configStream);
        } catch (JAXBException e) {
            throw new ConfigurationException("Failed to load the configuration from " + configStream, 
                    e);
        }
    }
}
