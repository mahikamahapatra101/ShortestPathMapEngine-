compileServer:
        javac -cp ../junit5.jar *.java

startServer: compileServer
        sudo java WebApp 80

runAllTests: compileServer
        java -jar ../junit5.jar --class-path . --select-class=FrontendTests --select-class=BackendTests

clean:
        rm -f *.class