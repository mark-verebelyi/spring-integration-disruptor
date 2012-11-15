Spring Integration Disruptor
============================

Bringing the Disruptor to Spring Integration
--------------------------------------------

This project aims to bring the [LMAX Disruptor framework](http://lmax-exchange.github.com/disruptor) to the [Spring Integration ecosystem](http://www.springsource.org/spring-integration).

The planned features are the following:
* Extensive XML namespace support
* RingBuffer/Disruptor-backed MessageChannels, Adapters, Gateways

   <int-disruptor:disruptor id="disruptor" buffer-size="128" wait-strategy="yielding" claim-strategy="single-threaded" />

   <int-disruptor:channel id="channel1" disruptor="disruptor" />

   <int-disruptor:ring-buffer id="ring-buffer" buffer-size="512" claim-strategy="multi-threaded" wait-strategy="busy-spin" />

   <int-disruptor:messaging-event-factory id="eventFactory"/>
