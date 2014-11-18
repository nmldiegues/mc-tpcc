#!/bin/sh

workloads[1]="0 95"
workloads[2]="0 90"
workloads[3]="0 80"
workloads[4]="0 50"
workloads[5]="0 0"

workloadsStr[1]="n5-p0-o95"
workloadsStr[2]="n10-p0-o90"
workloadsStr[3]="n20-p0-o80"
workloadsStr[4]="n50-p0-o50"
workloadsStr[5]="n100-p0-o0"

topTxs[1]="1 0"
topTxs[2]="2 0"
topTxs[4]="4 0"
topTxs[8]="8 0"
topTxs[16]="8 2"
topTxs[24]="8 3"
topTxs[32]="8 4"
topTxs[48]="8 6"

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
for warehouses in 1 #8
do
	for workload in {1..5}
	do
		for t in 16 24 32 48 #1 2 4 8 16 24 32 48
		do
			for a in 1 #{1..3}
			do
				echo "running: !TopLevel! warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a"
				java -Xms16G -Xmx64G -cp ../../libs/jvstm-2.3.jar:. pt.inesc.gsd.tpcc.Tpcc $warehouses ${workloads[$workload]} $t 0 15 >> ../../auto-results/$warehouses-tl-${workloadsStr[$workload]}-$t-$a.data &
				pid=$!; wait_until_finish $pid; wait $pid; rc=$?
				if [[ $rc != 0 ]] ; then
		                    echo "Error within: running: !TopLevel! warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a" >> ../../auto-results/error.out
                		fi
				
                                echo "running: !ParNest! warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a"
                                java -Xms16G -Xmx64G -cp ../../libs/jvstm-2.3.jar:. pt.inesc.gsd.tpcc.Tpcc $warehouses ${workloads[$workload]} ${topTxs[$t]} 15 >> ../../auto-results/$warehouses-pr-${workloadsStr[$workload]}-$t-$a.data &
                                pid=$!; wait_until_finish $pid; wait $pid; rc=$?
                                if [[ $rc != 0 ]] ; then
                                    echo "Error within: running: !ParNest! warehouses $warehouses | workload ${workloads[$workload]} | threads $t | attempt $a" >> ../../auto-results/error.out
                                fi

			done
		done
	done
done
