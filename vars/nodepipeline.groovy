def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node('vbox-slave') {
        stage('Repository Clone') {
            git branch: 'main', credentialsId: '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', url: 'https://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@github.com/PasztorBence/calendarclient.git'
        }
        stage('Build') {
            configFileProvider([configFile(fileId: '2f0b42a7-b4e0-442c-b0b2-09e6792a3ac0', targetLocation: '.npmrc')]) { }
            sh 'npm install --registry=https://registry.npmjs.org/'
        }
        stage('Test') {
            sh 'npm test'
        }
        stage('Publish') {
            sh 'npm publish'
        }
        stage('Docker image Build') {
            dockerbuild('calendarclient')
        }
        stage('Docker Publish') {
            sh 'docker push 192.168.0.105:8085/calendarclientdocker:latest'
        }
    }
}
