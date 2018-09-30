package icebear8.docker;

def pushImage(user, imageName) {
  def utils = new icebear8.docker.utils()

  def imageId = "${user}/${imageName}:${utils.evaluateJobBuildTag()}"
  def remoteTags = utils.evaluateRemoteTags(imageName)

  return {
    stage("Push image ${imageId}") {
      echo "Push image: ${imageId} to remote with tag ${remoteTags}"

      remoteTags.each {
        docker.image("${imageId}").push("${it}")
      }
    }
  }
}
