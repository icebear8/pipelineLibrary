#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def tempMethod = "buildMethod"
  def packagePathToExecute = "icebear8.projects.arctic.BuildInstructions"
  
  def cl = Class.forName("${packagePathToExecute}")
  cl."${tempMethod}"(projectSettings)
}