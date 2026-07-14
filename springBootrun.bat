@echo off
:: 1. 압축을 푼 JDK의 bin 경로를 현재 실행 세션의 PATH에 임시로 추가합니다.
set "JAVA_HOME=D:\util\jdk\jdk-17.0.12"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo --- Java Version ---
java -version
echo --------------------

:: Maven 래퍼로 스프링 부트 실행
java -jar backend/target/demo-0.0.1-SNAPSHOT.jar
pause
