# Basic JVM Clojure lambda setup

## Build

To build an uberjar:

```bash
$ clj -T:build uber
```

Then upload the `target/lambda-*.jar` to your [lambda function](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html#java-package-console)
and off you go!
