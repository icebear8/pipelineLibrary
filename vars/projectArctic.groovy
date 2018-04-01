#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def p = "icebear8.projects.Arctic"
  def instructions = new '${p}'()
  instructions.buildMethod(projectSettings)
}