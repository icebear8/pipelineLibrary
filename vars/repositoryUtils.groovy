
def getBranchLatest() {
  return "master"
}

def getBranchStable() {
  return "stable"
}

def getBranchRelease() {
  return "release"
}

def getCurrentBuildBranch() {
  
  if (env.BRANCH_NAME != null) {
    return env.BRANCH_NAME
  }
  
  return getBranchLatest()
}

def isLatestBranch() {
  return currentBuildBranch().contains(getBranchLatest())
}

def isReleaseBranch() {
  return currentBuildBranch().contains(getBranchRelease())
}

def isStableBranch() {
  return currentBuildBranch().contains(getBranchStable())
}

def containsCurrentBranch(name) {
  return currentBuildBranch().contains("${name}")
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
    echo "Checkout branch: ${currentBuildBranch()}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "*/${currentBuildBranch()}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'PruneStaleBranch']],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}