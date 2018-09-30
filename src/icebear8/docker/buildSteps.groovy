package icebear8.docker;

def buildImage(user, imageName, dockerFilePath) {

  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}:${utils.evaluateJobBuildTag()}"
  def buildArgs = "${dockerFilePath}"

  if (isRebuildRequired() == true) {
    buildArgs = "--no-cache --rm ${dockerFilePath}"
  }

  return {
    stage("Build image ${imageId}") {
      echo "Build image: ${imageId} with dockerfile ${dockerFilePath}"
      docker.build("${imageId}", "${buildArgs}")
    }
  }
}

def isRebuildRequired() {
  if (repositoryUtils.isReleaseBranch() == true) {
    return true
  }

  return false
}
