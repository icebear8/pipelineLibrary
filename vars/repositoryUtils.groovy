
def branchLatest() {
  return "master"
}

def branchStable() {
  return "stable"
}

def branchRelease() {
  return "release"
}

def currentBuildBranch() {
  
  if (env.BRANCH_NAME != null) {
    return env.BRANCH_NAME
  }
  
  return branchLatest()
}

def containsCurrentBranch(name) {
  return currentBuildBranch().contains("${name}")
}

def isLatestBranch() {
  return currentBuildBranch().contains(branchLatest())
}

def isReleaseBranch() {
  return currentBuildBranch().contains(branchRelease())
}

def isStableBranch() {
  return currentBuildBranch().contains(branchStable())
}