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
  return branchTag + "-b${buildUtils.getCurrentBuildNumber()}"
}

def isImageProcessingRequired(imageName) {
  // for release or stable branches only process if image name is mentioned in the branch name
  if ((repositoryUtils.isStableBranch() == true) || (repositoryUtils.isReleaseBranch() == true)) {
    return repositoryUtils.containsCurrentBranch(imageName)
  }

  return true
}
