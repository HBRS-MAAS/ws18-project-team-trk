# MAAS Project - Team-TRK

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b65a987014c498da2df253ff30a0d4a)](https://app.codacy.com/app/tmeule2s/ws18-project-team-trk?utm_source=github.com&utm_medium=referral&utm_content=HBRS-MAAS/ws18-project-team-trk&utm_campaign=Badge_Grade_Dashboard)
Master [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=master)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

Develop [![Build Status](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk.svg?branch=develop)](https://travis-ci.org/HBRS-MAAS/ws18-project-team-trk)

The project contains a customer that tries to buy different products from a bakery and the bakery that contains multiple agents that process the order and the products. Next to these agents there is a timekeeper that controls the flow of time for theother agents.

## Team Members
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

To change the scenario you can pass a parameter with the path to the scenraio folder.

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
