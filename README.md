# Introduction
Filus is a web crawler for Tor Hidden Services.  
Uses HttpComponents by Apache.  

# Requirements
- Java Runtime Environment  
- Tor  

# Quick Setup Guide
1. Run the JAR file. 
2. It will output an exception almost immediately, this is fine. Wait until the program exits.
3. In your Tor config (torrc), set a ControlPort, HashedControlPassword, SocksPort, and SocksPolicy. If you are unfamiliar with these terms, please refer to the "torrc Sample" in the README below, or use Google :). Make sure that you restart/reload Tor to set the changes.
4. Filus has made a new file: "filus.properties". It should be in the same folder as where the JAR was executed. Open this file with your favourite text editor.
5. You will need to change the following values to the ones you had set in the _torrc_:
   - torProxy-port
   - torControl-port
   - torControl-passwd (in plain text)
6. Optionally, you can change the following self-explanatory settings:
   - fThreads-count
   - fConnTimeout
   - rqAgent
   - rqHeader
7. Run the JAR file again, like you did at 1.
# How to fix Common Errors:
   - "_Error: Cannot reach the local Tor client proxy_" - Filus cannot reach your Tor's SOCKS5 proxy. Possible solutions:
     1. Tor is not running.
     2. Torrc or filus.properties is configured wrong.
   - "_Error: wrong password!_" - Possible solutions:
     1. _HashedControlPassword_ in _torrc_ is not set correctly.
     2. _torControl-passwd_ in _filus.properties_ is not set correctly.
   - "_[F] NumberFormatException in property ...._" - You have entered an incorrect value in the _filus.properties_ file.
   - "_[F] I/O Exception while writing props_" - Filus (or Java) does not have the permission to write a properties file.
   - "_[FSCT] fControlTest failed: could not reach main addr or failover addr._" - Possible solutions:
     1. Make sure the proxy still works.
     2. Make sure the websites which are bound to _fControlTest-addr_ and _fControlTest-addr-failover_ are online.
   - "_[FSCT] sct: Host unreachable_" - Please try to run Filus again. Could have been bad luck.
   - "_[FWM] Webmonitor error: Exception:_" - Possible solutions:
     1. _fWebMonitor-port_ set incorrectly or already in use
     2. _fWebMonitor-addr_ set incorrectly or already in use
   - If you encounter an error which is not listed here, or you need help with resolving an error, feel free to send a message to _admin@hypothermic.nl_
# Sample Configuration Files
### _filus.properties_ Sample:
```
fConnTimeout=15000   
fControlTest-addr=http\://checkip.amazonaws.com/
fControlTest-addr-failover=http\://icanhazip.com/
fControlTest-connTimeout=30000
fControlTest-enable=1
fProxyTest-addr=http\://www.neverssl.com
fProxyTest-connTimeout=30000
fProxyTest-enable=1
fThreads-count=200
fWebMonitor-addr=127.0.0.1
fWebMonitor-enable=0
fWebMonitor-port=80
filusDebug=1
rqAgent=Filus
rqHeader=FilusCrawler
torControl-addr=127.0.0.1
torControl-passwd=MyGreatPassword
torControl-port=9053
torProxy-addr=127.0.0.1
torProxy-port=9052
```

### _torrc_ Sample:
```
SocksPort 127.0.0.1:9052
SocksPolicy accept *
ControlPort 9053
HashedControlPassword 16:72C58F5D039F7C0D60700B59333EA10C499E6061DAF70B748CB8413E32
```

Short explanation of abbrevations:
[F] = Filus  
[FSPT] = Filus Self Proxy Test  
[FSCT] = Filus Self Control Test  
[FWM] = Filus Web Monitor  

# Mentions
Learn more about Tor here: www.torproject.org  
Thanks to b10y on SO for the amazing socks5 integration: https://stackoverflow.com/users/330464/b10y  
Thanks to my coffee for making me create this readme file at 3 AM in the morning

# Contact
Feel free to contact me about Filus. Any questions are welcome.  
admin@hypothermic.nl or https://hypothermic.nl  
