#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def tempMethod = "buildMethod"
  def packagePathToExecute = "icebear8.projects.arctic.buildDefinition"

  def buildDefinition = new "${packagePathToExecute}"()
  buildDefinition."${tempMethod}"(projectSettings)
}