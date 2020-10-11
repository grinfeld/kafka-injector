[![Build Status](https://travis-ci.org/grinfeld/kafka-injector.svg?branch=master)](https://travis-ci.org/grinfeld/kafka-injector)
[![Code Quality: Java](https://img.shields.io/lgtm/grade/java/g/grinfeld/kafka-injector.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/grinfeld/kafka-injector/context:java)
[![Total Alerts](https://img.shields.io/lgtm/alerts/g/grinfeld/kafka-injector.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/grinfeld/kafka-injector/alerts)

# Kafka Injector

This package generates values and put them into Kafka according to configuration based on Yaml format.

## Running App via main (as jar and etc)

There are 3 parameters main app expects via System.getProperties() _(-Dname=value)_:

1.  **kafkaInjectorConf** - full qualified path to yml configuration
1. **timeUnit** - the time unit type (should be identical to [java.util.concurrent.TimeUnit](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/TimeUnit.html)) - ``DAYS, HOURS, MICROSECONDS, MILLISECONDS, MINUTES, NANOSECONDS, SECONDS``
1. **time** - number of time, according to **timeUnit** defined above

Example: 
``java -DkafkaInjectorConf=/somepath/kafka-injector/kafka.yml -DtimeUnit=HOURS -Dtime=1 -jar /somepath/kafka-injector/kafka-injector-$version-jar-with-dependencies.jar``

## As maven dependency

```
    <dependency>
        <groupId>com.mikerusoft</groupId>
        <artifactId>kafka-injector-core</artifactId>
        <version>${version}</version>
    </dependency>
```

## Here Example of configuration file

```yaml
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
    valueGenerators:
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
## Configuration

Main element is **kafka**. It has few (many) nested elements, as follows:

1. **url** - Kafka's broker url (String) - _required_
1. **schemaRegistryUrl** - schema-registry url. Should be populated when using avro serializer - _optional_
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
    1. **keyGenerator** - generator for key to be set in Kafka (currently supported only one keyGenerator for all valueGenerators in the topic)- _Optional_, default **null**
        1. for list of values see in **valueGenerator** below.
    1. **valueGenerators** - list of generators per topic
        1. **type** - fully qualified class name for Generator (String) (should be existed. See [generated package](kafka-injector-core/src/main/java/com/mikerusoft/kafka/injector/core/generate/model/) and should implement [DataGenerator](kafka-injector-core/src/main/java/com/mikerusoft/kafka/injector/core/generate/model/DataGenerator.java)) (String) _required_
        1. **fields** - list of fields to generate
            1. **name** - field name to generate. Actually, it should be name of setter (or adder) in class you generate (suitable for any method return ``void`` and has **only one** method argument). (String) _required_
            1. **type** - type of value generator (String) _required_. There few different types (from enum [GeneratorType](kafka-injector-core/src/main/java/com/mikerusoft/kafka/injector/core/properties/GeneratorType.java))
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
                1. **ENUM** - pick random value from one of Enums or fixed value. **cast** - should be enum class. If **value** i set - always returns the same value, else returns random enum value from this enum
                1. **MAP** - creates Map with values defined in **nested_fields**. names of **nested_value** will be the keys in Map. **cast** should be class implements *java.util.Map**. Ignores ***value** element of field.
            1. **cast** - fully qualified class name to cast/convert value to. The value should be primitive boxing classes (``java.lang.Long.class`` and etc) or primitive (``java.lang.Long.TYPE``) or ``java.lang.String``. 
            1. **value** - value to use for generation. (String) _required_ depends on **type** value (see list above).
            1. **nested-fields** (**nested_fields**) - in case of complex objects, it should be populated according to **type** value. Currently only **NESTED_OBJECT** and **NESTED_LIST** supported.
            1. **creator** - defines the custom method (and class) to create a container for **NESTED_OBJECT** only. _Optional_
                1. **class-name** - the class name where creator/builder method is placed in. It could be (and usual is) different from **cast** value. _required_
                1. **method-name** - the method name to create the desired **cast** object/container. Method could be either static or not. _Optional_. (**default**: empty constructor of **class-name**. If there is no **method-name**, means the **class-name** should have empty constructor)
                1. **static** - defines if **method-name** is static in **class-name** or not
        1. **instances** - duplicates this generator specified times (In case we want the same configuration for more than 1 generator. It could be for concurrency reasons). (Number). _Optional_, (**default 1**). 
        1. **delayAfter** - the first element for flow control. If set, defines delay before starting to emit the 1st element (doesn't affect emitting other element, except the first one). (Number) milli seconds. _Optional_, (**default 0**).
        1. **interval** - the interval manage flow control. It defines interval for generating elements. For example, when putting 1 (ms) - means that it will generate value every 1 milli second, i.e. generates value, wait 1 milli second, then generates the second value and so on.  (Number) milli seconds. _Optional_, (**default 0**).
        1. **takeWhile** - the number of requests to take until stop generating the new ones. Means, limit the number of generated requests to specified value in the parameter. (Number) milli seconds. _Optional_, (**default 0**).

## Custom Generator

When you need to define only rules how to generate fields, you can use only MapGenerator, ListGenerator and other simple generators

For example:

```yaml
kafka:
  url: "localhost:9092"
  batchSize: 250000
  lingerMs: 5
  topics:
  - name: "IN-RAW"
    client_id_config: "sometext"
    serializer:
      key: "org.apache.kafka.common.serialization.StringSerializer"
      value: "com.mikerusoft.kafka.injector.core.kafka.JsonSerializer"
    keyGenerator:
      type: "com.mikerusoft.kafka.injector.core.generate.model.SingleRootGenerator"
      fields:
      - name: "does_not_matter"
        type: "fixed"
        cast: "java.lang.Integer"
        value: "110"
    valueGenerators:
    - type: "com.mikerusoft.kafka.injector.core.generate.model.SingleRootGenerator"
      fields:
        - name: "root"
          type: "map"
          cast: "java.util.HashMap"
            nestedFields:
              - name: "key1"
                type: "regex"
                cast: "java.lang.String"
                value: "[0-9][0-9]{7,7}"
              - name: "key2"
                type: "fixed"
                cast: "java.lang.String"
                value: "SOME_VALUE"
          
```

This one, finally, will send to Kafka json of 
```json
{
  "key1": "some generated value",
  "key2": "Some Text"
}
```

If you have POJOs with regular set and get methods for fields and empty constructor, you have everything to generate data and put it in Kafka.

But if for some reasons, one of classes doesn't have set and get methods for fields or any other restriction, you can implement your own generator by extending
[SpecificDataGenerator<T, B>](kafka-injector-core/src/main/java/com/mikerusoft/kafka/injector/core/generate/model/SpecificDataGenerator.java)
where ``T`` is your object to send to Kafka and ``B`` is a builder to use instead of ``T`` during generation process.

See [example](examples/src/main/java/com/mikerusoft/kafka/injector/examples/model/ExampleMessageGenerator.java)