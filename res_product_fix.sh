#!/bin/sh
cd ./res
sed -i '/product="tablet"/{s/^/<!-- /}' `grep -rl 'product="tablet"' *`
sed -i '/product="tablet"/{s/$/ -->/}' `grep -rl 'product="tablet"' *`
