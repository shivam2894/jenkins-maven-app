pipeline {
     agent any
     stages {
        stage("Build project and save tar file") {
            steps {
				sh "docker build -t backend1 ."
				sh "docker image save -o maven-app.tar backend1"
            }
        }
        stage("Send tar file to Java-backend server") {
            steps {
				sh "scp -i /key/cdac.pem maven-app.tar ubuntu@34.211.143.214:/home/ubuntu/"
                sh "ssh -i /key/cdac.pem ubuntu@34.211.143.214 -yes  sudo docker load < maven-app.tar"              
            }
        }
		stage("Stop and Remove the current docker image") {
            steps {
                sh "ssh -i /key/cdac.pem ubuntu@34.211.143.214 -yes  sudo docker stop nattukakabackend1"
				sh "ssh -i /key/cdac.pem ubuntu@34.211.143.214 -yes  sudo docker rm nattukakabackend1"			
            }
        }
		stage("Run the new image") {
            steps {
				sh "ssh -i /key/cdac.pem ubuntu@34.211.143.214 -yes  sudo docker run --name nattukakabackend1 -p 8080:8080 -d backend1"				
            }
        }
    }
}