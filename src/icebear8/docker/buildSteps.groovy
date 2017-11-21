package icebear8.docker;

def buildImage(user, imageName, dockerFilePath) {

  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}:${utils.evaluateJobBuildTag()}"
  def buildArgs = "${dockerFilePath}"

  if (isRebuildRequired() == true) {
    buildArgs = "--no-cache --rm ${dockerFilePath}"
  }

  return {
    stage("Build image ${imageId}") {
      echo "Build image: ${imageId} with dockerfile ${dockerFilePath}"
      docker.build("${imageId}", "${buildArgs}")
    }
  }
}

def isRebuildRequired() {
  if ((repositoryUtils.isLatestBranch() == true) || (repositoryUtils.isStableBranch() == true) || (repositoryUtils.isReleaseBranch() == true)) {
    return true
  }

  return false
}

def removeImage(imageId, localImageTag, remoteImageTag) {
  return {
    stage("Remove image ${imageId}") {
      echo "Remove image: ${imageId}, tags: ${localImageTag}, ${remoteImageTag}"
      sh "docker rmi ${imageId}:${localImageTag}"
      sh "docker rmi ${imageId}:${remoteImageTag}"
    }
  }
}
