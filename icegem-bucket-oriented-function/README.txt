This small project illustrates how to execute functions on a specified set of buckets.

Steps:

1. Compile the project:

mvn clean package

2. Run the project:

2.1 Start locator

gemfire start-locator -port=10355 -Dgemfire.mcast-port=0

2.2. Start two servers

cd <PROJECT_HOME>\icegem-bucket-oriented-function-server\target
.\server.(bat|sh)
.\server.(bat|sh)

2.3. Start a client

cd <PROJECT_HOME>\icegem-bucket-oriented-function-client\target
.\client.(bat|sh)

2.4 Press "Enter" button to start execution from client.

Check a client output - it should contain lines like these:

[...]

Execute function with filter key(s) : [0]
Total number of entries in buckets that satisfy filter keys: 0
Execute function with filter key(s) : [1]
Bucket with id = 49 contains entries with id: [1, 340, 114, 453, 227]
Total number of entries in buckets that satisfy filter keys: 5
Execute function with filter key(s) : [2]
Bucket with id = 50 contains entries with id: [341, 2, 115, 454, 228]
Total number of entries in buckets that satisfy filter keys: 5
Execute function with filter key(s) : [1, 2]
Bucket with id = 50 contains entries with id: [341, 2, 115, 454, 228]
Bucket with id = 49 contains entries with id: [1, 340, 114, 453, 227]
Total number of entries in buckets that satisfy filter keys: 10

[...]