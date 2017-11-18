package icebear8.docker;

def setupBuildTasks(buildProperties) {
  def buildTasks = [:]

  for(itJob in buildProperties.dockerJobs) {
    def isCurrentImageBranch = repositoryUtils.containsCurrentBranch(itJob.imageName)
    def imageId = "${buildProperties.dockerHub.user}/${itJob.imageName}"
    def localImageId = "${imageId}:${dockerUtils.getCurrentBuildTag()}"

    if (isBuildRequired(isCurrentImageBranch) == true) {
      buildTasks[itJob.imageName] = buildImage(localImageId, itJob.dockerfilePath, isRebuildRequired())
    }
  }
  
  return buildTasks
}

def setupPushTasks(buildProperties) {
  def pushTasks = [:]

  for(itJob in buildProperties.dockerJobs) {
    
    def isCurrentImageBranch = repositoryUtils.containsCurrentBranch(itJob.imageName)
    def imageId = "${buildProperties.dockerHub.user}/${itJob.imageName}"
    def localImageId = "${imageId}:${dockerUtils.getCurrentBuildTag()}"

    def remoteImageTag = dockerUtils.getTagLocalBuild()
    
    if (repositoryUtils.isLatestBranch() == true) {
      remoteImageTag = dockerUtils.getTagLatest()
    }
    else if (repositoryUtils.isStableBranch() == true) {
      remoteImageTag = dockerUtils.getTagStable()
    }
    else if (repositoryUtils.isReleaseBranch() == true) {
      def releaseTag = evaluateReleaseTag(repositoryUtils.currentBuildBranch(), itJob.imageName)
      remoteImageTag = releaseTag != null ? releaseTag : dockerUtils.getTagLatest()
    }
    
    if (isPushRequired(isCurrentImageBranch) == true) {
      pushTasks[itJob.imageName] = pushImage(localImageId, remoteImageTag)
    }
  }
  
  return pushTasks
}

def setupPostTasks(buildProperties) {
    def postTasks = [:]
      
    for(itJob in buildProperties.dockerJobs) {
      
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
      
      postTasks[itJob.imageName] = dockerImage.removeLocal {
        imageId = "${buildProperties.dockerHub.user}/${itJob.imageName}"
        localImageTag = "${dockerUtils.getCurrentBuildTag()}"
        remoteImageTag = "${remoteTag}"
      }
    }
    
    return postTasks
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
