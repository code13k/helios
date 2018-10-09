# Configuration
Helios has two configuration files.

## app_config.yml
It's application configuration file.
```yaml
# Server port
port:
  sub_ws: 55400
  pub_ws: 55401
  pub_http: 55402
  api_http: 55403

# Cluster
#cluster:
#  port: 55410
#  nodes:
#    - 192.168.100.1
#    - 192.168.100.2
```
* Helios has four servers. One is a subscribing server using WebSocket, another is a publishing server using WebSocket, a third is a publishing server using http, the fourth is a restful API server using http. You can edit server port.
* Helios support clustering. You can cluster node by adding nodes IP. 
  * If you want to use clustering nodes, uncomment cluster configuration and add nodes IP.

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
