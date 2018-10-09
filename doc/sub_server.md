# Server
Helios has four servers. One is a subscribing server using WebSocket, another is a publishing server using WebSocket, a third is a publishing server using http, the fourth is a restful API server using http.

You can subscribe message via WebSocket.

## Usage
### URL
```html
ws://example.com:{port}/sub
```
* port
  * Server port
  * It's *sub_ws* in app_config.yml.
### Command
```html
SUB {topic1, topic2, topics3 ...}
UNSUB {topic1, topic2, topic3 ...}
PING
DISCONNECT
```
* SUB
  * Subscribe topic 
* UNSUB
  * Unsubscribe topic
* PING
  * Ping-Pong
* DISCONNECT
  * Disconnect
### Example
```html
ws://example.com:55400/sub
SUB org.code13k.topic
```
```html
ws://example.com:55400/sub
SUB org.code13k.topic1 org.code13k.topic2 org.code13k.topic3
```
```html
ws://example.com:55400/sub
SUB org.code13k.topic1 org.code13k.topic2
UNSUB org.code13k.topic1
```
  