pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                sh 'cd backend && mvn clean package -DskipTests'
            }
        }

        stage('Backend Tests (JUnit)') {
            steps {
                sh 'cd backend && mvn test'
            }
        }

		stage('Frontend Tests (Angular - Karma)') {
			steps {
				sh '''
					cd frontend/angular
					npm install
					npm test -- --watch=false --browsers=ChromeHeadless
				'''
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
            echo '✅ Build, tests and deployment succeeded'
        }
        failure {
            echo '❌ Pipeline failed – rollback triggered'
            sh 'docker-compose down'
        }
    }
}
