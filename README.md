# <img src="https://www.workinblue.fr/wp-content/uploads/2018/03/Logo.png" width="72"> ENS Rennes - Optimization

> Optimization is a solver for P2P energetic problem.

---
# Description

This project is a solver to minimize P2P energetic problem based on a [European dataset](https://github.com/DTU-ELMA/European_Dataset) releases in this [journal](https://www.nature.com/articles/sdata2017175).

This project read all CSV file, execute script on the collected data, then display the result in a graphstream windows.

There are some arguments you can use to launch this programm :
* ```-debug false``` will print on terminal some debug logs if set to true (default false).
* ```-gui false``` will display data on graphstream windows if set to true (defautl false).
* ```-generator false``` will display on graphstream generators if set to true (default false).
* ```-date 2013-12-01 00:00:00``` a date to load on the CSV loads file.
* ```-algo scipy``` which algo to use, ``scipy`` is based on scipy optimize, when ``admm`` is a custom implementation.
* ```-worker 5``` number of threads used to compute the specified algo on dataset when date is set to ``all`` (default 2).

Date need to be formated as follow : `YYYY-MM-DD HH:00:00` with HH between 00 and 23.



---
# Setup

This project is compiled under java 8 in order to deploy it more easily. Make sure to install Java 8 and set java.exe to your PATH variables.

Download from this [permalink](https://doi.org/10.5281/zenodo.620228) the ``network_edges.csv`` and ``load_signal.csv`` that are too heavy for git and place these files under ``src/main/resources/``.


---
# Docker

To create a local docker image, make sure to execute ``gradlew clean jar`` first to generate the jar file, which is located under build/libs/.

Then run :

```docker build . -t optimization[:tag]```

You can run this image with custom jvm arguments through env var.

```docker run -d --name optimization --mount type=bind,source="/c/sqlite",target=/optimize/sqlite optimization[:tag] -debug false -date all -worker 5```
