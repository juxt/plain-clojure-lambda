# JVM Clojure lambda setup w/ Reitit handler for function URLs

## Setup

To install the javascript dependencies:

```bash
$ yarn install
```

## Build

To build an uberjar:

```bash
$ clj -T:build uber
```

Then upload the `target/lambda-*.jar` to your [lambda function](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html#java-package-console)
and off you go!

To use, create a [function URL](https://docs.aws.amazon.com/lambda/latest/dg/urls-configuration.html#create-url-console)
then visit the given url.

## Dev

To develop this locally, start shadow-cljs:

```bash
$ npx shadow-cljs watch app
```

Then either start your repl in the ordinary way (using the `:dev` alias) or connect to the repl that shadow-cljs opens.

You can then execute `(user/go!)` to start or restart a local webserver on http://localhost:8000
