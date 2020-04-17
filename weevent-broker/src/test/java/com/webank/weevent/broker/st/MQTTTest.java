package com.webank.weevent.broker.st;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.webank.weevent.broker.JUnitTestBase;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class MQTTTest extends JUnitTestBase {

    private final String url = "tcp://localhost:7001";
    private final int actionTimeout = 3000;

    private MqttClient mqttClient;
    private String content = "hello mqtt via tcp";

    static class MessageListener implements IMqttMessageListener {
        public int received = 0;

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            log.info("received message, {}", message.getPayload());
            received++;
        }
    }

    @Before
    public void before() throws Exception {
        log.info("=============================={}.{}==============================",
                this.getClass().getSimpleName(),
                this.testName.getMethodName());

        String clientId = UUID.randomUUID().toString();
        this.mqttClient = new MqttClient(this.url, clientId, null);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setConnectionTimeout(this.actionTimeout);
        connOpts.setKeepAliveInterval(this.actionTimeout);
        connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        connOpts.setCleanSession(true);
        this.mqttClient.connect(connOpts);
    }

    @After
    public void after() throws Exception {
        if (this.mqttClient != null && this.mqttClient.isConnected()) {
            this.mqttClient.disconnect();
        }
    }

    @Test
    public void testConnectWithDefaultVersion() {
        try {
            String clientId = UUID.randomUUID().toString();
            MqttClient mqttClient = new MqttClient(this.url, clientId, null);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(this.actionTimeout);
            mqttClient.connect(connOpts);

            Assert.assertTrue(true);
        } catch (MqttException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testConnect31() {
        try {
            // client id must less then 23 bytes in 3.1
            String clientId = UUID.randomUUID().toString().split("-")[0];
            MqttClient mqttClient = new MqttClient(this.url, clientId, null);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(this.actionTimeout);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            mqttClient.connect(connOpts);

            Assert.assertTrue(true);
        } catch (MqttException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test(expected = MqttException.class)
    public void testConnect31ClientTooLong() throws MqttException {
        // client id must less then 23 bytes in 3.1
        String clientId = UUID.randomUUID().toString();
        MqttClient mqttClient = new MqttClient(this.url, clientId, null);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setConnectionTimeout(this.actionTimeout);
        connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        mqttClient.connect(connOpts);

        Assert.assertTrue(true);
    }

    @Test
    public void testConnect2Times() {
        String clientId = UUID.randomUUID().toString();

        try {
            MqttClient mqttClient = new MqttClient(this.url, clientId, null);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(this.actionTimeout);
            mqttClient.connect(connOpts);

            Assert.assertTrue(true);
        } catch (MqttException e) {
            log.error("exception", e);
            Assert.fail();
        }

        try {
            MqttClient mqttClient2 = new MqttClient(this.url, clientId, null);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(this.actionTimeout);
            mqttClient2.connect(connOpts);

            Assert.assertTrue(true);
        } catch (MqttException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testPing() {
        try {
            Thread.sleep(this.actionTimeout * 3);

            Assert.assertTrue(true);
        } catch (InterruptedException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testPublishQos0() {
        try {
            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            message.setQos(0);
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testPublishQos1() {
        try {
            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            message.setQos(1);
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test(expected = MqttException.class)
    public void testPublishQos2() throws MqttException {
        try {
            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            message.setQos(2);
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(true);
        } catch (InterruptedException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testPublish2Times() {
        try {
            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            this.mqttClient.publish(this.topicName, message);
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }


    @Test
    public void testSubscribe() {
        try {
            MessageListener listener = new MessageListener();
            this.mqttClient.subscribeWithResponse(this.topicName, listener).waitForCompletion();

            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(listener.received > 0);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testSubscribeWildcard() {
        try {
            MessageListener listener = new MessageListener();
            this.mqttClient.subscribeWithResponse("#", listener).waitForCompletion();

            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(listener.received > 0);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testSubscribeBatch() {
        try {
            MessageListener listener = new MessageListener();
            String[] topics = {this.topicName, "com.weevent"};
            this.mqttClient.subscribeWithResponse(topics, new IMqttMessageListener[]{listener, listener}).waitForCompletion();

            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(listener.received > 0);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testSubscribeQos0() {
        try {
            MessageListener listener = new MessageListener();
            this.mqttClient.subscribeWithResponse(this.topicName, listener).waitForCompletion();

            MqttMessage message = new MqttMessage(this.content.getBytes(StandardCharsets.UTF_8));
            this.mqttClient.publish(this.topicName, message);

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(listener.received > 0);
        } catch (Exception e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test(expected = MqttException.class)
    public void testSubscribeQos2() throws MqttException {
        try {
            MessageListener listener = new MessageListener();
            this.mqttClient.subscribeWithResponse(this.topicName, 2, listener).waitForCompletion();

            Thread.sleep(this.actionTimeout);
            Assert.assertTrue(true);
        } catch (InterruptedException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }

    @Test
    public void testDisconnect() {
        try {
            this.mqttClient.disconnect();

            Assert.assertTrue(true);
        } catch (MqttException e) {
            log.error("exception", e);
            Assert.fail();
        }
    }
}