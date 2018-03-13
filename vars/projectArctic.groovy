#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def tempMethod = "buildMethod"

  def buildDefinition = new icebear8.projects.arctic.buildDefinition()
  buildDefinition."${tempMethod}"(projectSettings)
}