#!/bin/bash
string="ILDS"

if [ "$1" == "ILDS" ]
then
	cat ../FileFormats/add_times ../Demo/ILDS.output | java Validate 
else	
	cat ../FileFormats/add_times ../Demo/Local.output | java Validate
fi
