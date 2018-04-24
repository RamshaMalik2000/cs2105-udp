if [ $# -eq 3 ]
	then
	    echo "Usage: test.sh <unreliNetPort> <rcvPort>"
		exit 1
fi

javac *.java

echo "Running Bob..."
java Bob $2 > bobOutput.txt & 2>&1

sleep 2

echo "Running UnreliNET..."
java UnreliNET 0.1 0.1 0.1 0.1 $1 $2 > unreliNetOutput.txt & 2>&1

sleep 2

echo "Running Alice, timeout after 180 seconds..."
timeout -t 180 java Alice testinput $1 testoutput > aliceOutput.txt 2>&1

sleep 3

PIDS=$( ps -fu $USER | grep -i "java" | grep -v grep | awk '{print $2}' )

for pid in $PIDS
do
	kill $pid
	echo "killed java process with PID "$pid
done

sleep 1

cmp testinput testoutput

if [ $? -ne 0 ]
then
	echo "Test failed".
else
	echo "Test passed".
fi

yes | rm testoutput
