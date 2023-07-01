# JSON Specialiser
[![Unit Test](https://github.com/BenMMcLean/JSON-Specialiser/actions/workflows/unit.yml/badge.svg)](https://github.com/BenMMcLean/JSON-Specialiser/actions/workflows/unit.yml)

JSON Specialiser (JSON-S) is a tool for taking in any structured
or unstructured JSON document and generating a client specialised
to a client. This is primary use case for the technology is to
manage large config files that are delivered to a variety of 
clients.

JSON-S documents are written exactly like regular JSON, in fact
a plain JSON string is entirely valid JSON-S code. JSON-S is
annotated with special "control codes", that instruct the compiler
how a document should be interpreted and modified. Control codes
are keys in a JSON object that start with a #, and contain 
arguments describing their function. An example of a control code
is like so:
```json
{
  "platformText": {
    "#selector": [
      {
        "claims": [["android"]],
        "child": "You are an Android user!"
      },
      {
        "child": "You are not an Android user :("
      }
    ]
  }
}
```

In the above example, given a client with the claim "android", the
output from the compiler would be
```json
{
  "platformText": "You are an Android user!"
}
```