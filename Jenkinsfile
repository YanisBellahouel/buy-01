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
			mail(
				subject: "SUCCESS: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
				body: """Le build a réussi ✅
						Consultez les détails ici : ${env.BUILD_URL}""",
				to: "yanis.bellahouel76@gmail.com"
			)
		}
		failure {
			echo '❌ Pipeline failed'
			mail(
				subject: "FAILURE: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
				body: """Le build a échoué ❌
						Consultez les détails ici : ${env.BUILD_URL}""",
				to: "yanis.bellahouel76@gmail.com"
			)
		}
	}
}