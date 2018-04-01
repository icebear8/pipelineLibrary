#!/usr/bin/env groovy

def buildMethod(projectSettings) {

  projectSettings = new icebear8.projects.arctic.BuildSettings();
  projectSettings.getBuildTriggerSettings()

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