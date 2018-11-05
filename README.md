# MAAS Project - Team-TRK

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b65a987014c498da2df253ff30a0d4a)](https://app.codacy.com/app/tmeule2s/ws18-project-team-trk?utm_source=github.com&utm_medium=referral&utm_content=HBRS-MAAS/ws18-project-team-trk&utm_campaign=Badge_Grade_Dashboard)
Master [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=master)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

Develop [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=develop)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

The project contains a simple Jade client that tries to buy breads from a bakery. 
The bakery responses to the request with a random price. 
The buyer chooses the response with the lowest price and places the actual order.
The bakery accepts the order and sends a response.
The buyer gets the final response and closes.

## Team Members
* Robert Seboldt     - [@Umkipp](https://github.com/Umkipp)
* Kirill Schreiner   - [@kschre](https://github.com/kschre)
* Tim van der Meulen - [@tmeule2s](https://github.com/tmeule2s)

## Dependencies
* JADE v.4.5.0
* ...

## How to run
Just install gradle and run:

    gradle run

It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    gradle clean

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
