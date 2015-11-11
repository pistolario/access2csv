# access2csv

Simple program to extract data from Access databases into CSV files.

## Features

 * view the schema of the database
 * export some tables

## Examples

Dumping a schema:

    $ java -jar access2csv.jar -i myfile.accdb -s	
	CREATE TABLE Test(
		Id INT,
		Name TEXT,
	)
	CREATE TABLE Test2(
		Id INT,
		Name TEXT
	)
	
Export one table:

    $ java -jar access2csv.jar -i myfile.accdb -t Test
	1,"foo"
	2,"bar"

## Installation

Binaries are available at
https://github.com/pistolario/access2csv/releases, download a jar
file from there then use it as shown above.

### Compile from source

    $ git clone https://github.com/pistolario/access2csv.git
	$ cd access2csv
	$ ant
	
Now you should have an `access2csv.jar`, ready to go.

## Depenencies

 * [Jackess](http://jackcess.sourceforge.net/) - a pure Java library
   for reading from and writing to MS Access databases
 * [Jackess encrypt](http://jackcessencrypt.sourceforge.net) - Extension for
   using cyphered MS Access databases
 * [The bouncy Castle Crypto](http://www.bouncycastle.org/) Cryptographic library
 * [Commons-cli](https://commons.apache.org/proper/commons-cli/) - API for 
   parsing command line options
 * [opencsv](http://opencsv.sourceforge.net/) - CSV library

## Contributing

Use https://github.com/pistolario/access2csv to open issues or
pull requests.
