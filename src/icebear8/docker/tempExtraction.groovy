package icebear8.docker

def buildTasks = [:]
def pushTasks = [:]
def postTasks = [:]

def createSetupStage(buildProperties) {
  return stage("Setup build") {
    echo "Setup build"
    
    echo "Properties: ${buildProperties}"
    
    for(itJob in buildProperties.dockerJobs) {
      
      def isCurrentImageBranch = repositoryUtils.containsCurrentBranch(itJob.imageName)
      def imageId = "${buildProperties.dockerHub.user}/${itJob.imageName}"
      def localImageTag = "${env.BRANCH_NAME}_${env.BUILD_NUMBER}".replaceAll('/', '-')
      def localImageId = "${imageId}:${localImageTag}"

      if (isBuildRequired(isCurrentImageBranch) == true) {
        buildTasks[itJob.imageName] = dockerStep.buildImage(localImageId, itJob.dockerfilePath, isRebuildRequired())
      }
      
      def remoteImageTag = dockerUtils.tagLocalBuild()
      
      if (repositoryUtils.isLatestBranch() == true) {
        remoteImageTag = dockerUtils.tagLatest()
      }
      else if (repositoryUtils.isStableBranch() == true) {
        remoteImageTag = dockerUtils.tagStable()
      }
      else if (repositoryUtils.isReleaseBranch() == true) {
        def releaseTag = evaluateReleaseTag(repositoryUtils.currentBuildBranch(), itJob.imageName)
        remoteImageTag = releaseTag != null ? releaseTag : dockerUtils.tagLatest()
      }
      
      if (isPushRequired(isCurrentImageBranch) == true) {
        pushTasks[itJob.imageName] = dockerStep.pushImage(localImageId, remoteImageTag)
      }
      
      postTasks[itJob.imageName] = dockerStep.removeImage(imageId, localImageTag, remoteImageTag)
    }
  }
}

def createParallelSteps() {
  return docker.withServer(env.DEFAULT_DOCKER_HOST_CONNECTION, 'default-docker-host-credentials') {
    try {
      stage("Build") {
        parallel buildTasks
      }
      stage("Push") {
        parallel pushTasks
      }
    }
    finally {
      stage("Clean up") {
        parallel postTasks
      }
    }
  }
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