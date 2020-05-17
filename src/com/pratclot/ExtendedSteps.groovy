package com.pratclot

class ExtendedSteps implements Serializable{

    private callerScript

    ExtendedSteps(callerScript) {
        this.callerScript = callerScript
    }

    def call(shellCommands) {
        updateGitHubStatus("pending")
        String lastStage = callerScript.env.STAGE_NAME
        String ts = callerScript.slack.updateThread("Starting ${lastStage} stage...").ts
        try {
            callerScript.sh """
                ${shellCommands}
            """
            updateGitHubStatus("success")
            callerScript.slack.updateThread("${lastStage} succeeded", "good", ts)
        } catch(ex) {
            updateGitHubStatus("failure")
            callerScript.slack.updateThread("${lastStage} failed", "danger", ts)
            throw ex
        }
    }

    void updateGitHubStatus(String status) {
        callerScript.println(GitHub.setBuildStatus(callerScript, "blah-blah", status, callerScript.env.STAGE_NAME))
    }
}
