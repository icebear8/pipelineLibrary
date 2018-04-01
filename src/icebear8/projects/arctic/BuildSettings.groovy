#!/usr/bin/env groovy

package icebear8.projects.arctic;

def getProperties() {
  def triggers = []

  if (repositoryUtils.isLatestBranch() == true) {
    triggers << cron('H 15 * * *')
  }

  return properties([
    pipelineTriggers(triggers),
    buildDiscarder(logRotator(
      artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5',
      numToKeepStr: '5', daysToKeepStr: '5'))
  ])
}

return this;