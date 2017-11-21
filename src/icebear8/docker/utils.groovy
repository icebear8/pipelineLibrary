package icebear8.docker

def evaluateJobBuildTag() {
  return "${buildUtils.getCurrentBuildBranch()}_${buildUtils.getCurrentBuildNumber()}".replaceAll('/', '-')
}
