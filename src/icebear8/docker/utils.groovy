package icebear8.docker;

def getTagLatest() {
  return "latest"
}

def getTagStable() {
  return "stable"
}

def getTagBuild() {
  return "build"
}

def evaluateJobBuildTag() {
  return "${buildUtils.getCurrentBuildBranch()}-b${buildUtils.getCurrentBuildNumber()}".replaceAll('/', '-')
}

def evaluateRemoteTag(imageName) {
  def remoteTag = getTagBuild()

  if (repositoryUtils.isLatestBranch() == true) {
    remoteTag = getTagLatest()
  }
  else if (repositoryUtils.isStableBranch() == true) {
    remoteTag = getTagStable()
  }
  else if (repositoryUtils.isReleaseBranch() == true) {
    def releaseTag = evaluateReleaseTag(buildUtils.getCurrentBuildBranch(), imageName)
    remoteTag = releaseTag != null ? releaseTag : getTagLatest()
  }

  return remoteTag
}

def evaluateReleaseTag(releaseBranch, imageName) {
  def indexOfImage = releaseBranch.indexOf(imageName)

  if (indexOfImage < 0)
  {
    return null // exit if no valid release tag could be found
  }

  def branchTag = releaseBranch.substring(indexOfImage + imageName.length() + 1) // +1 because of additional sign between image id and release tag
  return branchTag
}

def isImageProcessingRequired(currentImageName, jobList) {
  // If the current branch contains an image name, only this image is built
  // Otherwise all images are built
  
  for (itImageJob in jobList) {
    if (repositoryUtils.containsCurrentBranch(itImageJob.imageName) == true) {
      // The branch name contains one of the image names
      // Check whether the current image is the one to build
      return repositoryUtils.containsCurrentBranch(currentImageName)
    }
  }
  
  // Branch does not contain an image name => build all images
  return true
}
