package docker

def REPO_LATEST_BRANCH = 'master'
def REPO_STABLE_BRANCH = 'stable'
def REPO_RELEASE_BRANCH = 'release'

def DOCKER_TAG_LATEST = 'latest'
def DOCKER_TAG_STABLE = 'stable'
def DOCKER_NO_TAG_BUILD = 'build'

def helloPipelineLibrary() {
  println "Hello from the library"
}