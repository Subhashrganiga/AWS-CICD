package com.subhash.lambda.UploadsNotificationLambda;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class UploadsNotificationHandler
        implements RequestHandler<SQSEvent, Void> {

    // Best practice: initialize outside handler
    private static final SnsClient snsClient = SnsClient.create();
    private static final String TOPIC_ARN =
            System.getenv("SNS_TOPIC_ARN");

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        // ✅ Guard clause (MANDATORY)
//        ss
        if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
            context.getLogger().log("No SQS records found. Invocation source is not SQS.");
            return null;
        }

        context.getLogger().log(
                "Received " + event.getRecords().size() + " SQS messages"
        );

        for (SQSEvent.SQSMessage message : event.getRecords()) {

            snsClient.publish(PublishRequest.builder()
                    .topicArn(TOPIC_ARN)
                    .message(message.getBody())
                    .build());

            context.getLogger().log(
                    "Published message to SNS: " + message.getBody()
            );
        }

        return null;
    }

}