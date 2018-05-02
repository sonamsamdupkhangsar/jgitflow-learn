package com.ipipeline.jms;

import com.ipipeline.jms.consumer.Receiver;
import com.ipipeline.jms.message.MessageModel;
import com.ipipeline.jms.message.ThreadModel;
import com.ipipeline.jms.producer.Sender;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.AbstractJmsListeningContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class SpringJmsApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SpringJmsApplicationTest.class);

    @ClassRule
    public static EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private DefaultJmsListenerContainerFactory jmsListenerContainerFactory;

    @Autowired
    private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Test
    public void testReceive() throws Exception {
        List<MessageModel> aaaMessageList = buildMessageList("aaa", 10);
        List<MessageModel> pacLifeMessageList = buildMessageList("PacLife", 2);
        List<MessageModel> transAmericaLifeMessageList = buildMessageList("TransAmerica", 11);
        List<MessageModel> aaaMessageList2 = buildMessageList("aaa", 7);

        for(MessageListenerContainer messageListenerContainer: jmsListenerEndpointRegistry.getListenerContainers()) {
            LOG.info("stop container {}", messageListenerContainer.toString());
            messageListenerContainer.stop();
        }
        sendMessages(aaaMessageList);
        sendMessages(pacLifeMessageList);
        sendMessages(transAmericaLifeMessageList);
        sendMessages(aaaMessageList2);

        for(MessageListenerContainer messageListenerContainer: jmsListenerEndpointRegistry.getListenerContainers()) {
            LOG.info("start container {}", messageListenerContainer.toString());
            messageListenerContainer.start();
        }
        LOG.info("receiver.latch.count: {}", receiver.getLatch().getCount());
        receiver.getLatch().await(20000, TimeUnit.MILLISECONDS);

        Map<ThreadModel, List<MessageModel>> map = receiver.getThreadMessages();
        Assert.assertEquals(3, map.keySet().size());
        checkMap(map, 3);
    }

    private void sendMessages(List<MessageModel> list) {
        for(MessageModel messageModel : list) {
            sender.send("helloworld.q", messageModel);
        }
    }

    private List<MessageModel> buildMessageList(String carrierId, int count) {
        List<MessageModel> list = new ArrayList<>();
        MessageModel messageModel = null;

        for (int i = 0; i < count; i++) {
            messageModel = new MessageModel(carrierId, String.valueOf(i));
            list.add(messageModel);
        }
        return list;
    }

    private void checkMap(Map<ThreadModel, List<MessageModel>> map, final int keyCount) {
        Set<ThreadModel> keySet = map.keySet();
        Assert.assertEquals(keyCount, map.keySet().size());
        boolean foundAaa = false;
        boolean foundTransAmerica = false;
        boolean foundPacLife = false;

        for (ThreadModel key: keySet) {
            if (key.getCarrierName().equals("aaa")) {
                Assert.assertEquals(17, map.get(key).size());
                foundAaa = true;
            }
            else if (key.getCarrierName().equals("PacLife")) {
                Assert.assertEquals(2, map.get(key).size());
                foundPacLife = true;
            }
            else if (key.getCarrierName().equals("TransAmerica")) {
                Assert.assertEquals(11, map.get(key).size());
                foundTransAmerica = true;
            }
            else {
                Assert.fail("unknown carrier name found "+ key.getCarrierName());
            }
            LOG.info("key: {} has list size {}", key, map.get(key).size());
        }
        Assert.assertTrue(foundAaa);
        Assert.assertTrue(foundTransAmerica);
        Assert.assertTrue(foundPacLife);
    }
}