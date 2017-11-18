
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
  return getCurrentBuildBranch().contains(getBranchLatest())
}

def isReleaseBranch() {
  return getCurrentBuildBranch().contains(getBranchRelease())
}

def isStableBranch() {
  return getCurrentBuildBranch().contains(getBranchStable())
}

def containsCurrentBranch(name) {
  return getCurrentBuildBranch().contains("${name}")
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
    echo "Checkout branch: ${getCurrentBuildBranch()}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "*/${getCurrentBuildBranch()}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'PruneStaleBranch']],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}