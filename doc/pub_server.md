# Server
Helios has four servers. One is a subscribing server using WebSocket, another is a publishing server using WebSocket, a third is a publishing server using http, the fourth is a restful API server using http.

You can publish message via HTTP and WebSocket.


## WebSocket
### Usage
```html
ws://example.com:{port}/pub/{topic}
```
* port
  * Server port
  * It's *pub_ws* in app_config.yml.
* Publish message to specific topic via WebSocket.
* Sending message via WebSocket.

### Example
```html
ws://example.com:55401/pub/org.code13k.topic
```


## HTTP
### Usage
```html
http://example.com:{port}/pub/{topic}
```
* port
  * Server port
  * It's *pub_http* in app_config.yml.
* Publish message to specific topic via HTTP.
* Using POST and adding message into request body.

### Example
```html
http://example.com:55402/pub/org.code13k.topic
```
