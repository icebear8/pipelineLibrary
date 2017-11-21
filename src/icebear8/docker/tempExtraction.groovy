package icebear8.docker;

def setupPostTasks(buildProperties) {
    def postTasks = [:]

    for(itJob in buildProperties.dockerJobs) {

      postTasks[itJob.imageName] = dockerImage.removeLocalFunc(
        evaluateImageId(buildProperties.dockerHub.user, itJob.imageName),
        dockerUtils.getCurrentBuildTag(),
        evaluateRemoteTag())
    }
    return postTasks
}

def evaluateImageId(user, image) {
  return "${user}/${image}"
}

def evaluateRemoteTag() {
  def remoteTag = dockerUtils.getTagLocalBuild()

  if (repositoryUtils.isLatestBranch() == true) {
    remoteTag = dockerUtils.getTagLatest()
  }
  else if (repositoryUtils.isStableBranch() == true) {
    remoteTag = dockerUtils.getTagStable()
  }
  else if (repositoryUtils.isReleaseBranch() == true) {
    def releaseTag = evaluateReleaseTag(repositoryUtils.currentBuildBranch(), itJob.imageName)
    remoteTag = releaseTag != null ? releaseTag : dockerUtils.getTagLatest()
  }

  return remoteTag
}

def isBuildRequired(isCurrentImageBranch) {
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

def isPushRequired(isCurrentImageBranch) {

  if (((repositoryUtils.isReleaseBranch() == false) && (repositoryUtils.isStableBranch() == false)) || (repositoryUtils.isLatestBranch() == true)) {
    return true
  }
  else if ((isCurrentImageBranch == true) && ((repositoryUtils.isReleaseBranch() == true) || (repositoryUtils.isStableBranch() == true))) {
    return true
  }

  return false
}

def evaluateReleaseTag(releaseBranch, imageName) {
  def indexOfImage = releaseBranch.indexOf(imageName)

  if (indexOfImage < 0)
  {
    return null // exit if no valid release tag could be found
  }

  return releaseBranch.substring(indexOfImage + imageName.length() + 1) // +1 because of additional sign between image id and release tag
}

def createDummyStage(name, content) {
  stage("${name}") {
    echo "${content}"
  }
}

def buildImage(imageId, dockerFilePath, isRebuild) {
  def buildArgs = "${dockerFilePath}"

  if (isRebuild == true) {
    buildArgs = "--no-cache --rm ${dockerFilePath}"
  }

  return {
    stage("Build image ${imageId}") {
      echo "Build image: ${imageId} with dockerfile ${dockerFilePath}"
      docker.build("${imageId}", "${buildArgs}")
    }
  }
}

def pushImage(imageId, remoteTag) {
  return {
    stage("Push image ${imageId} to ${remoteTag}") {
      echo "Push image: ${imageId} to remote with tag ${remoteTag}"

      docker.image("${imageId}").push("${remoteTag}")
    }
  }
}

return this;
