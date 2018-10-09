# API Server
Helios has four servers. One is a subscribing server using WebSocket, another is a publishing server using WebSocket, a third is a publishing server using http, the fourth is a restful API server using http.

You can request api via HTTP.


### Usage
```html
http://example.com:{port}/{domain}/{method}
```


### Example
```html
http://example.com:55403/app/env
http://example.com:55403/app/status
http://example.com:55403/app/hello
http://example.com:55403/app/ping
```


# API (Topic)

## GET /topic/count
Get topic count
```json
{"data":15}
```

## GET /topic/all
Get all topic
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

## GET /topic/search?keyword={KEYWORD}
Find topic with keyword
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



# API (Cluster)

## GET /cluster/status
Get cluster status
```json
{
  "data": {
    "count": 2,
    "version": "3.10",
    "info": [
      {
        "address": "127.0.0.1:55410",
        "version": "3.10.5",
        "uuid": "65db4bbc-5b94-4009-a65b-54e0d4e51366"
      },
      {
        "address": "127.0.0.1:55411",
        "version": "3.10.5",
        "uuid": "f121af0f-3445-465a-bdf6-af240bae035f"
      },
      ...
    ]
  }
}
```



# API (App)

## GET /app/env
Get application environments
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

## GET /app/status
Get application status
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

## GET /app/config
Get application configuration
```json
{
  "data": {
    "cluster": {
      "nodes": [
        "192.168.100.1",
        "192.168.100.2"
      ],
      "port": 55410
    },
    "port": {
      "subWs": 55400,
      "apiHttp": 55403,
      "pubWs": 55401,
      "pubHttp": 55402
    }
  }
}
```

## GET /app/hello
Hello, World
```json
{"data":"world"}
```

## GET /app/ping
Ping-Pong
```json
{"data":"pong"}

