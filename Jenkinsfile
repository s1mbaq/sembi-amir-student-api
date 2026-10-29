pipeline {
    agent { label 'sembi-amir-docker' }
    options { skipDefaultCheckout(true); disableConcurrentBuilds(); timeout(time: 25, unit: 'MINUTES') }
    triggers { pollSCM('H/2 * * * *') }
    environment { IMAGE = 'sembi-amir-student-api:v1.0-37693' }
    stages {
        stage('Checkout') {
            steps { checkout scm; echo 'Sembi Amir | IT2-2307 | Student ID 37693'; sh 'java -version && docker version && chmod +x gradlew' }
        }
        stage('Build') { steps { sh './gradlew --no-daemon clean assemble' } }
        stage('Test') { steps { sh './gradlew --no-daemon test' } }
        stage('Docker Build') { steps { sh 'docker build -t "$IMAGE" .' } }
        stage('Verify') { steps { sh 'docker image inspect "$IMAGE" --format "Image={{.Id}} Size={{.Size}} Created={{.Created}}"' } }
    }
    post {
        always { junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'; archiveArtifacts allowEmptyArchive: true, artifacts: 'build/reports/tests/test/**' }
    }
}
