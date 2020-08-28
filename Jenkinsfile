pipeline {
  agent none
  stages {
    stage('Git') {
      steps {
        sleep 3
        git(url: 'https://github.com/suleymankotan/Gateway-Api.git', branch: 'master')
      }
    }

    stage('asd') {
      steps {
        sh '''echo aaaa
'''
      }
    }

    stage('sax') {
      steps {
        echo 'asd'
      }
    }

    stage('finish') {
      steps {
        mail(subject: 'Test', body: 'asdasad', from: 'jenkins@suleymankotan.com', to: 'slkotan85@gmail.com')
      }
    }

  }
}