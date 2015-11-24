#!/bin/bash
string="ILDS"

if [ "$1" == "ILDS" ]
then
	cat ../FileFormats/complete-input-2 ../Demo/ILDS.output | java Validate
else	
	cat ../FileFormats/complete-input-2 ../Demo/Local.output | java Validate
fi
