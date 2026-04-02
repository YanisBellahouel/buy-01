pipeline {
    agent any

	tools {
        maven 'maven_3_9'
    }

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

        // 🔍 SONARQUBE ANALYSIS
		stage('SonarQube Analysis - User Service') {
			steps {
				dir('microservices/user-service') {
					withSonarQubeEnv('SonarQube') {
						sh '''
						mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
						-Dsonar.projectKey=sonarbuy \
						-Dsonar.projectName='sonarbuy' \
						-Dsonar.host.url=http://sonarqube:9000 \
						-Dsonar.token=sqp_36fe0921599ae93e8080c69750224240205bd02c
						'''
					}
				}
			}
		}
		

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
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
                sh 'docker compose build'
            }
        }

stage('Deploy') {
    steps {
        script {
            // Nettoyage des services applicatifs uniquement
            sh 'docker compose down --remove-orphans'
            sh 'docker rm -f mongodb zookeeper kafka || true'
            sh 'docker compose up -d'
        }
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