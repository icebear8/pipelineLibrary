
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
    if (utils.isImageProcessingRequired(itJob.imageName, config.buildJobs)) {
      buildTasks[itJob.imageName] = dockerBuild.buildImage(config.dockerRegistryUser, itJob.imageName, itJob.dockerfilePath)
    }
  }

  return buildTasks
}

// Prepares multiple docker images push tasks with parameters
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
    if (utils.isImageProcessingRequired(itJob.imageName, config.buildJobs)) {
      pushTasks[itJob.imageName] = dockerPush.pushImage(config.dockerRegistryUser, itJob.imageName)
    }
  }

  return pushTasks
}

// Prepares multiple docker images remove tasks with parameters
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
    if (utils.isImageProcessingRequired(itJob.imageName, config.buildJobs)) {
      removeTasks[itJob.imageName] = dockerRemove.removeImage(config.dockerRegistryUser, itJob.imageName)
    }
  }

  return removeTasks
}

// Prepare cleanup task to remove all unused images
def setupClenupAllUnusedTask(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
  
  def removeTasks = [:]
  
  def dockerRemove = new icebear8.docker.removeSteps()
  removeTasks["images"] = dockerRemove.removeAllUnusedImages()
  removeTasks["containers"] = dockerRemove.removeUnusedContainers()
  
  return removeTasks
}
