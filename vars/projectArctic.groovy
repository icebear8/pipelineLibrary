#!/usr/bin/env groovy

def buildMethod(projectSettings) {
  def p = "Arctic"
  def instructions = new icebear8.projects."${p}"()
  instructions.buildMethod(projectSettings)
}