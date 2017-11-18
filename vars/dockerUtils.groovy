
def getTagLatest() {
  return "latest"
}

def getTagStable() {
  return "stable"
}

def getTagLocalBuild() {
  return "build"
}

def getCurrentBuildTag() {
  return "${buildUtils.getCurrentBuildBranch()}_${buildUtils.getCurrentBuildNumber()}".replaceAll('/', '-')
}
