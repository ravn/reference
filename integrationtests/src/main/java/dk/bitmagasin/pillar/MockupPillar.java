/*
 * #%L
 * Bitmagasin integritetstest
 * 
 * $Id$
 * $HeadURL$
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
package dk.bitmagasin.pillar;

import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.bitmagasin.common.MockupConf;
import dk.bitmagasin.common.MockupGetTimeMessage;
import dk.bitmagasin.common.MockupGetTimeReplyMessage;

/**
 * Pillar MockUp
 * @author bam
 * @since 2010-10-01 */
public class MockupPillar implements MessageListener, ExceptionListener {
	/** The log for this instance.*/
	private final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * The measure for the timeout for this pillar.
	 */
    private static long timeoutMeasure = 1;
    /**
     * The unit for the timeout for this pillar.
     */
    private static String timeoutUnit = "sec";
    /**
     * The error code for retrieval of data from this pillar.
     */
    private static String errorCode = null;
    /**
     * The id for this pillar.
     */
    private static String pillarId = MockupConf.pillarId;

    private Session session;
    
    /**
     * The destination of the bus.
     */
    private Topic bus;
    /**
     * The producer to the bus.
     */
    private MessageProducer busProducer;

    private boolean verbose = true;
    private boolean transacted;
    private boolean durable = true;
    private int ackMode = Session.AUTO_ACKNOWLEDGE; //TODO probably not a good idea
    
    public static void main(String[] args) {
    	System.out.println("Arguments (default): timeoutMeasure (1), "
    			+ "timeoutUnit (sec), errorCode (null), pillarId (?)");
    	for(String arg : args) {
    		if(arg.startsWith("timeoutMeasure=")) {
    			timeoutMeasure = Long.parseLong(arg.replaceFirst("timeoutMeasure=", ""));
    		} else if(arg.startsWith("timeoutUnit=")) {
    			timeoutUnit = arg.replace("timeoutUnit=", "");
    		} else if(arg.startsWith("errorCode=")) {
    			errorCode = arg.replace("errorCode=", "");
    		} else if(arg.startsWith("pillarId=")) {
    			pillarId = arg.replace("pillarId=", "");
    		} else {
    			System.err.println("Bad argument: " + arg);
    		}
    	}

        MockupPillar mockupPillar = new MockupPillar();
        mockupPillar.run();
    }
    
    protected MockupPillar() {
    	log.info("Starting Pillar: " + pillarId);
    }

    public void run() {
        try {
            // Create connection
            ActiveMQConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory(MockupConf.user,
                            MockupConf.password, MockupConf.url);
            Connection connection = connectionFactory.createConnection();
            if (durable && pillarId != null && pillarId.length() > 0 
            		&& !"null".equals(pillarId)) {
                connection.setClientID(pillarId);
            }
            connection.setExceptionListener(this);
            connection.start();

            session = connection.createSession(transacted, ackMode);

            //create messagelistener on SLA topic
            bus = session.createTopic(MockupConf.SLAID);
            //consumer = session.createDurableSubscriber(topic, MockupConf.pillarId);
            session.createConsumer(bus).setMessageListener(this);
            busProducer = session.createProducer(bus);
            busProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (Exception e) {
        	log.error("Caught exception during run!", e);
        }
    }
    
    public void onMessage(Message msg) {
        try {
    		// report on which message is received
        	log.info("Message: " + msg.getJMSMessageID() + " wit type: "
        			+ msg.getJMSType() + ", on " + msg.getJMSDestination());
        	
    		if(!(msg instanceof TextMessage)) {
            	// Cannot handled non-TextMessage!
            	log.error("ERROR: Not in the format of a "
            			+ "TextMessage: \n" + msg);
        		return;	
    		}
    		
        	TextMessage txtMsg = (TextMessage) msg;
        	log.debug("Received text message: " + txtMsg.getText());
        	
        	// verify getType
        	if(txtMsg.getJMSType() == null || txtMsg.getJMSType().isEmpty()) {
        		// TODO handle better
        		log.error("ERROR: Unhandled message jms type: " 
        				+ txtMsg.getJMSType());
        		return;
        	}
        	
    		// TODO make more of these cases.
        	if(txtMsg.getJMSType().equals("GetTime")) {
        		visit(new MockupGetTimeMessage(txtMsg.getText()), 
        				txtMsg.getJMSReplyTo());
        	} else {
        		log.debug("Message " + msg.getJMSMessageID() + "ignored!");
        	}
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void visit(MockupGetTimeMessage msg, Destination replyTo) 
            throws JMSException {
    	log.info("Received MockupGetTimeMessage, with id: " 
    			+ msg.getConversationId() + ", and reply to: "+ replyTo);
    	
    	// validate pillarId
    	List<String> pillarIds = msg.getPillarIds();
    	if(!pillarIds.contains(pillarId)) {
    		log.info("Is not meant for my ID!");
    		// Do not handle message, which are not meant for us!
    		return;
    	}

    	MockupGetTimeReplyMessage replyMsg = new MockupGetTimeReplyMessage();
    	replyMsg.setDataId(msg.getDataId());
    	replyMsg.addConversationId(msg.getConversationId());
    	replyMsg.addTimeUnit(timeoutUnit);
    	replyMsg.setTimeMeasure(timeoutMeasure);
    	replyMsg.setPillarId(pillarId);
    	if(errorCode != null) {
    		replyMsg.addError(errorCode);
    	}
    	
        TextMessage sendMsg = session.createTextMessage(replyMsg.asXML());
        sendMsg.setJMSType("GetTimeReply");

        if(verbose) {
        	log.info("Sending: MockupGetTimeReplyMessage to: " 
        			+ replyTo);
        }
        
        MessageProducer mp = session.createProducer(replyTo);
        mp.send(replyTo, sendMsg);
        if(transacted) {
        	session.commit();
        }
    }

    public synchronized void onException(JMSException ex) {
        log.error("JMS Exception occured.  Shutting down client.", ex);
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
