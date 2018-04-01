#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def instructions = new icebear8.projects.Arctic()
  instructions.buildMethod(projectSettings)
}