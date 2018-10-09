# Helios [![Build Status](https://travis-ci.org/code13k/helios.svg?branch=master)](https://travis-ci.org/code13k/helios)
**Helios** is topic-based pub/sub server using WebSocket written in Java.
Helios provide a solution to broadcast message to many subscriber. So it can send many messages to many subscribers quickly.
Helios has four servers. One is a subscribing server using WebSocket, another is a publishing server using WebSocket, a third is a publishing server using HTTP, the fourth is a restful API server using HTTP.

It provide pub method via HTTP and WebSocket.
You can publish message using HTTP and WebSocket.

It provider sub method via WebSocket.
You can subscribe message using WebSocket.

It provide clustering nodes using Hazelcast.
You can build high availability(HA) systems by clustering node.

* **[Configuration](./doc/configuration.md)**
* **[Publish Data](./doc/pub_server.md)**
* **[Subscribe Data](./doc/sub_server.md)**
* **[API](./doc/api_server.md)**


# Latest Release
The current stable version is ready.

The current unstable version is [v1.0.0-Alpha.1](https://github.com/code13k/helios/releases/tag/1.0.0-Alpha.1)


## License
MIT License

Copyright (c) 2018 Code13K

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.



                               
