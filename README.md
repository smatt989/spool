# Scalatra Template #

This is a scalatra template with a connection to H2 db with Slick and serving up a react/redux web front end.

## Build & Run ##

```sh
$ cd spool
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

For first time use, you can create and populate the db by visiting in order:
 [http://localhost:8080/db/create-tables](http://localhost:8080/db/create-tables)
 [http://localhost:8080/db/load-tables](http://localhost:8080/db/load-tables)

From there, you can visit [http://localhost:8080/adventures/1](http://localhost:8080/adventures/1) and [http://localhost:8080/adventures/1/waypoints](http://localhost:8080/adventures/1/waypoints) to view the first adventure and its waypoints respectively.

