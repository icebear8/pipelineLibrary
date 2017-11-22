
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

// Prepares multiple docker images remove tasks wity parameters
// dockerRegistryUser
// buildJobs
def setupRemoveTasks(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def dockerRemove = new icebear8.docker.removeSteps()
  def utils = new icebear8.docker.utils()

  def removeTasks = [:]

  for(itJob in config.buildJobs) {
    if (utils.isImageProcessingRequired(itJob.imageName)) {
      removeTasks[itJob.imageName] = dockerRemove.removeImage(config.dockerRegistryUser, itJob.imageName)
    }
  }

  return removeTasks
}
