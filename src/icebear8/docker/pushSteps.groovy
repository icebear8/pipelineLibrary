package icebear8.docker;

def pushImage(user, imageName) {
  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}:${utils.evaluateJobBuildTag()}"
  def remoteTag = utils.evaluateRemoteTag(imageName)

  return {
    stage("Push image ${imageId} to ${remoteTag}") {
      echo "Push image: ${imageId} to remote with tag ${remoteTag}"

      docker.image("${imageId}").push("${remoteTag}")
    }
  }
}
