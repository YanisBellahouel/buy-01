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

        stage('Build Backend (Product Service)') {
            steps {
                dir('microservices/product-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Backend Tests - User Service (JUnit)') {
            steps {
                dir('microservices/user-service') {
                    sh 'mvn test'
                }
            }
        }

        stage('Backend Tests - Product Service (JUnit)') {
            steps {
                dir('microservices/product-service') {
                    sh 'mvn test'
                }
            }
        }

        stage('Frontend Tests (Angular - Karma)') {
            steps {
                echo '⏭️ Frontend tests skipped in CI pipeline'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose build'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        failure {
            echo '❌ Pipeline failed – rollback triggered'
            sh 'docker-compose down || true'
        }
        success {
            echo '✅ Deployment successful'
        }
    }
}
