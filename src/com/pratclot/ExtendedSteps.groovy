package com.pratclot

class ExtendedSteps implements Serializable{

    private callerScript
    private String lastStage

    ExtendedSteps(callerScript) {
        this.callerScript = callerScript
    }

    def call(shellCommands) {
        callerScript.withCredentials([
                callerScript.file(credentialsId: callerScript.GOOGLE_SERVICES_FILE_VAR_ID, variable: "GOOGLE_SERVICES_FILE_PATH"),
                callerScript.string(credentialsId: callerScript.GOOGLE_PROJECT_VAR_ID, variable: "GOOGLE_PROJECT_ID"),
                callerScript.file(credentialsId: callerScript.GOOGLE_SERVICE_CREDENTIALS_ID, variable: "PATH_TO_GOOGLE_SERVICE_KEY")
        ]) {
            lastStage = callerScript.env.STAGE_NAME
            updateGitHubStatus("pending")
            String ts = updateSlackStatus("Starting ${lastStage} stage...").ts
            try {
                callerScript.sh """
                ln -fs ${callerScript.env.GOOGLE_SERVICES_FILE_PATH} app/google-services.json
                ${shellCommands}
            """
                updateGitHubStatus("success")
                updateSlackStatus("good", "${lastStage} succeeded", ts)
            } catch (ex) {
                updateGitHubStatus("failure")
                updateSlackStatus("danger", "${lastStage} failed", ts)
                throw ex
            }
        }
    }

    private Object updateSlackStatus(String status = null, String message, String ts = null) {
        return callerScript.slack.updateThread(message, status, ts)
    }

    private void updateGitHubStatus(String status, String message = "") {
        callerScript.println(GitHub.setBuildStatus(callerScript, message, status, lastStage))
    }
}
