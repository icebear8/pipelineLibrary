
// Prepares multiple docker image build postTasks with the parameters
// dockerRegistryUser
// buildJobs
def setupBuildTasks(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def dockerBuild = new icebear8.docker.buildSteps()

  def buildTasks = [:]

  for(itJob in config.buildJobs) {
    def isCurrentImageBranch = repositoryUtils.containsCurrentBranch(itJob.imageName)
    def imageId = "${config.dockerRegistryUser}/${itJob.imageName}"
    def localImageId = "${imageId}:${dockerUtils.getCurrentBuildTag()}"

    if (dockerBuild.isBuildRequired(itJob.imageName) == true) {
      buildTasks[itJob.imageName] = dockerBuild.buildImage(config.dockerRegistryUser, itJob.imageName, itJob.dockerfilePath)
    }
  }

  return buildTasks
}


// Builds a docker image with the following parameters
// imageId
// dockerFilePath
// isRebuild
def build(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def buildArgs = "${config.dockerFilePath}"

  if (config.isRebuild == true) {
    buildArgs = "--no-cache --rm ${config.dockerFilePath}"
  }

  return {
    stage("Build image ${config.imageId}") {
      echo "Build image: ${config.imageId} with dockerfile ${config.dockerFilePath}"
      docker.build("${config.imageId}", "${buildArgs}")
    }
  }
}

// Pushes an image to the docker hub with the following parameters:
// imageId
// remoteTag
def push(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  return {
    stage("Push image ${config.imageId} to ${config.remoteTag}") {
      echo "Push image: ${config.imageId} to remote with tag ${config.remoteTag}"

      docker.image("${config.imageId}").push("${config.remoteTag}")
    }
  }
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
