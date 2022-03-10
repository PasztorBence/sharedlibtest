def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node('vbox-slave') {
        stage('Repository Clone') {
            git branch: 'main', url: 'https://github.com/PasztorBence/mavenpipelinetest.git'
        }
        stage('Build/Publish') {
            mavenPom = readMavenPom 'pom.xml'
            withMaven(jdk: 'jdk8', mavenSettingsConfig: '8cc2cb63-74a8-4de8-937e-938ca4b32dc9') {
                sh 'mvn clean deploy -Dversion=${mavenPom.version}'
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