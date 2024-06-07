# Basic JVM Clojure lambda setup

Take a look at the [reitit-function-url](https://github.com/juxt/plain-clojure-lambda/tree/reitit-function-url)
branch for an example of a shadow-cljs frontend + reitit backend all in one lambda!

## Build

To build an uberjar:

```bash
$ clj -T:build uber
```

Then upload the `target/lambda-*.jar` to your [lambda function](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html#java-package-console)
and off you go!
