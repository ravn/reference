/*
 * #%L
 * bitrepository-access-client
 * *
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
package org.bitrepository.alarm;

import java.io.PrintStream;
import java.util.Date;

import org.apache.kahadb.util.ByteArrayOutputStream;
import org.bitrepository.alarm.handling.AlarmHandler;
import org.bitrepository.alarm.handling.handlers.AlarmLogger;
import org.bitrepository.alarm.handling.handlers.AlarmMailer;
import org.bitrepository.settings.referencesettings.MailingConfiguration;
import org.bitrepository.settings.referencesettings.AlarmServiceSettings;
import org.bitrepository.bitrepositoryelements.Alarm;
import org.bitrepository.bitrepositoryelements.AlarmCode;
import org.bitrepository.bitrepositorymessages.AlarmMessage;
import org.bitrepository.bitrepositorymessages.IdentifyContributorsForGetStatusRequest;
import org.bitrepository.client.DefaultFixtureClientTest;
import org.bitrepository.protocol.message.ExampleMessageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for the AlarmClient and the AlarmHandlers.
 */
public class AlarmClientTester extends DefaultFixtureClientTest {
    private static final Long DEFAULT_WAIT_TIME = 500L;

//    @Test(groups = {"regressiontest"})
//    @Test(groups = {"other-test"})
//    public void defaultAlarmHandlingTest() throws Exception {
//        addDescription("Tests the instantiation of a AlarmClient by using a local broker and a TestAlarmHandler.");
//        addStep("Defining constants for the test.", "Should not be able to fail here.");
//        String QUEUE = "ALARM-" + new Date().getTime();
//
//        addStep("Initialise the variables for the AlarmClient, e.g. The Messagebus, the AlarmHandler and "
//                + "the AlarmConfiguration", "Should be allowed.");
//        TestAlarmHandler alarmHandler = new TestAlarmHandler();
//        
//        addStep("Instantiating the AlarmClient based on these variables.", "Should connect to the messagebus.");
//        BasicAlarmService aclient = new BasicAlarmService(messageBus, settings, store, contributorMediator);
//        aclient.addHandler(alarmHandler);
//        
//        addStep("Wait for setup", "We wait!");
//        synchronized (this) {
//            try {
//                wait(DEFAULT_WAIT_TIME);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        addStep("Verify that nothing has been received by the handler.", "Latest alarm should be null.");
//        Assert.assertNull(alarmHandler.getLatestAlarm(), "No alarms sent yet, thus latest alarm should be null.");
//        Assert.assertNull(alarmHandler.getLatestAlarmMessage(), "No alarms sent yet, thus latest alarm should be null.");
//        
//        addStep("Create and send a message for the AlarmClient to handle", "");
//        AlarmMessage alarmMsg = ExampleMessageFactory.createMessage(AlarmMessage.class);
//        alarmMsg.setTo(QUEUE);
//        messageBus.sendMessage(alarmMsg);
//        
//        addStep("Wait for mediator to handle message", "We wait!");
//        synchronized (this) {
//            try {
//                wait(DEFAULT_WAIT_TIME);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        addStep("Verify that the handler has received the sent alarm message.", 
//                "The Alarm message should be received and stored.");
//        Assert.assertNotNull(alarmHandler.getLatestAlarm(), "The handler should have received a Alarm message");
//        Assert.assertNotNull(alarmHandler.getLatestAlarmMessage(), "The handler should have received a Alarm message");
//        Assert.assertTrue(alarmHandler.getLatestAlarmMessage().contains(alarmMsg.getCollectionID()));
//        Assert.assertTrue(alarmHandler.getLatestAlarmMessage().contains(alarmMsg.getCorrelationID()));
//        Assert.assertTrue(alarmHandler.getLatestAlarmMessage().contains(QUEUE));
//        Assert.assertEquals(alarmMsg, alarmHandler.getLatestAlarm());
//    }
//
//    // TODO requires the logging properties to send the output to 'STDOUT' instead of an actual log file.
//    @Test( groups={"other-test"})
//    public void TestLoggingHandler() throws Exception {
//        addDescription("Tests the AlarmLoggingHandler handling of alarms and other objects.");
//        addStep("Initalise the output stream receiver.", "Should be OK.");
//        PrintStream defaultOut = System.out;
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            System.setOut(new PrintStream(out));
//
//            addStep("Initalise handler, AlarmMessage and constants", "Should not be problematic.");
//            AlarmHandler handler = new AlarmLogger();
//            AlarmMessage alarmMsg = ExampleMessageFactory.createMessage(AlarmMessage.class);
//
//            String ALARM_MESSAGE = "REGRESSION-TEST";
//            String EXCEPTION_MESSAGE = "Can you handle this??";
//            String IDENTIFY_ID = "Identification_identifier";
//
//            addStep("Insert description of ALARM_CODE and ALARM_MESSAGE in message send to handler", 
//            "Should sent to log.");
//            Alarm alarm = new Alarm();
//            alarm.setAlarmCode(AlarmCode.COMPONENT_FAILURE);
//            alarm.setAlarmText(ALARM_MESSAGE);
//            alarmMsg.setAlarm(alarm);
//            handler.handleAlarm(alarmMsg);
//
//            addStep("Tests whether it has been written to the LOG", "Should be found in the outputstream");
//            String logwrittenOutput = new String(out.toByteArray());
//            Assert.assertTrue(logwrittenOutput.contains(ALARM_MESSAGE), 
//	     "The message should contain '" + ALARM_MESSAGE + "' but was: '" + logwrittenOutput);
//            Assert.assertTrue(logwrittenOutput.contains(AlarmCode.COMPONENT_FAILURE.name()), 
//	     "The message should contain '" + AlarmCode.COMPONENT_FAILURE + "' but was: '" + logwrittenOutput);
//            Assert.assertTrue(logwrittenOutput.contains(alarmMsg.getClass().getName()), 
//	     "The message should contain '" + alarmMsg.getClass().getName() + "' but was: '" + logwrittenOutput);
//            defaultOut.print(logwrittenOutput);
//            out.flush();
//
//            addStep("Tests the handling of other objects, in this case an NullPointerException", 
//            "Should be written to the log.");
//            handler.handleOther(new NullPointerException(EXCEPTION_MESSAGE));
//            logwrittenOutput = new String(out.toByteArray());
//            Assert.assertTrue(logwrittenOutput.contains(EXCEPTION_MESSAGE), 
//	     "The message should contain '" + EXCEPTION_MESSAGE + "' but was: '" + logwrittenOutput);
//            defaultOut.print(logwrittenOutput);
//            out.flush();
//
//            addStep("Tests the handling of other messages, in this case an IdentifyConstributorsForGetStatusRequest", 
//            "Should be written to the log.");
//            IdentifyContributorsForGetStatusRequest identifyMsg = ExampleMessageFactory.createMessage(
//	     IdentifyContributorsForGetStatusRequest.class);
//            identifyMsg.setCollectionID(IDENTIFY_ID);
//            handler.handleOther(identifyMsg);
//            logwrittenOutput = new String(out.toByteArray());
//            Assert.assertTrue(logwrittenOutput.contains(IDENTIFY_ID), 
//	     "The message should contain '" + IDENTIFY_ID + "' but was: '" + logwrittenOutput);
//            Assert.assertTrue(logwrittenOutput.contains(identifyMsg.getClass().getName()), 
//	     "The message should contain '" + identifyMsg.getClass().getName() + "' but was: '" 
//	     + logwrittenOutput);
//
//            defaultOut.print(logwrittenOutput);
//            out.flush();
//        } finally {
//            System.setOut(defaultOut);
//        }
//    }
//
//    // TODO insert your own mail address to test the mailing handler.
//    @Test( groups={"other-test"})
//    public void mailingAlarmHandler() throws Exception {
//        addDescription("Testing the MailingAlarmHandler");
//        addStep("Initialising the variables for the test.", "Should be OK");
//        AlarmServiceSettings asettings = new AlarmServiceSettings();
//        MailingConfiguration conf = new MailingConfiguration();
//        conf.setMailReceiver("error@sbforge.org");
//        conf.setMailSender("error@sbforge.org");
//        conf.setMailServer("sbforge.org");
//        asettings.setMailingConfiguration(conf);
//        AlarmHandler handler = new AlarmMailer(asettings);
//        AlarmMessage msg = ExampleMessageFactory.createMessage(AlarmMessage.class);
//
//        String ALARM_MESSAGE = "REGRESSION-TEST";
//        String EXCEPTION_MESSAGE = "Can you handle this??";
//
//        addStep("Insert description of ALARM_CODE and ALARM_MESSAGE in message send to handler", 
//        "Should sent to log.");
//        Alarm alarm = new Alarm();
//        alarm.setAlarmCode(AlarmCode.COMPONENT_FAILURE);
//        alarm.setAlarmText(ALARM_MESSAGE);
//        msg.setAlarm(alarm);
//        handler.handleAlarm(msg);
//
//        addStep("Tests the handling of other objects, in this case an exception", "Should be written to the log.");
//        handler.handleOther(new NullPointerException(EXCEPTION_MESSAGE));
//    }
}
