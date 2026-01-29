pipeline {
    agent any

    stages {

        stage('Build Backend (User Service)') {
            steps {
                dir('microservices/user-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Backend Tests (JUnit)') {
            steps {
                dir('microservices/user-service') {
                    sh 'mvn test'
                }
            }
        }

        stage('Frontend Tests (Angular - Karma)') {
            steps {
                dir('frontend/angular') {
                    sh 'npm install'
                    sh 'npm test -- --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker compose build'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }

    post {
        failure {
            echo '❌ Pipeline failed – rollback triggered'
            sh 'docker compose down || true'
        }
        success {
            echo '✅ Deployment successful'
        }
    }
}
