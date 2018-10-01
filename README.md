# Helios is topic-based pub/sub server using websocket.
* Subscribe message via websocket
* Publish message via websocket and http

[![Build Status](https://travis-ci.org/code13k/helios.svg?branch=master)](https://travis-ci.org/code13k/helios)


## app_config.yml
It's application configuration file.
```yaml
# Server port
port:
  sub_ws: 55400
  pub_ws: 55401
  pub_http: 55402
  api_http: 55403
```

## logback.xml
It's Logback configuration file that is famous logging library.
* You can send error log to Telegram.
  1. Uncomment *Telegram* configuration.
  2. Set value of `<botToken>` and `<chatId>`.
       ```xml
       <appender name="TELEGRAM" class="com.github.paolodenti.telegram.logback.TelegramAppender">
           <botToken></botToken>
           <chatId></chatId>
           ...
       </appender>
       ```
  3. Insert `<appender-ref ref="TELEGRAM"/>` into `<root>`
     ```xml
     <root level="WARN">
         <appender-ref ref="FILE"/>
         <appender-ref ref="TELEGRAM"/>
     </root>
     ```
* You can send error log to Slack.
  1. Uncomment *Slack* configuration.
  2. Set value of `<webhookUri>`.
       ```xml
       <appender name="SLACK_SYNC" class="com.github.maricn.logback.SlackAppender">
           <webhookUri></webhookUri>
           ...
       </appender>
       ```
  3. Insert `<appender-ref ref="SLACK"/>` into `<root>`
     ```xml
     <root level="WARN">
         <appender-ref ref="FILE"/>
         <appender-ref ref="SLACK"/>
     </root>
     ```
* You can reload configuration but need not to restart application.


# Server
Helios has four servers. 
One is a subscribing server using websocket, another is a publishing server using websocket, a thrid is a publishing server using http, the fourth is a restful API server using http.

## Sub Websocket Server
### Usage
```html
ws://example.com:{port}/sub
```
* port
  * Server port
  * It's *sub_ws* in app_config.yml.
* command
  * SUB {topic}
  * UNSUB {topic}
  * PING
  
### Example
```html
ws://example.com:55401/sub
SUB org.code13k.topic
```
  
## Pub Websocket Server
```html
ws://example.com:{port}/pub/{topic}
```
* port
  * Server port
  * It's *pub_ws* in app_config.yml.
* Publish message to specific topic via websocket
  
### Example
```html
ws://example.com:55401/pub/org.code13k.topic
```
  
## Pub HTTP Server
```html
http://example.com:{port}/pub/{topic}
```
* port
  * Server port
  * It's *pub_http* in app_config.yml.
* Publish message to specific topic.
* Using POST and sending message add body.


### Example
```html
http://example.com:55402/pub/org.code13k.topic
```

## API HTTP Server
### Usage
```html
http://example.com:{port}/{domain}/{method}
```

### Example
```html
http://example.com:55403/app/status
http://example.com:55403/app/hello
http://example.com:55403/app/ping
```

### API
#### GET /topic/count
* Get topic count
```json
{"data":15}
```

### GET /topic/all
* Get all topic
##### Response
```json
{
  "data": [
    {
      "channelCount": 15,
      "topic": "primitive.topic.all"
    },
    {
      "channelCount": 15,
      "topic": "org.code13k.topic1"
    },
    ...
  ]
}
```

### GET /topic/search?keyword={KEYWORD}
* Find topic with keyword
##### Response
```json
{
  "data": [
    {
      "channelCount": 15,
      "topic": "primitive.topic.all"
    },
    {
      "channelCount": 15,
      "topic": "org.code13k.topic1"
    },
    ...
  ]
}
```

#### GET /app/env
* Get application environments
##### Response
```json
{
  "data":{
    "applicationVersion": "1.4.0",
    "hostname": "hostname",
    "osVersion": "10.11.6",
    "jarFile": "code13k-helios-1.0.0-alpha.1.jar",
    "javaVersion": "1.8.0_25",
    "ip": "192.168.0.121",
    "javaVendor": "Oracle Corporation",
    "osName": "Mac OS X",
    "cpuProcessorCount": 4
  }
}
```
#### GET /app/status
* Get application status
##### Response
```json
{
  "data":{
    "threadInfo":{...},
    "cpuUsage": 2.88,
    "threadCount": 25,
    "currentDate": "2018-10-02T01:15:21.290+09:00",
    "startedDate": "2018-10-02T01:14:40.995+09:00",
    "runningTimeHour": 0,
    "vmMemoryUsage":{...}
  }
}
```
#### GET /app/hello
* Hello, World
##### Response
```json
{"data":"world"}
```
#### GET /app/ping
* Ping-Pong
##### Response
```json
{"data":"pong"}



