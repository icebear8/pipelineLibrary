package icebear8.docker;

def buildImage(user, imageName, dockerFilePath) {
  def imageId = "${user}/${imageName}:${utils.evaluateJobBuildTag()}"
  def buildArgs = "${dockerFilePath}"

  if (isBuildRequired(imageName) == true) {
    buildArgs = "--no-cache --rm ${dockerFilePath}"
  }

  return {
    stage("Build image ${imageId}") {
      echo "Build image: ${imageId} with dockerfile ${dockerFilePath}"
      docker.build("${imageId}", "${buildArgs}")
    }
  }
}

def isBuildRequired(imageName) {
  def isCurrentImageBranch = repositoryUtils.containsCurrentBranch(imageName)

  if (isCurrentImageBranch == true) {
    return true
  }
  else if ((repositoryUtils.isStableBranch() == false) && (repositoryUtils.isReleaseBranch() == false)) {
    return true
  }

  return false
}

def isRebuildRequired() {
  if ((repositoryUtils.isLatestBranch() == true) || (repositoryUtils.isStableBranch() == true) || (repositoryUtils.isReleaseBranch() == true)) {
    return true
  }

  return false
}

def pushImage(imageId, remoteTag) {
  return {
    stage("Push image ${imageId} to ${remoteTag}") {
      echo "Push image: ${imageId} to remote with tag ${remoteTag}"

      docker.image("${imageId}").push("${remoteTag}")
    }
  }
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
