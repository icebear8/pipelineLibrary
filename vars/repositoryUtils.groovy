
def getBranchLatest() {
  return "master"
}

def getBranchStable() {
  return "stable"
}

def getBranchRelease() {
  return "release"
}

def isLatestBranch() {
  return buildUtils.getCurrentBuildBranch().contains(getBranchLatest())
}

def isReleaseBranch() {
  return buildUtils.getCurrentBuildBranch().contains(getBranchRelease())
}

def isStableBranch() {
  return buildUtils.getCurrentBuildBranch().contains(getBranchStable())
}

def containsCurrentBranch(name) {
  return buildUtils.getCurrentBuildBranch().contains("${name}")
}

// Returns the current git tag if available
// If there is no tag, null is returned
def getAvailableTagName() {
    commit = getCurrentCommitId()
    if (commit) {
        desc = sh(script: "git describe --tags ${commit}", returnStdout: true)?.trim()
        if (isValidTag(desc)) {
            return desc
        }
    }
    return null
}
 
def getCurrentCommitId() {
    return sh(script: 'git rev-parse HEAD', returnStdout: true)?.trim()
}
 
def isValidTag(desc) {
    match = desc =~ /.+-[0-9]+-g[0-9A-Fa-f]{6,}$/
    result = !match
    match = null // prevent serialisation
    return result
}

// Creates a checkout stage with the following parameters
// stageName
// branchName
// repoUrl
// repoCredentials
def checkoutBranch(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  stage("${config.stageName}") {
    echo "Checkout branch: ${config.branchName}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "${config.branchName}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'PruneStaleBranch']],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}

// Creates a checkout stage with the following parameters
// stageName
// branchName
// subDirectory
// repoUrl
// repoCredentials
def checkoutBranchToSubdir(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  stage("${config.stageName}") {
    echo "Checkout branch: ${config.branchName}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "${config.branchName}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [
        [$class: 'CleanBeforeCheckout'],
        [$class: 'PruneStaleBranch'],
        [$class: 'RelativeTargetDirectory', relativeTargetDir: "${config.subDirectory}"]],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}

// Creates a checkout stage with the following parameters
// stageName
// repoUrl
// repoCredentials
def checkoutCurrentBranch(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  stage("${config.stageName}") {
    echo "Checkout branch: ${buildUtils.getCurrentBuildBranch()}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "*/${buildUtils.getCurrentBuildBranch()}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'PruneStaleBranch']],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}


