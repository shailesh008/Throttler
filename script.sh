#!/bin/bash
#Read the proc file and parse the output

computeCPU() {
	uptime=$(awk '{print $1}' /proc/uptime)
    status=$(awk '{print}' /proc/$pid/stat)
	y=$(echo $status | awk '{print $2}')
    utime=$(echo $status | awk '{print $14}')
    stime=$(echo $status | awk '{print $15}')
    cutime=$(echo $status | awk '{print $16}')
    cstime=$(echo $status | awk '{print $17}')
    starttime=$(echo $status | awk '{print $22}')
	pages=$(echo $status | awk '{print $24}')
    hertz=$(getconf CLK_TCK)
    totaltime=$((utime + stime + cutime + cstime))
    starter=$(expr $starttime / $hertz)
    uptime=$(printf "%.0f" $uptime)
    timeelapsed=$(awk "BEGIN {print $uptime-$starter}")
    cpu=$(awk "BEGIN {print $totaltime*100/$hertz}")
    cpupercent=$(awk "BEGIN {print $cpu/$timeelapsed}")
}

computeMemory() {
	memTotal=$(cat /proc/meminfo | grep MemTotal | awk '{print $2}')
	memory=$(awk "BEGIN {print $pages*4/$memTotal}")
}

getMetrics() {
	pid=$1
	computeCPU
	computeMemory
    echo "{\"pid\" : \"$pid\",\"cpu\" : \"$cpupercent\", \"memory\" : \"$memory\"}"
    #echo "{\"pid\" : \"$pid\",\"cpu\" : \"10\", \"memory\" : \"10\"}"
}

debug=2
getMetrics $1