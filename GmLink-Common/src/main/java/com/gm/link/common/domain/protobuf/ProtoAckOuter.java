// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ProtoAck.proto

package com.gm.link.common.domain.protobuf;

public final class ProtoAckOuter {
  private ProtoAckOuter() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_gm_link_common_domain_protobuf_ProtoAck_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_gm_link_common_domain_protobuf_ProtoAck_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016ProtoAck.proto\022\"com.gm.link.common.dom" +
      "ain.protobuf\"\231\001\n\010ProtoAck\022\r\n\005magic\030\001 \001(\007" +
      "\022\017\n\007version\030\002 \001(\r\022\013\n\003cmd\030\003 \001(\r\022\027\n\017origin" +
      "al_msg_id\030\004 \001(\t\022\016\n\006status\030\005 \001(\r\022\026\n\terror" +
      "_msg\030\006 \001(\tH\000\210\001\001\022\021\n\tcheck_sum\030\007 \001(\007B\014\n\n_e" +
      "rror_msgB5\n\"com.gm.link.common.domain.pr" +
      "otobufB\rProtoAckOuterP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_gm_link_common_domain_protobuf_ProtoAck_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_gm_link_common_domain_protobuf_ProtoAck_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_gm_link_common_domain_protobuf_ProtoAck_descriptor,
        new java.lang.String[] { "Magic", "Version", "Cmd", "OriginalMsgId", "Status", "ErrorMsg", "CheckSum", "ErrorMsg", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
