#!/usr/bin/bash

i=0

cat names.txt | while read line; do
	echo $line | sed "s,^, <string name=\"name$i\">," | sed 's,$,</string>,'
	let i=$i+1
done
