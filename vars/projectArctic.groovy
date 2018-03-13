#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def tempMethod = "buildMethod"
  def packagePathToExecute = "icebear8.projects.arctic.buildDefinition"
  
  def cl = Class.forName("${packagePathToExecute}")

  def buildDefinition = new cl()
  buildDefinition."${tempMethod}"(projectSettings)
}