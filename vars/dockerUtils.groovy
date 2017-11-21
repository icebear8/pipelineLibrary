
def getTagLatest() {
  return "latest"
}

def getTagStable() {
  return "stable"
}

// deprectated
def getTagLocalBuild() {
  return getTagBuild()
}

def getTagBuild() {
  return "build"
}

// deprecated
def getCurrentBuildTag() {
  return "${buildUtils.getCurrentBuildBranch()}_${buildUtils.getCurrentBuildNumber()}".replaceAll('/', '-')
}
