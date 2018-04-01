#!/usr/bin/env groovy

package icebear8.projects;

def buildMethod(projectSettings) {
  def triggers = []

  if (repositoryUtils.isLatestBranch() == true) {
    triggers << cron('H 15 * * *')
  }

  properties([
    pipelineTriggers(triggers),
    buildDiscarder(logRotator(
      artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5',
      numToKeepStr: '5', daysToKeepStr: '5'))
  ])

  def branchNameParameter = "*/${buildUtils.getCurrentBuildBranch()}"
  
  repositoryUtils.checkoutBranch {
    stageName = 'Checkout'
    branchName = branchNameParameter
    repoUrl = "${projectSettings.repository.url}"
    repoCredentials = "${projectSettings.repository.credentials}"
  }

  docker.withServer(env.DEFAULT_DOCKER_HOST_CONNECTION, 'default-docker-host-credentials') {
    try {
      stage("Build") {
        parallel dockerImage.setupBuildTasks {
          dockerRegistryUser = "${projectSettings.dockerHub.user}"
          buildJobs = projectSettings.dockerJobs
        }
      }
      stage("Push") {
        parallel dockerImage.setupPushTasks {
          dockerRegistryUser = "${projectSettings.dockerHub.user}"
          buildJobs = projectSettings.dockerJobs
        }
      }
    }
    finally {
      stage("Clean up") {
        parallel dockerImage.setupRemoveTasks {
          dockerRegistryUser = "${projectSettings.dockerHub.user}"
          buildJobs = projectSettings.dockerJobs
        }
      }
    }
  }
}