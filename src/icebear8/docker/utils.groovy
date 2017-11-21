package icebear8.docker

def evaluateJobBuildTag() {
  return "${buildUtils.getCurrentBuildBranch()}_${buildUtils.getCurrentBuildNumber()}".replaceAll('/', '-')
}

def evaluateRemoteTag(imageName) {
  def remoteTag = dockerUtils.getTagLocalBuild()

  if (repositoryUtils.isLatestBranch() == true) {
    remoteTag = dockerUtils.getTagLatest()
  }
  else if (repositoryUtils.isStableBranch() == true) {
    remoteTag = dockerUtils.getTagStable()
  }
  else if (repositoryUtils.isReleaseBranch() == true) {
    def releaseTag = evaluateReleaseTag(repositoryUtils.currentBuildBranch(), imageName)
    remoteTag = releaseTag != null ? releaseTag : dockerUtils.getTagLatest()
  }

  return remoteTag
}

def evaluateReleaseTag(releaseBranch, imageName) {
  def indexOfImage = releaseBranch.indexOf(imageName)

  if (indexOfImage < 0)
  {
    return null // exit if no valid release tag could be found
  }

  return releaseBranch.substring(indexOfImage + imageName.length() + 1) // +1 because of additional sign between image id and release tag
}

def isImageProcessingRequired(imageName) {
  // for release or stable branches only process if image name is mentioned in the branch name
  if ((repositoryUtils.isStableBranch() == true) || (repositoryUtils.isReleaseBranch() == true)) {
    return repositoryUtils.containsCurrentBranch(imageName)
  }

  return true
}
