package io.epopeia;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
public class Application {

    Application() {
    }

    public static void main(String... args) {
        final ApplicationContext ctx = SpringApplication.run(Application.class, args);

        if (ctx.getEnvironment().acceptsProfiles("server")) {
            final AbstractServerConnectionFactory server = ctx.getBean(AbstractServerConnectionFactory.class);
            final String connectionId = server.getOpenConnectionIds().get(0);
            final Map<String, Object> headers = new HashMap<>();
            headers.put(IpHeaders.CONNECTION_ID, connectionId);
            final Message<String> message = new GenericMessage<>("This is not a reply from any request!!!", headers);
            final TcpSendingMessageHandler sender = ctx.getBean(TcpSendingMessageHandler.class);
            sender.handleMessageInternal(message);
        } else if (ctx.getEnvironment().acceptsProfiles("client")) {
            final TcpSendingMessageHandler sender = ctx.getBean(TcpSendingMessageHandler.class);
            sender.handleMessageInternal(new GenericMessage<String>("Fernando"));
            sender.handleMessageInternal(new GenericMessage<String>("Amaral"));
        }
    }
}
