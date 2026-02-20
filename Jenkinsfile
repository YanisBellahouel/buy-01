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
    success {
        echo '✅ Deployment successful'
        emailext (
            subject: "✅ SUCCESS - Build ${env.JOB_NAME}",
            body: "Le build a réussi 🎉\n\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_NUMBER}",
            to: "yanis.bellahouel76@gmail.com"
			from: "yanis.bellahouel76@gmail.com"
        )
    }
    failure {
        echo '❌ Pipeline failed – rollback triggered'
        sh 'docker-compose down || true'
        emailext (
            subject: "❌ FAILURE - Build ${env.JOB_NAME}",
            body: "Le build a échoué ❌\n\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_NUMBER}",
            to: "yanis.bellahouel76@gmail.com"
			from: "yanis.bellahouel76@gmail.com"
        )
    }
}
}
