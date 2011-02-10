Grid-Blender
============

A client to submit blender jobs to the grid. Supports spitting up one big render job into multiple smaller one to get results back quicker.

### Prerequisites ###

In order to build the backend from the git sources, you need: 

- Sun Java Development Kit (version ≥ 6)
- [git](http://git-scm.com) 
- [Apache Maven](http://maven.apache.org) (version >=2)


### Checking out sourcecode ###

 `git clone git://github.com/grisu/grid-blender.git`

### Building Grid blender using Maven ###

To build one of the above modules, cd into the module root directory of the module to build and execute: 

    cd grid-blender
    mvn clean install

This will build a war file that can be deployed into a container and also a deb file that can be installed on a Debian based machine.


Running the client
-----------------------------

This is a grid client application to submit blender render jobs to the grid. The client splits up the render job into small jobs (one job per frame to render) and submits them to multiple locations on the grid. This way the user usually gets results back from the grid much faster than he would if he would render the job on his desktop machine. 

### Prerequisites ###

* Sun Java (version >= 6)
* the [Grid blender client](https://code.arcs.org.au/hudson/me/my-views/view/My%20stuff/job/Grisu-clients-SNAPSHOT/lastSuccessfulBuild/artifact/blender-client/target/grid-blender.jar)

### Usage ###

#### GUI mode ####

Double click the `grid-blender.jar` file or start it via:

    java -jar grid-blender.jar
    
#### Commandline mode ####

This is how you start the application:

    java -jar grid-blender.jar -m <mode> <mode-dependent-parameters>

##### Submit mode #####

To submit a blender job, you need a .blend file. Also you need to know the first and last frame you want to render. And also you need to guess a value for the walltime of each of the jobs. HPC queueing systems need you to provide a sort of cut-of-time in order to be able to know when to run your job. This cut-of-time is the maximum time you reckon the most complex frame of your render model will take. It's a bit hard to figure that out, especially if you don't know on which kind of processor the job will end up. So you need to choose a big enough number. But don't make it too big, since the bigger the walltime, the longer it usually takes for the job to get started on the cluster.

Ok, here we go, this is how you usually would submit a blender job to the grid (assuming authentication is taken care of):

    java -jar grid-blender.jar -m submit -u <the_myproxy_username> \
    -b /home/markus/Desktop/CubesTest.blend -s 1 -e 40 -F PNG -o <output_name> \
    -n <jobname> --verbose --vo <vo_to_use> -w 120 --forceKill

You need to use a unique jobname for each of your jobs, since this will be the handle you need to access the job later on. The `-b`, `-s`, `-e`, `-F` and `-o` options are more or less equivalent to the arguments the blender commandline app uses (Don't use # in the -o option like you would on the normal blender commandline — somehow that doesn't work over the grid). `--verbose` is recommended for now, in order to see what's actually happening. The `--vo` options indicates the VO you want to use to submit the job. For example, for a VPAC user that would probably `--vo /ARCS/VPAC` (unless you are in other VOs as well. `-w` is the maximum walltime for one frame in minutes. As said above, select a save value, otherwise some of your frames might be killed mid-run. The `--forceKill` option will kill and clean up jobdirectories of a (possible) earlier job with the same jobname. It's always a good idea to clean up after you don't need a job anymore. 

##### Check mode #####

The check mode can be used in 2 ways: as one off run or as a loop that runs until the job is finished.

To run it just to check the current status of the job, you would do something like:

    java -jar grid-blender.jar -m check -u <the myproxy_username> \
    -n <jobname> --detailed --status

This just prints out the number of jobs that are running, waiting and finished. Also, the `--detailed` options makes the client to print out the status and submission location of each of the (per-frame) jobs.

To monitor the job in a loop, this command would be used:

    java -jar grid-blender.jar -m check -u <the myproxy_username> \
    -n <jobname> --detailed --status -l <check_every_xxx_minutes> \
    -d /home/markus/Desktop/test/

The `-l` option tells the client to get the status of the job until all of the (per-frame) jobs are finished (successful or not). You have to also provide a number which indicates the number of minutes to wait inbetween checks. The above command also uses the `-d` option. This makes the client download all of the finished frames into the specified directory.


