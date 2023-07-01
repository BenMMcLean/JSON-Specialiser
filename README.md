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

## Codes
### #selector
The selector control code allows the compiler to choose between
any number of options based on information about the client. By
default, the code only evaluates based on "claims", a list of
strings that represent the client, but the compiler can be extended
with custom fields (for example, a field matching max and min
version could be implemented).

Claims are grouped in an all-any relationship, where the nested
arrays represent a group of claims that must match the client entirely,
and the top level arrays just need any of the nested conditions to 
match. For example

Given a client that has the claims `["claim1", "claim2"]`, 
`[["claim1", "claim3"], ["claim4"]]` would not match, since the user 
doesn't have both claim (1 && 4) || 5. On the other hand, 
`[["claim1"], ["claim3"]]` would match, since the user has claim1.

The arguments of selector are a list of options, those options
constituting some number of conditions (or none, for an else branch)
and a child that will be inserted after evaluation. Options are in
priority order, the first option that matches will be returned.

An example of a selector can be seen above.

### #include
Include allows the document to insert another JSON document in place.
For this to work, the compiler must be provided a FileAccessStrategy.
An example of an include is as follows:
```json
{
  "#include": {
    "file": "otherfile.json"
  }
}
```

This feature is useful for managing large and complex files.

### #combine
Combine flattens any nested set of arrays xor objects. It is used like
so:
```json
{
  "#combine": [
    {
      "key": "value"
    },
    {
      "key1": "value1"
    }
  ]
}
```
which would output

```json
{
  "key": "value",
  "key1": "value1"
}
```

Arrays work similarly
```json
{
  "#combine": [
    ["thing1", "thing2"],
    ["thing3"]
  ]
}
```
would output
```json
["thing1", "thing2", "thing3"]
```