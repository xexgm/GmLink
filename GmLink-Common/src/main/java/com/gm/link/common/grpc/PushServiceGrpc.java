package com.gm.link.common.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.55.1)",
    comments = "Source: push_grpc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PushServiceGrpc {

  private PushServiceGrpc() {}

  public static final String SERVICE_NAME = "PushService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UserRequest,
      com.gm.link.common.grpc.PushGrpc.Push2UserResponse> getPush2UserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Push2User",
      requestType = com.gm.link.common.grpc.PushGrpc.Push2UserRequest.class,
      responseType = com.gm.link.common.grpc.PushGrpc.Push2UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UserRequest,
      com.gm.link.common.grpc.PushGrpc.Push2UserResponse> getPush2UserMethod() {
    io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UserRequest, com.gm.link.common.grpc.PushGrpc.Push2UserResponse> getPush2UserMethod;
    if ((getPush2UserMethod = PushServiceGrpc.getPush2UserMethod) == null) {
      synchronized (PushServiceGrpc.class) {
        if ((getPush2UserMethod = PushServiceGrpc.getPush2UserMethod) == null) {
          PushServiceGrpc.getPush2UserMethod = getPush2UserMethod =
              io.grpc.MethodDescriptor.<com.gm.link.common.grpc.PushGrpc.Push2UserRequest, com.gm.link.common.grpc.PushGrpc.Push2UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Push2User"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.gm.link.common.grpc.PushGrpc.Push2UserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.gm.link.common.grpc.PushGrpc.Push2UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PushServiceMethodDescriptorSupplier("Push2User"))
              .build();
        }
      }
    }
    return getPush2UserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UsersRequest,
      com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> getPush2UsersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Push2Users",
      requestType = com.gm.link.common.grpc.PushGrpc.Push2UsersRequest.class,
      responseType = com.gm.link.common.grpc.PushGrpc.Push2UsersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UsersRequest,
      com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> getPush2UsersMethod() {
    io.grpc.MethodDescriptor<com.gm.link.common.grpc.PushGrpc.Push2UsersRequest, com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> getPush2UsersMethod;
    if ((getPush2UsersMethod = PushServiceGrpc.getPush2UsersMethod) == null) {
      synchronized (PushServiceGrpc.class) {
        if ((getPush2UsersMethod = PushServiceGrpc.getPush2UsersMethod) == null) {
          PushServiceGrpc.getPush2UsersMethod = getPush2UsersMethod =
              io.grpc.MethodDescriptor.<com.gm.link.common.grpc.PushGrpc.Push2UsersRequest, com.gm.link.common.grpc.PushGrpc.Push2UsersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Push2Users"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.gm.link.common.grpc.PushGrpc.Push2UsersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.gm.link.common.grpc.PushGrpc.Push2UsersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PushServiceMethodDescriptorSupplier("Push2Users"))
              .build();
        }
      }
    }
    return getPush2UsersMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PushServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PushServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PushServiceStub>() {
        @java.lang.Override
        public PushServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PushServiceStub(channel, callOptions);
        }
      };
    return PushServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PushServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PushServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PushServiceBlockingStub>() {
        @java.lang.Override
        public PushServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PushServiceBlockingStub(channel, callOptions);
        }
      };
    return PushServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PushServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PushServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PushServiceFutureStub>() {
        @java.lang.Override
        public PushServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PushServiceFutureStub(channel, callOptions);
        }
      };
    return PushServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void push2User(com.gm.link.common.grpc.PushGrpc.Push2UserRequest request,
        io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPush2UserMethod(), responseObserver);
    }

    /**
     */
    default void push2Users(com.gm.link.common.grpc.PushGrpc.Push2UsersRequest request,
        io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPush2UsersMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PushService.
   */
  public static abstract class PushServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PushServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PushService.
   */
  public static final class PushServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PushServiceStub> {
    private PushServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PushServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PushServiceStub(channel, callOptions);
    }

    /**
     */
    public void push2User(com.gm.link.common.grpc.PushGrpc.Push2UserRequest request,
        io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPush2UserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void push2Users(com.gm.link.common.grpc.PushGrpc.Push2UsersRequest request,
        io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPush2UsersMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PushService.
   */
  public static final class PushServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PushServiceBlockingStub> {
    private PushServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PushServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PushServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.gm.link.common.grpc.PushGrpc.Push2UserResponse push2User(com.gm.link.common.grpc.PushGrpc.Push2UserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPush2UserMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.gm.link.common.grpc.PushGrpc.Push2UsersResponse push2Users(com.gm.link.common.grpc.PushGrpc.Push2UsersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPush2UsersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PushService.
   */
  public static final class PushServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PushServiceFutureStub> {
    private PushServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PushServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PushServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gm.link.common.grpc.PushGrpc.Push2UserResponse> push2User(
        com.gm.link.common.grpc.PushGrpc.Push2UserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPush2UserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gm.link.common.grpc.PushGrpc.Push2UsersResponse> push2Users(
        com.gm.link.common.grpc.PushGrpc.Push2UsersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPush2UsersMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PUSH2USER = 0;
  private static final int METHODID_PUSH2USERS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUSH2USER:
          serviceImpl.push2User((com.gm.link.common.grpc.PushGrpc.Push2UserRequest) request,
              (io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UserResponse>) responseObserver);
          break;
        case METHODID_PUSH2USERS:
          serviceImpl.push2Users((com.gm.link.common.grpc.PushGrpc.Push2UsersRequest) request,
              (io.grpc.stub.StreamObserver<com.gm.link.common.grpc.PushGrpc.Push2UsersResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getPush2UserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.gm.link.common.grpc.PushGrpc.Push2UserRequest,
              com.gm.link.common.grpc.PushGrpc.Push2UserResponse>(
                service, METHODID_PUSH2USER)))
        .addMethod(
          getPush2UsersMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.gm.link.common.grpc.PushGrpc.Push2UsersRequest,
              com.gm.link.common.grpc.PushGrpc.Push2UsersResponse>(
                service, METHODID_PUSH2USERS)))
        .build();
  }

  private static abstract class PushServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PushServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.gm.link.common.grpc.PushGrpc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PushService");
    }
  }

  private static final class PushServiceFileDescriptorSupplier
      extends PushServiceBaseDescriptorSupplier {
    PushServiceFileDescriptorSupplier() {}
  }

  private static final class PushServiceMethodDescriptorSupplier
      extends PushServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PushServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PushServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PushServiceFileDescriptorSupplier())
              .addMethod(getPush2UserMethod())
              .addMethod(getPush2UsersMethod())
              .build();
        }
      }
    }
    return result;
  }
}
