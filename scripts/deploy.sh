# /bin/sh

failure() {
  if [ $? -ne 0 ]; then
    echo "[ERROR] ${0}"
    exit 1
  fi
}

./gradlew installdist
failure "Gradle build failed"

docker build -t calendar-poll .
failure "Docker image build failure"

image=$(docker images | awk '{print $3}' | awk 'NR==2')

aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 759614112154.dkr.ecr.eu-west-1.amazonaws.com
failure "AWS login failure"

docker tag ${image} 759614112154.dkr.ecr.eu-west-1.amazonaws.com/calendar-poll:tag
docker push  759614112154.dkr.ecr.eu-west-1.amazonaws.com/calendar-poll:tag
failure "Docker push failed"

aws ecs update-service --cluster default --service calendar-poll-service --force-new-deployment | cat
failure "Deployment failed"

echo "[INFO] Image successfully deployed"


