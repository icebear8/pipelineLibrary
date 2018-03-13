#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def instructions = new icebear8.projects.arctic.BuildInstructions()
  instructions.buildMethod(projectSettings)
}