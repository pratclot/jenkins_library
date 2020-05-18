package com.pratclot

class ExtendedSteps implements Serializable{

    private callerScript
    private String lastStage

    ExtendedSteps(callerScript) {
        this.callerScript = callerScript
    }

    def call(shellCommands) {
        lastStage = callerScript.env.STAGE_NAME
        updateGitHubStatus("pending")
        String ts = updateSlackStatus("Starting ${lastStage} stage...").ts
        try {
            callerScript.sh """
                ${shellCommands}
            """
            updateGitHubStatus("success")
            updateSlackStatus("good", "${lastStage} succeeded", ts)
        } catch(ex) {
            updateGitHubStatus("failure")
            updateSlackStatus("danger", "${lastStage} failed", ts)
            throw ex
        }
    }

    private Object updateSlackStatus(String status = null, String message, String ts = null) {
        return callerScript.slack.updateThread(message, status, ts)
    }

    private void updateGitHubStatus(String status, String message = "") {
        callerScript.println(GitHub.setBuildStatus(callerScript, message, status, lastStage))
    }
}
