*****************************
A Cool Jenkins Shared Library
*****************************

With as little as following your pipeline will be able to:

- run shell steps
- send stage progress updates to Slack
- set statuses in GitHub

.. code-block:: groovy

    @Library('jenkins_library@v0.1.2')
    import com.pratclot.*

    slack = new Slack(this).startThread()
    extendedSteps = new ExtendedSteps(this)

    pipeline {
        agent any

        stages {
            stage('Lint') {
                steps {
                    script {
                        extendedSteps """
                            ./gradlew ktLintDebugCheck
                        """
                    }
                }
            }
        }
    }

See how cool?

.. raw:: html

    <div display="flex">
        <img src="assets/slack.png" width="300" display="inline-block" />
        <img src="assets/statuses.png" width="600" display="inline-block" />
    </div>
