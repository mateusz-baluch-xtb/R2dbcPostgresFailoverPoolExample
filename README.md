# r2dbc-postgres with failover and connection pool problem example

This example demonstrates problem with failover and connection pool in R2DBC PostgreSQL.

## How to reproduce the problem

1. Start a PostgreSQL servers using Docker Compose:
    ```bash
    $ docker compose up
    ```
2. Run the application:
    ```bash
    $ ./gradlew run
    ```
3. Observe the logs and see that the application is connected to two standby instances.
    ```
    > Task :run
    Connected to standby
    Connected to standby
    
    BUILD SUCCESSFUL in 2s
    2 actionable tasks: 1 executed, 1 up-to-date
    ```

4. Stop secondary PostgreSQL server:
    ```bash
    $ docker compose stop postgres_secondary
    ```
5. Run the application again:
    ```bash
    $ ./gradlew run
    ```
   
6. Observe the logs with the error:
    ```
     > Task :run FAILED
    Connected to primary
    Exception in thread "main" io.r2dbc.postgresql.PostgresqlConnectionFactory$PostgresConnectionException: [08003] Cannot connect to a PREFER_SECONDARY node using [127.0.0.1:5432, 127.0.0.1:5433]. Known server states: {127.0.0.1/<unresolved>:5433=CONNECT_FAIL, 127.0.0.1/<unresolved>:5432=PRIMARY}
    at io.r2dbc.postgresql.PostgresqlConnectionFactory.cannotConnect(PostgresqlConnectionFactory.java:188)
    at io.r2dbc.postgresql.PostgresqlConnectionFactory.lambda$doCreateConnection$7(PostgresqlConnectionFactory.java:153)
    at reactor.core.publisher.Mono.lambda$onErrorMap$28(Mono.java:3783)
    at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onError(FluxOnErrorResume.java:94)
    at reactor.core.publisher.MonoFlatMap$FlatMapMain.onError(MonoFlatMap.java:180)
    at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onError(Operators.java:2210)
    at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onError(Operators.java:2210)
    at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onError(FluxOnErrorResume.java:100)
    at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onError(Operators.java:2210)
    at reactor.core.publisher.Operators.error(Operators.java:198)
    at reactor.core.publisher.MonoError.subscribe(MonoError.java:53)
    at reactor.core.publisher.MonoDefer.subscribe(MonoDefer.java:53)
    at reactor.core.publisher.Mono.subscribe(Mono.java:4496)
    at reactor.core.publisher.FluxSwitchIfEmpty$SwitchIfEmptySubscriber.onComplete(FluxSwitchIfEmpty.java:82)
    at reactor.core.publisher.MonoNext$NextSubscriber.onComplete(MonoNext.java:102)
    at reactor.core.publisher.FluxConcatMapNoPrefetch$FluxConcatMapNoPrefetchSubscriber.innerComplete(FluxConcatMapNoPrefetch.java:286)
    at reactor.core.publisher.FluxConcatMap$ConcatMapInner.onComplete(FluxConcatMap.java:887)
    at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onComplete(Operators.java:2205)
    at reactor.core.publisher.Operators.complete(Operators.java:137)
    at reactor.core.publisher.MonoEmpty.subscribe(MonoEmpty.java:46)
    at reactor.core.publisher.Mono.subscribe(Mono.java:4496)
    at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onError(FluxOnErrorResume.java:103)
    at reactor.core.publisher.MonoFlatMap$FlatMapMain.onError(MonoFlatMap.java:180)
    at reactor.core.publisher.MonoDelayUntil$DelayUntilTrigger.onError(MonoDelayUntil.java:514)
    at reactor.core.publisher.FluxHandle$HandleSubscriber.onError(FluxHandle.java:213)
    at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onError(MonoFlatMapMany.java:255)
    at reactor.core.publisher.FluxHandle$HandleSubscriber.onError(FluxHandle.java:213)
    at reactor.core.publisher.FluxCreate$BaseSink.error(FluxCreate.java:477)
    at reactor.core.publisher.FluxCreate$BufferAsyncSink.drain(FluxCreate.java:866)
    at reactor.core.publisher.FluxCreate$BufferAsyncSink.error(FluxCreate.java:811)
    at reactor.core.publisher.FluxCreate$SerializedFluxSink.drainLoop(FluxCreate.java:237)
    at reactor.core.publisher.FluxCreate$SerializedFluxSink.drain(FluxCreate.java:213)
    at reactor.core.publisher.FluxCreate$SerializedFluxSink.error(FluxCreate.java:189)
    at io.r2dbc.postgresql.client.ReactorNettyClient$Conversation.onError(ReactorNettyClient.java:707)
    at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.close(ReactorNettyClient.java:980)
    at io.r2dbc.postgresql.client.ReactorNettyClient.drainError(ReactorNettyClient.java:539)
    at io.r2dbc.postgresql.client.ReactorNettyClient.handleClose(ReactorNettyClient.java:522)
    at io.r2dbc.postgresql.client.ReactorNettyClient.access$200(ReactorNettyClient.java:94)
    at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.onComplete(ReactorNettyClient.java:871)
    at reactor.core.publisher.FluxHandle$HandleSubscriber.onComplete(FluxHandle.java:223)
    at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onComplete(FluxPeekFuseable.java:940)
    at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onComplete(FluxMap.java:275)
    at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onComplete(FluxMap.java:275)
    at reactor.netty.channel.FluxReceive.terminateReceiver(FluxReceive.java:483)
    at reactor.netty.channel.FluxReceive.drainReceiver(FluxReceive.java:275)
    at reactor.netty.channel.FluxReceive.onInboundComplete(FluxReceive.java:419)
    at reactor.netty.channel.ChannelOperations.onInboundComplete(ChannelOperations.java:445)
    at reactor.netty.channel.ChannelOperations.terminate(ChannelOperations.java:499)
    at reactor.netty.channel.ChannelOperations.onInboundClose(ChannelOperations.java:460)
    at reactor.netty.channel.ChannelOperationsHandler.channelInactive(ChannelOperationsHandler.java:73)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:303)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:281)
    at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:274)
    at io.netty.handler.codec.ByteToMessageDecoder.channelInputClosed(ByteToMessageDecoder.java:412)
    at io.netty.handler.codec.ByteToMessageDecoder.channelInactive(ByteToMessageDecoder.java:377)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:303)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:281)
    at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:274)
    at io.netty.channel.ChannelInboundHandlerAdapter.channelInactive(ChannelInboundHandlerAdapter.java:81)
    at io.r2dbc.postgresql.client.ReactorNettyClient$EnsureSubscribersCompleteChannelHandler.channelInactive(ReactorNettyClient.java:554)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:303)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:281)
    at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:274)
    at io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1402)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:301)
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:281)
    at io.netty.channel.DefaultChannelPipeline.fireChannelInactive(DefaultChannelPipeline.java:900)
    at io.netty.channel.AbstractChannel$AbstractUnsafe$7.run(AbstractChannel.java:811)
    at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:173)
    at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:166)
    at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:469)
    at io.netty.channel.epoll.EpollEventLoop.run(EpollEventLoop.java:405)
    at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:994)
    at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
    at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
    at java.base/java.lang.Thread.run(Thread.java:1583)
    Suppressed: java.lang.Exception: #block terminated with an error
    at reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:103)
    at reactor.core.publisher.Flux.blockLast(Flux.java:2753)
    at pl.baluch.r2dbc.failoverpool.Main.run(Main.java:42)
    at pl.baluch.r2dbc.failoverpool.Main.main(Main.java:18)
    Caused by: java.lang.IllegalArgumentException: Self-suppression not permitted
    at java.base/java.lang.Throwable.addSuppressed(Throwable.java:1096)
    at io.r2dbc.postgresql.MultiHostConnectionStrategy.lambda$connect$0(MultiHostConnectionStrategy.java:91)
    at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onError(FluxOnErrorResume.java:94)
    ... 68 more
    Suppressed: io.r2dbc.postgresql.client.ReactorNettyClient$PostgresConnectionClosedException: [08006] Connection unexpectedly closed
    at io.r2dbc.postgresql.client.ReactorNettyClient.lambda$static$0(ReactorNettyClient.java:102)
    ... 42 more
    Suppressed: io.r2dbc.postgresql.client.ReactorNettyClient$PostgresConnectionClosedException: [08006] Connection unexpectedly closed
    ... 43 more
    Suppressed: io.r2dbc.postgresql.client.ReactorNettyClient$PostgresConnectionClosedException: [08006] Connection unexpectedly closed
    ... 43 more
    Suppressed: io.r2dbc.postgresql.client.ReactorNettyClient$PostgresConnectionClosedException: [08006] Connection unexpectedly closed
    ... 43 more
    Caused by: [CIRCULAR REFERENCE: io.r2dbc.postgresql.client.ReactorNettyClient$PostgresConnectionClosedException: [08006] Connection unexpectedly closed]
    
    FAILURE: Build failed with an exception.
    
    * What went wrong:
      Execution failed for task ':run'.
    > Process 'command '/home/user/.sdkman/candidates/java/21-tem/bin/java'' finished with non-zero exit value 1
    
    * Try:
    > Run with --stacktrace option to get the stack trace.
    > Run with --info or --debug option to get more log output.
    > Run with --scan to get full insights.
    > Get more help at https://help.gradle.org.
    
    BUILD FAILED in 2s
    2 actionable tasks: 1 executed, 1 up-to-date
    ```

