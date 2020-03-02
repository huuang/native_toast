import 'dart:async';

import 'package:flutter/services.dart';

enum ToastPosition {
  Top,
  Center,
  Bottom,
}

class NativeToast {
  static const MethodChannel _channel = const MethodChannel('com.huuang.native_toast');

  static Future<void> showToast(String message, {ToastPosition position = ToastPosition.Center}) async {
    _channel.invokeMethod("showToast", {"message": message, "position": position.index});
  }
}
