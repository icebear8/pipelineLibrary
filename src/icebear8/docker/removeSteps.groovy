package icebear8.docker;

def removeImage(user, imageName) {
  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}"
  def localTag = utils.evaluateJobBuildTag()
  def remoteTag = utils.evaluateRemoteTag(imageName)

  return {
    stage("Remove image ${imageId}") {
      echo "Remove image: ${imageId}, tags: ${localTag}, ${remoteTag}"
      sh "docker rmi ${imageId}:${localTag}"
      sh "docker rmi ${imageId}:${remoteTag}"
    }
  }
}