## Problem description
```
[08003] Cannot connect to a PREFER_SECONDARY node using [127.0.0.1:5432, 127.0.0.1:5433]. Known server states: {127.0.0.1/<unresolved>:5433=CONNECT_FAIL, 127.0.0.1/<unresolved>:5432=PRIMARY}
```

Above error is misleading, because we set `PREFER_SECONDARY` target server type, and error message contains information
that we can connect to primary instance. So why the connection to the primary instance is not happening?

The problem is that the connection pool is calling MultiHostConnectionStrategy.connect() method only once - to initialize
`Mono<Connection>`. Code important to understand the problem is located in `io.r2dbc.postgresql.MultiHostConnectionStrategy` class.
```java
public Mono<Client> connect(TargetServerType targetServerType) {
   AtomicReference<Throwable> exceptionRef = new AtomicReference<>();

   return attemptConnection(targetServerType)
           .onErrorResume(e -> {
              if (!exceptionRef.compareAndSet(null, e)) {
                 exceptionRef.get().addSuppressed(e);
              }
              return Mono.empty();
           })
           .switchIfEmpty(Mono.defer(() -> targetServerType == PREFER_SECONDARY ? attemptConnection(PRIMARY) : Mono.empty()))
           .switchIfEmpty(Mono.error(() -> {
              Throwable error = exceptionRef.get();
              if (error == null) {
                 return new PostgresqlConnectionFactory.PostgresConnectionException(String.format("No server matches target type '%s'", targetServerType), null);
              } else {
                 return new PostgresqlConnectionFactory.PostgresConnectionException(String.format("Cannot connect to a host of %s", this.addresses), error);
              }
           }));
}

private Mono<Client> attemptConnection(TargetServerType targetServerType) {
   AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
   return getCandidates(targetServerType).concatMap(candidate -> this.attemptConnection(targetServerType, candidate)
                   .onErrorResume(e -> {
                      if (!exceptionRef.compareAndSet(null, e)) {
                         exceptionRef.get().addSuppressed(e);
                      }
                      this.statusMap.put(candidate, HostConnectOutcome.fail(candidate));
                      return Mono.empty();
                   }))
           .next()
           .switchIfEmpty(Mono.defer(() -> exceptionRef.get() != null
                   ? Mono.error(exceptionRef.get())
                   : Mono.empty()));
}
```

