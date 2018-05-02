package com.ipipeline.jms.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipipeline.jms.message.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private JmsTemplate jmsTemplate;

/*
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;
*/

    public void send(String destination, String message) {
        LOGGER.info("sending message='{}' to destination='{}'", message, destination);
        try {
            jmsTemplate.convertAndSend(destination, message);
        }
        catch(JmsException e) {
            LOGGER.error("failed to send message to queue", e);
        }
    }

    public void send(String destination, final MessageModel messageModel) {
        LOGGER.info("sending messageModel='{}' to destination='{}'", messageModel, destination);
        try {
            /*Session session = cachingConnectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            //Message message = session.createTextMessage("<foo>hey</foo>");
            message.setStringProperty("JMSXGroupID", messageModel.getCarrierId());
            session.q
            Producer producer = session.createProducer(destination);
*/

            final String json = getJson(messageModel);
            jmsTemplate.convertAndSend(destination, json, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws JMSException {
                    message.setStringProperty("JMSXGroupID", messageModel.getCarrierId());
                    return message;
                }
            });
        }
        catch(JmsException e) {
            LOGGER.error("failed to send message to queue", e);
        }
        /*catch(javax.jms.JMSException e) {
            LOGGER.error("javax.jms.JMSException occured, failed to send message to queue", e);
        }*/
    }

    private String getJson(MessageModel messageModel) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(messageModel);
            return value;
        }
        catch(JsonProcessingException e) {
            LOGGER.error("exception on converting to json", e);
            return null;
        }
    }
}
