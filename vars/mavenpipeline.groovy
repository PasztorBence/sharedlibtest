def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    properties([parameters([booleanParam(description: 'release-e', name: 'isRealease')])])
    node('vbox-slave') {
        boolean isReleaseBuild
        stage('Prepare'){
            try{
            isReleaseBuild = Boolean.parseBoolean(isRealease)
            } catch(MissingPropertyException e){
            isReleaseBuild = false
            }
        }
        stage('Repository Clone') {
            git branch: 'main', url: 'https://github.com/PasztorBence/mavenpipelinetest.git'
        }
        stage('Build/Publish') {
            def mavenPom = readMavenPom file: 'pom.xml'
            withMaven(jdk: 'jdk8', mavenSettingsConfig: '8cc2cb63-74a8-4de8-937e-938ca4b32dc9') {
                if(isReleaseBuild){
                    def version = mavenPom.version
                    def versionWithoutSnap = version.replace("-SNAPSHOT", "")
                    sh "mvn versions:set -DnewVersion=${versionWithoutSnap}"
                }
                sh "mvn clean deploy"
            }
        }
        stage('Docker image Build') {
            dockerbuild('mavenpipelinetest')
        }
        stage('Docker Publish') {
            sh 'docker push 192.168.0.105:8085/mavenpipelinetest:latest'
        }
    }
}
