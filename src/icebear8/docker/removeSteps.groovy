#!/usr/bin/env groovy

package icebear8.docker;

def removeImage(user, imageName) {
  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}"
  def localTag = utils.evaluateJobBuildTag()
  def remoteTags = utils.evaluateRemoteTags(imageName)

  return {
    stage("Remove image ${imageId}") {
      echo "Remove image: ${imageId}, tags: ${localTag}, ${remoteTags}"
      sh "docker rmi ${imageId}:${localTag}"
      
      remoteTags.each {
        sh "docker rmi ${imageId}:${it}"
      }     
    }
  }
}

def removeAllUnusedImages() {
  return {
    stage("Remove all unused images") {
      sh "docker system prune --force --all"
    }
  }
}