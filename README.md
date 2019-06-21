# Kafka Injector

This package generates values and put them into Kafka according to configuration based on Yaml format.

## Running App via main (as jar and etc)

There are 3 parameters main app expects via System.getProperties() _(-Dname=value)_:

1.  **kafkaInjectorConf** - full qualified path to yml configuration
1. **timeUnit** - the time unit type (should be identical to [java.util.concurrent.TimeUnit](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/TimeUnit.html)) - ``DAYS, HOURS, MICROSECONDS, MILLISECONDS, MINUTES, NANOSECONDS, SECONDS``
1. **time** - number of time, according to **timeUnit** defined above

Example: 
``java -DkafkaInjectorConf=/somepath/kafka-injector/kafka.yml -DtimeUnit=HOURS -Dtime=1 -jar /somepath/kafka-injector/kafka-injector-1.0.0-jar-with-dependencies.jar``


## Here Example of configuration file

```$yml
kafka:
  url: "localhost:19092"
  batchSize: 250000
  lingerMs: 5
  topics:
  - name: "IN-RAW"
    client_id_config: "sometext"
    serializer:
      key: "org.apache.kafka.common.serialization.StringSerializer"
      value: "JsonSerializer"
    generators:
    - type: "path.path.SomeGenerator"
      fields:
      - name: "setListValue"
        type: "list"
        cast: "java.lang.String"
        value: "LIST_VALUE1,LIST_VALUE2"
      - name: "setSourceType"
        type: "list"
        cast: "java.lang.String"
        value: "VALUE1,VALUE2"
      - name: "setKafkafied"
        type: "fixed"
        cast: "java.lang.Boolean"
        value: "true"
      - name: "setMessageId"
        type: "regex"
        cast: "java.lang.String"
        value: "[0-9]{10,14}-[a-bA-B]{20,20}"
      - name: "setDirection"
        type: "fixed"
        cast: "java.lang.String"
        value: "IN"
      - name: "setText"
        type: "regex"
        cast: "java.lang.String"
        value: "[a-bA-B 0-9]{5,100}"
      - name: "setStatus"
        type: "fixed"
        cast: "java.lang.String"
        value: "NA"
      - name: "setOwner"
        type: "nested_object"
        cast: "path.path.Device"
        nestedFields:
          - name: "setValue"
            type: "regex"
            cast: "java.lang.String"
            value: "9725[0-9][0-9]{7,7}"
          - name: "setType"
            type: "fixed"
            cast: "java.lang.String"
            value: "SMS"
      - name: "setRecipients"
        type: "nested_list"
        cast: "java.util.List"
        nestedFields:
          - name: ""
            type: "nested_object"
            cast: "path.path.SomeDevice"
            nestedFields:
              - name: "setValue"
                type: "regex"
                cast: "java.lang.String"
                value: "9725[0-9][0-9]{7,7}"
              - name: "setType"
                type: "fixed"
                cast: "java.lang.String"
                value: "SMS"
      - name: "setAttachment"
        type: "nested_list"
        cast: "java.util.List"
        nestedFields:
          - name: ""
            type: "nested_object"
            cast: "path.path.Attachment"
            nestedFields:
              - name: "setName"
                type: "regex"
                cast: "java.lang.String"
                value: "[a-bA-B]{2,10}\\.jpg"
              - name: "setContentType"
                type: "fixed"
                cast: "java.lang.String"
                value: "image/jpg"
              - name: "setContent"
                type: "regex"
                cast: "java.lang.String"
                value: "[a-bA-B0-9]{100,200}"
      delayAfter: 0
      interval: 10
      #takeWhile: 3000
```

Main element is **kafka**. It has few nested elements, as follows:

