
def getCurrentBuildBranch() {
  if (env.BRANCH_NAME != null) {
    return env.BRANCH_NAME
  }
  return repositoryUtils.getBranchLatest()
}

def getCurrentBuildNumber() {
  if (env.BUILD_NUMBER != null) {
    return env.BUILD_NUMBER
  }
  return '0'
}
