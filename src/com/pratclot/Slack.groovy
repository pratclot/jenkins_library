package com.pratclot

class Slack implements Serializable{

    private callerScript
    private slackChannel
    private slackInitialResponse
    private String initialMessage

    public Slack(callerScript, slackChannel = "#jenkins") {
        this.callerScript = callerScript
        this.slackChannel = slackChannel
    }

    public Slack startThread() {
        initialMessage = "${callerScript.env.JOB_NAME} ${callerScript.env.BUILD_NUMBER} (<${callerScript.env.BUILD_URL}console|Open>)"
        slackInitialResponse = callerScript.slackSend(
                channel: slackChannel,
                message: "Build Started - " + initialMessage
        )

        return this
    }

    public Object updateThread(String message, String color = null, String timestamp = null) {
        List attachments = [
                [
                        text    : message,
                        fallback: 'Hey, Vader seems to be mad at you.',
                        color   : color
                ]
        ]
        return callerScript.slackSend(
                channel: slackInitialResponse.threadId,
                attachments: attachments,
                timestamp: timestamp
        )
    }

    private void markThreadAs(String color) {
        updateThread("Build Finished - " + initialMessage, color, slackInitialResponse.ts)
    }

    public void markThreadAsGreen() {
        markThreadAs("good")
    }

    public void markThreadAsRed() {
        markThreadAs("danger")
    }
}