When we call `MultiHostConnectionStrategy.connect()` method, it returns `Mono<Connection>` with references the same
two instances of `exceptionRef` (one from `connect(TargetServerType)` and the second one from 
`attemptConnection(TargetServerType)`. So when using target type `PREFER_SECONDARY` and the first `attemptConnection` 
is not successful, the `exceptionRef` is filled with that exception. Then in `connect(TargetServerType)` method, we
store the same exception in `exceptionRef`. First connection is always successful (connecting to primary instance), 
because `exceptionRef` in the `connect` method is empty. All subsequent connections are failing, because the
`attemtConnection` method is returning the same exception, and then in the `connect` method we are trying to do:
```java
exceptionRef.get().addSuppressed(e);
```
and this is causing `java.lang.IllegalArgumentException: Self-suppression not permitted` exception to be thrown.

## Solution

The solution to that problem is to create a new instance of `AtomicReference<Throwable>` in the `attemptConnection` 
and `connect` methods for each connection attempt. We can do it by wrapping method calls in `Mono.defer(() -> call())`.

Example:
```java
    @Override
    public Mono<Client> connect() {
        return Mono.defer(() -> connect(this.multiHostConfiguration.getTargetServerType()));
    }

    public Mono<Client> connect(TargetServerType targetServerType) {
        AtomicReference<Throwable> exceptionRef = new AtomicReference<>();

        return Mono.defer(() -> attemptConnection(targetServerType))
                ...
```

### Explanation:

The `Mono.defer(() -> call())` method will create a new instance of `AtomicReference<Throwable>` for each connection
attempt. This will prevent the `java.lang.IllegalArgumentException: Self-suppression not permitted` exception from being
thrown.
