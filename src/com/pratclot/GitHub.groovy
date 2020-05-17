package com.pratclot

import com.squareup.okhttp.Credentials
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import groovy.json.JsonBuilder

class GitHub {
    static MediaType JSON = MediaType.parse("application/json; charset=utf-8")

    static setBuildStatus(callerScript, String message, String state, String context) {
        callerScript.withCredentials([
                callerScript.usernamePassword(
                        credentialsId: 'pratclot_github_token',
                        usernameVariable: 'USER',
                        passwordVariable: 'PASS')
        ]) {
            List<String> gitUrl = callerScript.env.GIT_URL.tokenize("/")
            String orgName = gitUrl.get(2)
            String repoName = gitUrl.get(3).tokenize(".").get(0)
            String commitSha = callerScript.env.GIT_COMMIT
            String url = "https://api.github.com/repos/" +
                    orgName + "/" + repoName +
                    "/statuses/" + commitSha
            callerScript.echo url
            String credentials = Credentials.basic(callerScript.env.USER, callerScript.env.PASS)

            Map bodyMap = [
                    "description": message,
                    "state"      : state,
                    "context"    : context,
                    "target_url" : callerScript.env.BUILD_URL
            ]


            JsonBuilder json = new groovy.json.JsonBuilder()
            json bodyMap

            callerScript.echo json.toString()

            OkHttpClient client = new OkHttpClient()
            RequestBody body = RequestBody.create(JSON, json.toString())
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .post(body)
                    .build()
            Response response = client.newCall(request).execute()
            return response.body().string()
        }
    }

    enum State {
        ERROR("error"),
        FAILURE("failure"),
        PENDING("pending"),
        SUCCESS("success")

        private final String state
        public State(final String state) { this.state = state }
        @Override String toString() { return state }
    }
}
