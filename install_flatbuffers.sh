#!/bin/sh
set -ex
wget https://github.com/google/flatbuffers/archive/v1.1.0.tar.gz -O flatbuffers.tar.gz
tar -xzvf flatbuffers.tar.gz
cd flatbuffers/java && mvn clean install
