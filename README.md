# Helios is topic-based pub/sub server using websocket.
* Subscribe message via websocket
* Publish message via websocket and http



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
#### GET /app/status
* Get application status and environment.
##### Response
```json
{
  "data":{
    "applicationVersion":"0.1.0-alpha.3",
    "cpuUsage":2.56,
    "threadInfo":{...},
    "vmMemoryFree":"190M",
    "javaVersion":"1.8.0_25",
    "vmMemoryMax":"3,641M",
    "currentDate":"2018-09-16T18:48:58.795+09:00",
    "threadCount":15,
    "startedDate":"2018-09-16T18:48:40.901+09:00",
    "javaVendor":"",
    "runningTimeHour":0,
    "osName":"Mac OS X",
    "cpuProcessorCount":4,
    "vmMemoryTotalFree":"3,585M",
    "hostname":"",
    "osVersion":"10.11.6",
    "jarFile":"code13k-helios-0.1.0-alpha.3.jar",
    "vmMemoryAllocated":"245M",
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



