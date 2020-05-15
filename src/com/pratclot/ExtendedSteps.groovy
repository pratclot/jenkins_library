package com.pratclot

class ExtendedSteps implements Serializable{

    private callerScript

    ExtendedSteps(callerScript) {
        this.callerScript = callerScript
    }

    def call(shellCommands) {
        String lastStage = callerScript.env.STAGE_NAME
        String ts = callerScript.slack.updateThread("Starting ${lastStage} stage...").ts
        try {
            callerScript.sh """
                ${shellCommands}
            """
            callerScript.slack.updateThread("${lastStage} succeeded", "good", ts)
        } catch(ex) {
            callerScript.slack.updateThread("${lastStage} failed", "danger", ts)
            throw ex
        }
    }
}
