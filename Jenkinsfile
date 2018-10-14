#!/usr/bin/env groovy

import groovy.json.JsonOutput
import java.util.Optional
import hudson.tasks.test.AbstractTestResultAction
import hudson.model.Actionable

def author = ""
def message = ""
def testSummary = ""
def total = 0
def failed = 0
def skipped = 0

def slackNotificationChannel = '#jenkinscrazy'     // ex: = "builds"

def notifySlack(text, channel, attachments) {
    def slackURL = 'https://hooks.slack.com/services/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'
    def jenkinsIcon = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'

    def payload = JsonOutput.toJson([text: text,
                                     channel: channel,
                                     username: "Jenkins",
                                     icon_url: jenkinsIcon,
                                     attachments: attachments
    ])

    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"
}


@NonCPS
def getTestSummary = { ->
    def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    def summary = ""

    if (testResultAction != null) {
        total = testResultAction.getTotalCount()
        failed = testResultAction.getFailCount()
        skipped = testResultAction.getSkipCount()

        summary = "Passed: " + (total - failed - skipped)
        summary = summary + (", Failed: " + failed)
        summary = summary + (", Skipped: " + skipped)
    } else {
        summary = "No tests found"
    }
    return summary
}

def populateGlobalVariables = {
    testSummary = getTestSummary()
}

//@NonCPS
//def getFailedTests = { ->
//    def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
//    def failedTestsString = "```"
//
//    if (testResultAction != null) {
//        def failedTests = testResultAction.getFailedTests()
//
//        if (failedTests.size() > 9) {
//            failedTests = failedTests.subList(0, 8)
//        }
//
//        for(CaseResult cr : failedTests) {
//            failedTestsString = failedTestsString + "${cr.getFullDisplayName()}:\n${cr.getErrorDetails()}\n\n"
//        }
//        failedTestsString = failedTestsString + "```"
//    }
//    return failedTestsString
//}

node{
        stage('Build') {
            def mvn_version = 'M3'
            withEnv( ["PATH+MAVEN=${tool mvn_version}/bin"] ){
            sh 'mvn test -Dtest=testClass -X'
            }
            step echo 'Building..'
                //maven build here

                populateGlobalVariables();

                def buildColor = currentBuild.result == null ? "good" : "warning"
                def buildStatus = currentBuild.result == null ? "Success" : currentBuild.result
                def jobName = "${env.JOB_NAME}"

                if (failed > 0) {
                    buildStatus = "Failed"
                    buildColor = "danger"
//                    def failedTestsString = getFailedTests()

                    notifySlack("", slackNotificationChannel, [
                            [
                                    title: "${jobName}, build #${env.BUILD_NUMBER}",
                                    title_link: "${env.BUILD_URL}",
                                    color: "${buildColor}",
                                    text: "${buildStatus}\n${author}",
                                    "mrkdwn_in": ["fields"],
                                    fields: [
                                            [
                                                    title: "Branch",
                                                    value: "${env.GIT_BRANCH}",
                                                    short: true
                                            ],
                                            [
                                                    title: "Test Results",
                                                    value: "${testSummary}",
                                                    short: true
                                            ]
                                    ]
                            ]
                    ])

                }
                else {
                    notifySlack("", slackNotificationChannel, [
                            [
                                    title: "${jobName}, build #${env.BUILD_NUMBER}",
                                    title_link: "${env.BUILD_URL}",
                                    color: "${buildColor}",
                                    author_name: "${author}",
                                    text: "${buildStatus}\n${author}",
                                    fields: [
                                            [
                                                    title: "Branch",
                                                    value: "${env.GIT_BRANCH}",
                                                    short: true
                                            ],
                                            [
                                                    title: "Test Results",
                                                    value: "${testSummary}",
                                                    short: true
                                            ]
                                    ]
                            ]
                    ])

                }
            }
}