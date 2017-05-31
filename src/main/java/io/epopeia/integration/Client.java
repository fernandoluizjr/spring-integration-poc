package io.epopeia.integration;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.messaging.Message;

@Profile("client")
@Configuration
public class Client {

    private final Integer port;

    private static final String toServer = "toServer";
    private static final String fromServer = "fromServer";

    @Autowired
    public Client(@Value("${server.port}") Integer port) {
        Objects.nonNull(port);
        this.port = port;
    }

    @Bean
    public AbstractClientConnectionFactory myClient() {
        return new TcpNetClientConnectionFactory("localhost", this.port);
    }

    @Bean
    @ServiceActivator(inputChannel = toServer)
    public TcpSendingMessageHandler mySender() {
        final TcpSendingMessageHandler sender = new TcpSendingMessageHandler();
        sender.setConnectionFactory(myClient()); // share the same connections
        return sender;
    }

    @Bean
    public TcpReceivingChannelAdapter myReceiver() {
        final TcpReceivingChannelAdapter receiver = new TcpReceivingChannelAdapter();
        receiver.setConnectionFactory(myClient()); // share the same connections
        receiver.setOutputChannelName(fromServer);
        return receiver;
    }

    @ServiceActivator(inputChannel = fromServer)
    public void handleMessageFromServer(Message<byte[]> message) {
        System.out.println("Received from server: " + new String(message.getPayload()));
    }
}
