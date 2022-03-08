def call(imagename){
  sh "docker build --tag 192.168.0.105:8085/${imagename} ."
}
