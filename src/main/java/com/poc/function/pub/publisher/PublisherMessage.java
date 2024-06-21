package com.poc.function.pub.publisher;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class PublisherMessage {

    private static final Log log = LogFactory.getLog(PublisherMessage.class);

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.pubsub.emulator-host}")
    private String emulatorHost;

    private static final String MESSAGE_PUBLISHED = "Published message ID: %s";

    // Method names should be verbs and express actions
    public void publishMessage(String topicId, String message) {
        ManagedChannel channel = createManagedChannel(emulatorHost);
        final TransportChannelProvider channelProvider = createTransportChannelProvider(channel);
        final CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        try {
            publisher = createPublisher(topicName, channelProvider, credentialsProvider);

            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = createPubsubMessage(data);

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            log.info(String.format(MESSAGE_PUBLISHED, messageId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            shutdownPublisher(publisher);
        }
    }

    private ManagedChannel createManagedChannel(String emulatorHost) {
        return ManagedChannelBuilder.forTarget(emulatorHost).usePlaintext().build();
    }

    private TransportChannelProvider createTransportChannelProvider(ManagedChannel channel) {
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    }

    private Publisher createPublisher(TopicName topicName, TransportChannelProvider channelProvider,
        CredentialsProvider credentialsProvider) {
        try {
            return Publisher.newBuilder(topicName)
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PubsubMessage createPubsubMessage(ByteString data) {
        return PubsubMessage.newBuilder().setData(data).build();
    }

    private void shutdownPublisher(Publisher publisher) {
        if (publisher != null) {
            publisher.shutdown();
            try {
                publisher.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
