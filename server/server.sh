#!/bin/sh
start(){
 nohup java -jar server-0.0.1-SNAPSHOT.jar &
}
stop(){
 PID=$(ps -ef | grep server-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{ print $2 }')
 if [ -z "$PID" ]
 then
 echo Application is already stopped
 else
 echo kill $PID
 kill $PID
 fi
}
log(){
 tail -f nohup.out
}
case $1 in
 start)
  start
 ;;
 stop)
  stop
 ;;
 log)
  log
 ;;
 restart)
  $0 stop
  sleep 5
  $0 start
 ;;
 *)
  echo "Usage: {start|stop|restart}"
 ;;
esac
exit 0