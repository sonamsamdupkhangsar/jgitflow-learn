package com.ipipeline.jms.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipipeline.jms.message.MessageModel;
import com.ipipeline.jms.message.ThreadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    private Map<ThreadModel, List<MessageModel>> map = new HashMap<>();

    private CountDownLatch latch = new CountDownLatch(100);

    public Receiver() {
        LOG.info("receiver instantiation");
    }
    public CountDownLatch getLatch() {
        return latch;
    }

    public Map<ThreadModel, List<MessageModel>> getThreadMessages() {
        return map;
    }

    @JmsListener(destination = "${activemq.queue.helloworld}")
    public void receive(String message) {
        List<MessageModel> list = null;

        MessageModel messageModel = getMessageModel(message);
        LOG.info("received message='{}'", messageModel);

        ThreadModel threadModel = new ThreadModel(Thread.currentThread().getName(), messageModel.getCarrierId());

        if (map.containsKey(threadModel)) {
            list = map.get(threadModel);
        }
        else {
            list = new ArrayList<>();
            map.put(threadModel, list);
            LOG.info("creating new list for threadName {}", Thread.currentThread().getName());
        }

        list.add(messageModel);
        latch.countDown();
    }

    private MessageModel getMessageModel(final String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MessageModel messageModel = objectMapper.readValue(json, MessageModel.class);
            return messageModel;
        }
        catch(IOException e) {
            LOG.error("exception on converting to json", e);
            return null;
        }
    }
}