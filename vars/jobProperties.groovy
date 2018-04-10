#!/usr/bin/env groovy

// Gets the build triggers for a specific project ID
// projectId
def getJobBuildTriggers(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def buildTriggers = []
  
  switch (config.projectId)
  {
    default:
      if (repositoryUtils.isLatestBranch() == true) {
        buildTriggers << cron('H 15 * * *')
      }
    break
  }

  return buildTriggers
}
