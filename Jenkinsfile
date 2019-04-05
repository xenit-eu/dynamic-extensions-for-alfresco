pipeline {
    agent any

    stages {

        stage('Setting version') {
            steps {
                sh "./gradlew makeVersionFile"
                script {
                    def version = readFile('build/version')
                    currentBuild.displayName = "${version} (#${env.BUILD_NUMBER})"
                }
            }
        }

        stage("Clean") {
            steps {
                sh "./gradlew clean"
            }
        }

        stage("Assemble") {
            steps {
                sh "./gradlew assemble"
            }
        }

        stage("Test") {
            steps {
                sh "./gradlew test"
            }
        }

        stage("Integration Test") {
            when {
                anyOf {
                    branch "master*"
                    changeRequest target: 'master*', comparator: 'GLOB'
                }
            }
            steps {
                sh "./gradlew integrationTest -Penterprise"
            }
        }

        stage('Publish Jars & Amps') {
            when {
                anyOf {
                    branch "master*"
                }
            }
            environment {
                SONNATYPE_CREDENTIALS = credentials('sonatype')
                GPGPASSPHRASE = credentials('gpgpassphrase')
            }
            steps {
                script {
                    sh "./gradlew uploadArchives -Pde_publish_username=${SONNATYPE_CREDENTIALS_USR} -Pde_publish_password=${SONNATYPE_CREDENTIALS_PSW} -PkeyId=DF8285F0 -Ppassword=${GPGPASSPHRASE} -PsecretKeyRingFile=/var/jenkins_home/secring.gpg"
                }
            }
        }
    }


    post {
        aborted {
            sh "./gradlew composeDownForced -Penterprise"
        }
        failure {
            sh "./gradlew composeDownForced -Penterprise"
        }
        success {
            archiveArtifacts artifacts: '**/build/amps/*.amp'
        }
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }
}


