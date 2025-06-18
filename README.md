
## FastEvent

is a Forge optimization mod that optimizes one of the most fundamental systems in Forge: the Event system.

## How Fast

It's not easy to make a test case for every supported Minecraft version, especially when the Event System for these versions are different from each other. So instead, I will provide a JMH benchmark report from a PR I made for Cleanroom project, in which I used the same optimization approach as FastEvent:

Register 10,000 event listeners, post event 0 times:
```
Benchmark                               Mode  Cnt     Score     Error  Units
BusPerformanceTest.register10000Legacy  avgt    5  1126.498 ± 284.633  ms/op
BusPerformanceTest.register10000Modern  avgt    5  1058.961 ± 173.586  ms/op
```
About 6.4% faster.

Register 1,000 event listeners, post event 10,000 times:
```
Benchmark                                       Mode  Cnt     Score      Error  Units
BusPerformanceTest.register1000test10000Legacy  avgt    5  4407.963 ± 4250.643  ms/op
BusPerformanceTest.register1000test10000Modern  avgt    5  3550.578 ± 1991.352  ms/op
```
About 24% faster.

Original: https://github.com/CleanroomMC/Cleanroom/pull/328#issuecomment-2801099504

## How does it work

(Nerdy technical details alert)

When devs are using `@EventBusSubscriber` and/or `@SubscribeEvent` to subscribe event handlers, the EventBus cannot get the event handler magically, instead only a `Method` object is available. So the most straight-forward approach is use this object directly: `method.invoke(...)`. This invocation will eventually be redirected by JVM back to the original method, allowing an event handler to receive an event after subscribing to it.

But this (`method.invoke(...)`) is really slow. To make it faster, the EventBus will, at runtime, generate event handler classes for every method it found, eliminating expensive reflection based invocation.

But generating classes introduces another slow-down. To make it even faster, FastEvent replaced class generation with lambda construction, speeding up event handler construction. Another benefit is that lambda is "hidden", allowing JVM to perform more optimization.

If you happen to know a bit Java, code examples below might be more intuitive for you:

```java
class Listen {
    public void onEvent(Event event) {
    }
}
Listen lis = new Listen();

// EventBus will generate a new class for every event handler
class IEventListener$Listen$onEvent implements IEventListener {
    private Listen instance;
    public IEventListener$Listen$onEvent(Listen instance) {
        this.instance = instance;
    }
    @Override
    public void invoke(Event event) {
        instance.onEvent(event);
    }
}
IEventListener handler = new IEventListener$Listen$onEvent(lis);

// FastEvent uses lambda to generate event handler
IEventListener handler = lis::onEvent;
```
