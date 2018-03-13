#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def buildDefinition = new icebear8.projects.arctic.buildDefinition()
  buildDefinition.buildMethod(projectSettings)
}