1. **url** - Kafka's (zookeeper) broker url (String) - _required_
1. **zkUrl** - Zookeeper url for creating non-existing topics (String) - _required if **createTopics** is set to **true**_ if need to create topics before starting injection (depends on **createTopics** and kafka properties)
1. **createTopics** - if true, tries to create topics (if not exist) before starting injection process - (Boolean) _Optional_, default: false. _Note:_ the tool still could create topics automatically during the first request - depends on kafka settings
1. **batchSize** - kafka's producer batch size configuration in bytes. (Number) _Optional_, default using Kafka's default.
1. **lingerMs** - kafka's producer linger ms configuration. (Number) _Optional_, default using Kafka's default.
1. **topics** - list of topics to generate data for:
    1. **name** - topic name (String) - _required_
    1. **partitions** - if topics doesn't exist used to create topic with specified number of partitions - (Number) _Optional_, default: 1. Used only  if **kafka.createTopics** is set to **true**.
    1. **replicas** - if topics doesn't exist used to create topic with specified number of replicas - (Number) _Optional_, default: 1. Used only  if **kafka.createTopics** is set to **true**.
    1. **client_id_config** - client id (String) - _required_
    1. **serializer** - element to define key and value serializers
        1. **key** - fully qualified class name for Kafka key serializer - _required_
        1. **value** - fully qualified class name for Kafka value serializer - _required_
    1. **generators** - list of generators per topic
        1. **type** - fully qualified class name for Generator (String) (should be existed. See [generated package](src/main/java/com/mikerusoft/kafka/injector/core/generate/model/) and should implement [DataGenerator](src/main/java/com/mikerusoft/kafka/injector/core/generate/model/DataGenerator.java)) (String) _required_
        1. **fields** - list of fields to generate
            1. **name** - field name to generate. Actually, it should be name of setter (or adder) in class you generate (method returned ``void`` and **only one** method argument). (String) _required_
            1. **type** - type of value generator (String) _required_. There few different types (from enum [GeneratorType](src/main/java/com/mikerusoft/kafka/injector/core/properties/GeneratorType.java))
                1. **REGEX** - generates according to regex specified in **value** element of field
                1. **NIL** - generates null. Ignore **value** element of field
                1. **RANDOM** - generates numeric random value (means, that **cast** should be one of numeric java classes or one of the supported custom types.
                1. **EMPTY** - generates empty String (means in **cast** should be "java.lang.String" - we'll be fixed later to ignore cast)
                1. **FIXED** - any fixed value. Treating type according to **cast** element
                1. **RANGE** - defines range between 2 numbers divided by comma (means, that **cast** should be one of numeric java classes or one of the supported custom types.
                1. **SEQUENTIAL_RANGE** - defines range (generated sequential) between 2 numbers divided by comma (means, that **cast** should be one of numeric java classes or one of the supported custom types.
                1. **LIST** - taking every time one element from the list in sequential order. Elements divided by comma.
                1. **RANDOM_LIST** - taking every time one element from the list in random order. Elements divided by comma.
                1. **TIMESTAMP** - set timestamp. If value is empty, sets current time (the moment the value is generated so it should be changed according to interval), else value should be set according to predefined date format: **yyyy-MM-dd HH:mm:ss.SSS**, e.g. **2018-07-11 12:30:45.123**.
                1. **TIMESTAMP_REGEX** - set timestamp. Generates timestamp according to predefined date format as regex (**yyyy-MM-dd HH:mm:ss.SSS**, e.g. **2018\\-07\\-11 12\\:30\\:45\\.123**) - _In this case better to use **TIMESTAMP** from above_. Using regex in date expression: **2018\\-07\\-11 12\\:30\\:45\\.[0-9]{3,3}** - milli seconds part generated from regex **[0-9]{3,3}**.
                1. **NESTED_OBJECT** - generates Object according to nested configuration. **cast** should be object's full class name to be generated. Subfields should be defined under **nestedFields** element and it shouldn't be empty.
                1. **NESTED_LIST** - generates List (of values of objects) according to nested configuration. **cast** - should be ``java.util.List`` (currently, the only valid value). Subfields should be defined under **nestedFields** element and it shouldn't be empty.
            1. **cast** - fully qualified class name to cast/convert value to. The value should be primitive boxing classes (``java.lang.Long.class`` and etc) or primitive (``java.lang.Long.TYPE``) or ``java.lang.String``. 
            1. **value** - value to use for generation. (String) _required_ depends on **type** value (see list above).
            1. **nested_fields** - in case of complex objects, it should be populated according to **type** value. Currently only **NESTED_OBJECT** and **NESTED_LIST** supported.
        1. **instances** - duplicates this generator specified times (In case we want the same configuration for more than 1 generator. It could be for concurrency reasons). (Number). _Optional_, (**default 1**). 
        1. **delayAfter** - the first element for flow control. If set, defines delay before starting to emit the 1st element (doesn't affect emitting other element, except the first one). (Number) milli seconds. _Optional_, (**default 0**).
        1. **interval** - the interval manage flow control. It defines interval for generating elements. For example, when putting 1 (ms) - means that it will generate value every 1 milli second, i.e. generates value, wait 1 milli second, then generates the second value and so on.  (Number) milli seconds. _Optional_, (**default 0**).
        1. **takeWhile** - the number of requests to take until stop generating the new ones. Means, limit the number of generated requests to specified value in the parameter. (Number) milli seconds. _Optional_, (**default 0**).
        