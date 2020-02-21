
Google Hashcode 2020
=============================

Alfresco Search Services solution for [Google Hashcode 2020](https://hashcode.withgoogle.com/).

**Strategy SHORTER_SIGNUPDAYS_MORE_BOOKS**

Taking the first library having a shorter signup process and the greater books available to scan

**Scoring**

```
A – example                        21
B – read on                 5,437,900
C – incunabula              5,440,614
D – tough choices           4,412,005
E – so many books           3,208,139
F – libraries of the world  2,685,997
--------------------------------------
Total score                21,184,676
```

**Strategy MORE_VALUABLE_LIBRARY_FIRST**

Taking the library having the maximum value. This value is calculated for every Library based in the total scoring provided by the books available and adjusted by the amount of time required to produce the scoring.

**Scoring**

```
A – example                        21
B – read on                 5,437,900
C – incunabula              5,653,771
D – tough choices           4,305,015
E – so many books           1,523,129
F – libraries of the world  4,360,778
--------------------------------------
Total score                21,280,614
```

Combining strategies will produce **23,402,225 points** in Google Hash Code Jugde System.

*Note that a more refined strategy can be designed to obtain higher scores*

Compiling
---------

```bash
$ mvn clean package
```

Running
-------

```bash
$ java -jar target/hashcode-2020-1.0.0.jar \
  --fileIn=in/a_example.in \
  --fileOut=a_example.out \
  --strategy=0
```
Available strategies:

* 0: SHORTER_SIGNUPDAYS_MORE_BOOKS
* 1: MORE_VALUABLE_LIBRARY_FIRST

Generating ZIP Source file
--------------------------

```bash
$ ./zip.sh

$ ls *.zip
src.zip
```
