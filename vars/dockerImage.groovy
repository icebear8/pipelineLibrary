
// Prepares multiple docker image build with the parameters
// dockerRegistryUser
// buildJobs
def setupBuildTasks(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def dockerBuild = new icebear8.docker.buildSteps()
  def utils = new icebear8.docker.utils()

  def buildTasks = [:]

  for(itJob in config.buildJobs) {
    if (utils.isImageProcessingRequired(itJob.imageName)) {
      buildTasks[itJob.imageName] = dockerBuild.buildImage(config.dockerRegistryUser, itJob.imageName, itJob.dockerfilePath)
    }
  }

  return buildTasks
}

// Prepares multiple docker images push tasks wity parameters
// dockerRegistryUser
// buildJobs
def setupPushTasks(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def dockerPush = new icebear8.docker.pushSteps()
  def utils = new icebear8.docker.utils()

  def pushTasks = [:]

  for(itJob in config.buildJobs) {
    if (utils.isImageProcessingRequired(itJob.imageName)) {
      pushTasks[itJob.imageName] = dockerPush.pushImage(config.dockerRegistryUser, itJob.imageName)
    }
  }

  return pushTasks
}

// Removes the local images with the following parameters:
// imageId
// localImageTag
// remoteImageTag
def removeLocal(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  return {
    stage("Remove image ${config.imageId}") {
      echo "Remove image: ${config.imageId}, tags: ${config.localImageTag}, ${config.remoteImageTag}"
      sh "docker rmi ${config.imageId}:${config.localImageTag}"
      sh "docker rmi ${config.imageId}:${config.remoteImageTag}"
    }
  }
}

def removeLocalFunc(imageId, localImageTag, remoteImageTag) {
  return {
    stage("Remove image ${imageId}") {
      echo "Remove image: ${imageId}, tags: ${localImageTag}, ${remoteImageTag}"
      sh "docker rmi ${imageId}:${localImageTag}"
      sh "docker rmi ${imageId}:${remoteImageTag}"
    }
  }
}
