#!/bin/sh

workloads[1]="0 100"
workloads[2]="5 90"
workloads[3]="15 70"
workloads[4]="10 90"
workloads[5]="0 90"
workloads[6]="30 70"
workloads[7]="0 70"

workloadsStr[1]="n0-p0-o100"
workloadsStr[2]="n5-p5-o90"
workloadsStr[3]="n15-p15-o70"
workloadsStr[4]="n0-p10-o90"
workloadsStr[5]="n10-p0-o90"
workloadsStr[6]="n0-p30-o70"
workloadsStr[7]="n30-p0-o70"


wait_until_finish() {
    pid3=$1
    echo "process is $pid3"
    LIMIT=30
    for ((j = 0; j < $LIMIT; ++j)); do
        kill -s 0 $pid3
        rc=$?
        if [[ $rc != 0 ]] ; then
            echo "returning"
            return;
        fi
        sleep 1s
    done
    kill -9 $pid3
}

mkdir auto-results
ant clean
ant compile
cd build/classes
for warehouses in 8 32
do
	for workload in {1..10}
	do
		for t in 1 2 4 8 16 24 32 48
		do
			for a in {1..3}
			do
				echo "running: warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a"
				java -Xms1G -Xmx16G -cp ../../libs/jvstm-2.3.jar:. pt.inesc.gsd.tpcc.Tpcc $warehouses ${workloads[$workload]} $t 15 >> ../../auto-results/$warehouses-${workloadsStr[$workload]}-$t-$a.data &
				pid=$!; wait_until_finish $pid; wait $pid; rc=$?
				if [[ $rc != 0 ]] ; then
		                    echo "Error within: running: warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a" >> ../../auto-results/error.out
                		fi
			done
		done
	done
done
