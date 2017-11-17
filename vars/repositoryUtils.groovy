
def branchLatest() {
  return "master"
}

def branchStable() {
  return "stable"
}

def branchRelease() {
  return "release"
}

def evaluateCurrentBuildBranch() {
  
  if (env.BRANCH_NAME != null) {
    return env.BRANCH_NAME
  }
  
  return branchLatest()
}
