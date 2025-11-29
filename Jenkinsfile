pipeline {
    agent any

    environment {
        REGISTRY = "registry.ru.yandex/bank"
        SERVICES = "accounts-service cash-service transfer-service notifications-service front-ui auth-server"
        DEV_NAMESPACE = "dev"
        PROD_NAMESPACE = "prod"
        HELM_RELEASE_DEV = "bank-app-dev"
        HELM_RELEASE_PROD = "bank-app-prod"
    }

    stages {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }

        stage("Build and Test All") {
            steps {
                sh "./gradlew clean test bootJar"
            }
        }

        stage("Build Images") {
            steps {
                script {
                    env.IMAGE_TAG = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                }
                sh """
                  for svc in ${SERVICES}; do
                    docker build -t ${REGISTRY}/$svc:${IMAGE_TAG} -f $svc/Dockerfile .
                  done
                """
            }
        }

        stage("Push Images") {
            steps {
                withCredentials([usernamePassword(credentialsId: "docker-registry-creds", usernameVariable: "DOCKER_USER", passwordVariable: "DOCKER_PASS")]) {
                    sh """
                      echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin ${REGISTRY}
                      for svc in ${SERVICES}; do
                        docker push ${REGISTRY}/$svc:${IMAGE_TAG}
                      done
                    """
                }
            }
        }

        stage("Deploy to Dev") {
            when {
                branch "main"
            }
            steps {
                sh """
                  helm upgrade --install ${HELM_RELEASE_DEV} helm \
                    --namespace ${DEV_NAMESPACE} --create-namespace \
                    --set accounts-service.image.tag=${IMAGE_TAG} \
                    --set cash-service.image.tag=${IMAGE_TAG} \
                    --set transfer-service.image.tag=${IMAGE_TAG} \
                    --set notifications-service.image.tag=${IMAGE_TAG} \
                    --set front-ui.image.tag=${IMAGE_TAG} \
                    --set auth-server.image.tag=${IMAGE_TAG}
                """
                sh """
                  helm test ${HELM_RELEASE_DEV} --namespace ${DEV_NAMESPACE}
                """
            }
        }

        stage("Deploy to Prod") {
            when {
                branch "main"
            }
            steps {
                script {
                    input message: "Deploy to PROD?", ok: "Deploy"
                }
                sh """
                  helm upgrade --install ${HELM_RELEASE_PROD} helm \
                    --namespace ${PROD_NAMESPACE} --create-namespace \
                    --set accounts-service.image.tag=${IMAGE_TAG} \
                    --set cash-service.image.tag=${IMAGE_TAG} \
                    --set transfer-service.image.tag=${IMAGE_TAG} \
                    --set notifications-service.image.tag=${IMAGE_TAG} \
                    --set front-ui.image.tag=${IMAGE_TAG} \
                    --set auth-server.image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
