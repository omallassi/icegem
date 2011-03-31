To run this util you need to perform following commands in icegem-cache-utils module directory:

mvn assembly:assembly

Then you will get icegem-cache-utils-<version>.zip artifact. Unzip it and run updater.sh script in it.

Examples:

./updater.sh -a -l localhost[10355] -s localhost[40404] - this will cause updater to update all regions
                                                          in distributed system with locator "localhost[10355]"
                                                          and cache server "localhost[40404]"

./updater.sh -r region1,region2 -c -l localhost[10355] -s localhost[40404] - this will cause updater to update regions
                                                          region1 and region2 with all its' subregions
                                                          in distributed system with locator "localhost[10355]" 
                                                          and cache server "localhost[40404]"