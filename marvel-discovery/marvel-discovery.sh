#!/bin/sh
#add by yongjie.teng on 20180903

#应用名称
APP_NAME=marvel-discovery

#需要启动的Java主程序（main方法类）
APP_MAINCLASS=marvel-discovery-0.0.1-SNAPSHOT.jar

#工作目录
WORK_DIR=/opt/marvel

#java虚拟机启动参数
# JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m -XX:-OmitStackTraceInFastThrow"

#杀spring-boot进程
#开始kill 应用进程...
echo "==================================================================="

appid=0
checkpid() {
    appid=$($JAVA_HOME/bin/jps -l | grep $APP_MAINCLASS | awk '{print $1}')
}

echo "Start kill $APP_NAME process..."
checkpid
if [ "$appid" ]; then
#存在应用进程,PID: $appid
echo "Exist $APP_NAME process, PID: $appid"
kill -9 $appid
sleep 3
#应用进程已kill
echo "$APP_NAME process has been killed"
fi

cd $WORK_DIR

#开始重启...
echo "Restarting $APP_MAINCLASS"

nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $APP_MAINCLASS >/var/log/marvel/$APP_NAME.log &

sleep 5
checkpid
echo "$APP_NAME has been started. PID: $appid"
echo "==================================================================="
