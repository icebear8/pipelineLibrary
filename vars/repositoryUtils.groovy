
def branchLatest() {
  return "master"
}

def branchStable() {
  return "stable"
}

def branchRelease() {
  return "release"
}

def currentBuildBranch() {
  
  if (env.BRANCH_NAME != null) {
    return env.BRANCH_NAME
  }
  
  return branchLatest()
}

def containsCurrentBranch(name) {
  return currentBuildBranch().contains("${name}")
}

def isLatestBranch() {
  return currentBuildBranch().contains(branchLatest())
}

def isReleaseBranch() {
  return currentBuildBranch().contains(branchRelease())
}

def isStableBranch() {
  return currentBuildBranch().contains(branchStable())
}

// Creates a checkout stage with the following parameters
// stageName
// branchName
// repoUrl
// repoCredentials
def checkoutStage(body) {

  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  stage("${config.stageName}") {
    echo "Checkout branch: ${config.branchName}"

    checkout([
      $class: 'GitSCM',
      branches: [[name: "*/${config.branchName}"]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'PruneStaleBranch']],
      submoduleCfg: [],
      userRemoteConfigs: [[url: "${config.repoUrl}", credentialsId: "${config.repoCredentials}"]]])
  }
}