// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ProtoHB.proto

package com.gm.link.common.domain.protobuf;

public final class ProtoHBOuter {
  private ProtoHBOuter() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_gm_link_common_domain_protobuf_ProtoHB_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_gm_link_common_domain_protobuf_ProtoHB_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rProtoHB.proto\022\"com.gm.link.common.doma" +
      "in.protobuf\"\214\001\n\007ProtoHB\022\r\n\005magic\030\001 \001(\007\022\017" +
      "\n\007version\030\002 \001(\r\022\013\n\003cmd\030\003 \001(\r\022\027\n\017from_mac" +
      "hine_id\030\004 \001(\t\022\025\n\rto_machine_id\030\005 \001(\t\022\021\n\t" +
      "timestamp\030\006 \001(\003\022\021\n\tcheck_sum\030\007 \001(\007B4\n\"co" +
      "m.gm.link.common.domain.protobufB\014ProtoH" +
      "BOuterP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_gm_link_common_domain_protobuf_ProtoHB_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_gm_link_common_domain_protobuf_ProtoHB_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_gm_link_common_domain_protobuf_ProtoHB_descriptor,
        new java.lang.String[] { "Magic", "Version", "Cmd", "FromMachineId", "ToMachineId", "Timestamp", "CheckSum", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
