#  Copyright (c) 2022 Fondef IDeA I+D.
"""Runs protoc with the gRPC plugin to generate messages and gRPC stubs."""

from grpc_tools import protoc

protoc.main((
    '',
    '--python_out=.',
    '--grpc_python_out=.',
    '--proto_path=src/main/proto/',
    'web.proto',
))
